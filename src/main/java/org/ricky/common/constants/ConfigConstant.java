package org.ricky.common.constants;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className ConfigConstant
 * @desc 整个系统需要的配置常数，统一配置避免出现不匹配的情况
 */
public interface ConfigConstant {

    String CHINA_TIME_ZONE = "Asia/Shanghai";

    String AUTHORIZATION = "Authorization";
    String BEARER = "Bearer ";

    String LOCAL_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    String EVENT_COLLECTION = "event";
    String USER_COLLECTION = "user";

    String USER_ID_PREFIX = "USR";

    String USER_CACHE = "user";

    String REDIS_DOMAIN_EVENT_CONSUMER_GROUP = "domain.event.group";
    String REDIS_WEBHOOK_CONSUMER_GROUP = "webhook.group";
    String REDIS_NOTIFICATION_CONSUMER_GROUP = "notification.group";

}
