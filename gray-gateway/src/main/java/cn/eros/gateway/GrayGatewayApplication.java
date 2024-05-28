package cn.eros.gateway;

import cn.eros.gateway.config.LoadBalancerConfiguration;
import cn.eros.gateway.properties.EnvProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;

/**
 * @author Eros
 * @since 2024/5/9 11:43
 */
@EnableDiscoveryClient
@EnableConfigurationProperties({EnvProperties.class})
@LoadBalancerClients(defaultConfiguration = LoadBalancerConfiguration.class)
@SpringBootApplication
public class GrayGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GrayGatewayApplication.class, args);
    }
}
