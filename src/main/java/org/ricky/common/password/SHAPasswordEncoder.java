package org.ricky.common.password;

import java.math.BigInteger;
import java.security.MessageDigest;

import static org.ricky.core.common.util.ValidationUtil.isBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className SHAPasswordEncoder
 * @desc 使用SHA算法的密码编码器
 */
public class SHAPasswordEncoder implements IPasswordEncoder {

    /**
     * SHA(Secure Hash Algorithm，安全散列算法），数字签名等密码学应用中重要的工具，
     * 被广泛地应用于电子商务等信息安全领域。虽然，SHA与MD5通过碰撞法都被破解了，
     * 但是SHA仍然是公认的安全加密算法，较之MD5更为安全
     */
    public static final String KEY_SHA = "SHA";

    @Override
    public String encode(CharSequence rawPassword) {
        if (isBlank(rawPassword)) {
            return "";
        }
        BigInteger sha;
        byte[] inputData = rawPassword.toString().getBytes();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(KEY_SHA);
            messageDigest.update(inputData);
            sha = new BigInteger(messageDigest.digest());
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        return sha.toString(32);
    }

}
