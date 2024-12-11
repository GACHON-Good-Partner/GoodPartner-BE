package goodpartner;

import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;

@Configuration
public class GatewayInitialRoutesConfig {

    @Bean
    public ApplicationRunner initialRoutes(RouteDefinitionWriter routeDefinitionWriter, ApplicationContext applicationContext) {
        return args -> {
            // users 라우트 정의
            RouteDefinition usersRoute = new RouteDefinition();
            usersRoute.setId("users");
            usersRoute.setUri(URI.create("http://user-BLUE:8080"));
            PredicateDefinition usersPredicate = new PredicateDefinition();
            usersPredicate.setName("Path");
            usersPredicate.addArg("pattern", "/users/**");
            usersRoute.setPredicates(Collections.singletonList(usersPredicate));

            // chats 라우트 정의
            RouteDefinition chatsRoute = new RouteDefinition();
            chatsRoute.setId("chats");
            chatsRoute.setUri(URI.create("http://chat-BLUE:8080"));
            PredicateDefinition chatsPredicate = new PredicateDefinition();
            chatsPredicate.setName("Path");
            chatsPredicate.addArg("pattern", "/chats/**");
            chatsRoute.setPredicates(Collections.singletonList(chatsPredicate));

            // RouteDefinitionWriter를 통해 초기 라우트 추가 (block()을 사용해서 동기화)
            Mono<Void> saveMono = routeDefinitionWriter.save(Mono.just(usersRoute))
                    .then(routeDefinitionWriter.save(Mono.just(chatsRoute)));

            // block()으로 실제 저장이 완료되길 대기
            saveMono.block();

            // 라우트가 모두 추가되었으므로 RefreshRoutesEvent 발행
            applicationContext.publishEvent(new RefreshRoutesEvent(this));
        };
    }
}