package org.ricky.common.ratelimit;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className RateLimiter
 * @desc 限流接口<br>
 * 建议采用以下策略：<br>
 * 最低访问量: 1TPS<br>
 * 极低访问量: 5TPS<br>
 * 较低访问量: 10TPS<br>
 * 正常访问量: 20TPS<br>
 * 较高访问量: 50TPS<br>
 * 极高访问量: 100TPS<br>
 */
public interface RateLimiter {

    /**
     * 将限流应用于请求
     *
     * @param uid UID
     * @param key 请求键-请求的唯一标识
     * @param tps 访问量
     */
    void applyFor(String uid, String key, int tps);

    /**
     * 将限流应用于请求
     *
     * @param key 请求键-请求的唯一标识
     * @param tps 访问量
     */
    void applyFor(String key, int tps);

}
