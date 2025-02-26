package org.ricky.common.password;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/11
 * @className PasswordConfiguration
 * @desc
 */
@Configuration
public class PasswordConfiguration {

    @Bean
    public IPasswordEncoder passwordEncoder() {
        return new SHAPasswordEncoder();
    }

}
