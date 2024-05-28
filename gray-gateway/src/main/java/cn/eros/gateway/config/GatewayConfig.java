package cn.eros.gateway.config;

import cn.eros.gateway.filters.GrayRouteFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Eros
 * @since 2024/5/24 15:56
 */
@Configuration
public class GatewayConfig {
    @Bean
    public GrayRouteFilter getGrayRouteFilter() {
        return new GrayRouteFilter();
    }
}
