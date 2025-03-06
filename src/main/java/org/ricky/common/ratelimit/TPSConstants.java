package org.ricky.common.ratelimit;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/12/30
 * @className TPSConstants
 * @desc
 */
public interface TPSConstants {

    int MINIMUM_TPS = 1;
    int EXTREMELY_LOW_TPS = 5;
    int LOW_TPS = 10;
    int NORMAL_TPS = 20;
    int HIGH_TPS = 50;
    int EXTREMELY_HIGH_TPS = 100;

}
