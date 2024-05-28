package cn.eros.gateway.config;

import cn.eros.gateway.balancer.VersionGrayLoadBalancer;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @author Eros
 * @since 2024/5/24 17:03
 */
public class LoadBalancerConfiguration {

    @Bean
    public ReactorLoadBalancer<ServiceInstance> grayLoadBalancer(Environment environment,
                                                                 LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new VersionGrayLoadBalancer(name, loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class));
    }
}
