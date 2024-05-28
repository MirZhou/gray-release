package cn.eros.gateway.filters;

import cn.eros.gateway.properties.EnvProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Eros
 * @since 2024/5/24 15:48
 */
@Component
@RefreshScope
public class GrayRouteFilter implements GlobalFilter, Ordered {
    @Autowired
    private EnvProperties envProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders header = response.getHeaders();
        header.add("Content-Type", "application/json; charset=UTF-8");

        List<String> grayUsers = request.getHeaders().get("userId");
        var version = this.envProperties.getProductionVersion();

        if (!CollectionUtils.isEmpty(grayUsers)) {
            String userId = grayUsers.getFirst();
            if (this.envProperties.getGrayUsers().contains(userId)) {
                version = this.envProperties.getGrayVersion();
            }
        }

        request.mutate().header("version", version);

        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        // 确保在路由之前执行
        return -1;
    }
}
