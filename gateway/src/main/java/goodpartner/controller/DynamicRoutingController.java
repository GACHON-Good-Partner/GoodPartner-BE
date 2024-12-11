package goodpartner.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/gateway")
public class DynamicRoutingController {

    private final RouteDefinitionWriter routeDefinitionWriter;
    private final RouteDefinitionLocator routeDefinitionLocator;
    private final ApplicationContext applicationContext;

    @Value("${security.key}")
    private String validApiKey;

    @Autowired
    public DynamicRoutingController(RouteDefinitionWriter routeDefinitionWriter,
                                    RouteDefinitionLocator routeDefinitionLocator,
                                    ApplicationContext applicationContext) {
        this.routeDefinitionWriter = routeDefinitionWriter;
        this.routeDefinitionLocator = routeDefinitionLocator;
        this.applicationContext = applicationContext;
    }

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("health-check");
    }

    @PostMapping("/update-routes")
    public Mono<ResponseEntity<String>> updateRoutes(@RequestBody Map<String, String> environmentMap, @RequestHeader("API-KEY") String apiKey) {
        if(apiKey == null || apiKey.isEmpty() || !apiKey.equals(validApiKey)) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }
        String serviceType = environmentMap.get("SERVICE_TYPE");
        String environment = environmentMap.get("ENVIRONMENT");

        if (serviceType == null || environment == null) {
            return Mono.just(ResponseEntity.badRequest().body("SERVICE_TYPE and ENVIRONMENT are required"));
        }

        String serviceUrl;
        try {
            serviceUrl = determineServiceUrl(serviceType, environment);
        } catch (IllegalArgumentException e) {
            return Mono.just(ResponseEntity.badRequest().body(e.getMessage()));
        }

        return deleteRoute(serviceType)
                .then(addRoute(serviceType, serviceUrl))
                .then(Mono.defer(() -> {
                    // 라우트 변경 완료 후 RefreshRoutesEvent 발행
                    applicationContext.publishEvent(new RefreshRoutesEvent(this));
                    return Mono.just(ResponseEntity.ok(serviceType + " service routing updated to " + environment + " environment"));
                }))
                .onErrorResume(e -> {
                    log.error("Error updating routes", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error updating routes: " + e.getMessage()));
                });
    }

    private String determineServiceUrl(String serviceType, String environment) {
        switch (serviceType) {
            case "users":
                return environment.equalsIgnoreCase("BLUE") ? "http://localhost:8081" : "http://localhost:9081";
            case "chats":
                return environment.equalsIgnoreCase("BLUE") ? "http://localhost:8082" : "http://localhost:9082";
            default:
                throw new IllegalArgumentException("Unknown service type: " + serviceType);
        }
    }

    private Mono<Void> deleteRoute(String routeId) {
        return routeDefinitionLocator.getRouteDefinitions()
                .filter(routeDefinition -> routeDefinition.getId().equals(routeId))
                .singleOrEmpty() // 0개면 Empty Mono 반환, 1개면 해당 route 반환
                .flatMap(routeDefinition -> routeDefinitionWriter.delete(Mono.just(routeDefinition.getId())))
                .doOnSuccess(v -> log.info("Route deleted: {}", routeId))
                .onErrorResume(org.springframework.cloud.gateway.support.NotFoundException.class, e -> {
                    // Route가 없어서 발생하는 예외는 경고 정도로 처리하고 무시
                    log.warn("Route not found, so not deleted: {}", routeId);
                    return Mono.empty();
                })
                .onErrorResume(e -> {
                    log.error("Error deleting route: {}", routeId, e);
                    return Mono.empty();
                });
    }

    private Mono<Void> addRoute(String routeId, String uri) {
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(routeId);
        routeDefinition.setUri(URI.create(uri));

        PredicateDefinition predicate = new PredicateDefinition();
        predicate.setName("Path");
        predicate.addArg("pattern", "/" + routeId + "/**");
        routeDefinition.setPredicates(Collections.singletonList(predicate));

        return routeDefinitionWriter.save(Mono.just(routeDefinition))
                .doOnSuccess(unused -> log.info("Route added: {} with URI: {}", routeId, uri))
                .then() // 성공 시 Mono<Void> 반환
                .onErrorResume(e -> {
                    log.error("Error adding route: {}", routeId, e);
                    return Mono.empty(); // 에러 발생시 무시하고 진행
                });
    }
}
