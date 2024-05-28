package cn.eros.micro.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Eros
 * @since 2024/5/9 15:13
 */
@EnableDiscoveryClient
@SpringBootApplication
public class MicroApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicroApplication.class, args);
    }
}
