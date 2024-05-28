package cn.eros.gateway.balancer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eros
 * @since 2024/5/14 15:26
 */
public class VersionGrayLoadBalancer implements ReactorServiceInstanceLoadBalancer {
    private final Logger logger = LoggerFactory.getLogger(VersionGrayLoadBalancer.class);

    private final String serviceId;
    private final AtomicInteger position;
    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    public VersionGrayLoadBalancer(String serviceId, ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider) {
        this(serviceId, new Random().nextInt(1000), serviceInstanceListSupplierProvider);
    }

    public VersionGrayLoadBalancer(String serviceId, int seed, ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider) {
        this.serviceId = serviceId;
        this.position = new AtomicInteger(seed);
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = this.serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
        HttpHeaders headers = ((RequestDataContext) request.getContext()).getClientRequest().getHeaders();

        return supplier.get(request).next()
                .map(serviceInstances -> this.processInstanceResponse(supplier, serviceInstances, headers));
    }

    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier,
                                                              List<ServiceInstance> serviceInstances,
                                                              HttpHeaders headers) {
        if (serviceInstances.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("No servers available for service: {}", serviceId);
            }
            return new EmptyResponse();
        }

        var reqVersions = headers.get("version");

        if (!CollectionUtils.isEmpty(reqVersions) && StringUtils.hasText(reqVersions.getFirst())) {
            String reqVersion = reqVersions.getFirst();

            List<ServiceInstance> targetInstances = serviceInstances.stream()
                    .filter(instance -> reqVersion.equals(instance.getMetadata().get("version")))
                    .toList();

            if (!targetInstances.isEmpty()) {
                return this.getInstanceResponse(targetInstances);
            }
        }

        return this.getInstanceResponse(serviceInstances);
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {


        // 自定义
        return this.getClusterRobinResponse(instances);
        // 默认RoundRobinLoadBalancer
        // return this.getRoundRobinInstance(instances);
        // 随机
        //return this.getRandomInstance(instances);
    }

    /**
     * 使用随机算法 参考{link
     * {@link org.springframework.cloud.loadbalancer.core.RandomLoadBalancer}}
     *
     * @param instances 实例
     * @return {@link Response }<{@link ServiceInstance }>
     */
    private Response<ServiceInstance> getRandomInstance(List<ServiceInstance> instances) {
        int index = ThreadLocalRandom.current().nextInt(instances.size());
        ServiceInstance instance = instances.get(index);
        return new DefaultResponse(instance);
    }

    /**
     * 使用RoundRobin机制获取节点
     *
     * @param instances 实例
     * @return {@link Response }<{@link ServiceInstance }>
     */
    private Response<ServiceInstance> getRoundRobinInstance(List<ServiceInstance> instances) {
        // 每一次计数器都自动+1，实现轮询的效果
        int pos = this.position.incrementAndGet() & Integer.MAX_VALUE;
        ServiceInstance instance = instances.get(pos % instances.size());
        return new DefaultResponse(instance);
    }

    /**
     * 自定义的负载策略
     *
     * @param instances
     * @return
     */
    private Response<ServiceInstance> getClusterRobinResponse(List<ServiceInstance> instances) {
        // 简单的轮训算法，在集群环境中，会出现不平衡的问题。
        return getRoundRobinInstance(instances);
    }
}
