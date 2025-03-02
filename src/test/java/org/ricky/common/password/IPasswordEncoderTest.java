package org.ricky.common.password;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className IPasswordEncoderTest
 * @desc
 */
class IPasswordEncoderTest {

    private final IPasswordEncoder passwordEncoder = new SHAPasswordEncoder();

    @Test
    void should_match() {
        String rawPassword = "123456";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

}