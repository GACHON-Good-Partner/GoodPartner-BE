package goodpartner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class KeyFilter implements GlobalFilter {

    @Value("${security.key}")
    private String validApiKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 헤더에서 키 확인
        String apiKey = exchange.getRequest().getHeaders().getFirst("API-KEY");

        // 키가 유효하지 않으면 403 Forbidden 응답
        if (apiKey == null || !apiKey.equals(validApiKey)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        // 키가 유효하면 요청 계속 처리
        return chain.filter(exchange);
    }
}
