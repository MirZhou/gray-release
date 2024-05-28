package cn.eros.gateway.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.List;

/**
 * @author Eros
 * @since 2024/5/9 17:28
 */
@ConfigurationProperties(prefix = "env")
@RefreshScope
public class EnvProperties {
    private String productionVersion;
    private String grayVersion;
    private List<String> grayUsers;

    public String getProductionVersion() {
        return productionVersion;
    }

    public void setProductionVersion(String productionVersion) {
        this.productionVersion = productionVersion;
    }

    public String getGrayVersion() {
        return grayVersion;
    }

    public void setGrayVersion(String grayVersion) {
        this.grayVersion = grayVersion;
    }

    public List<String> getGrayUsers() {
        return grayUsers;
    }

    public void setGrayUsers(List<String> grayUsers) {
        this.grayUsers = grayUsers;
    }
}
