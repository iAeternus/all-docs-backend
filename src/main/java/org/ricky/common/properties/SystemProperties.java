package org.ricky.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className SystemProperties
 * @desc 全文档系统的设置，由管理员进行配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "all-docs.config")
public class SystemProperties {

    private Boolean userUpload;

    private Boolean adminReview = true;

    private Boolean prohibitedWord;

    private Boolean userRegistry;

    private String initialUsername;

    private String initialPassword;

    private Boolean coverAdmin;

    /**
     * 是否启用流控
     */
    private Boolean enableLimitRate = false;

}
