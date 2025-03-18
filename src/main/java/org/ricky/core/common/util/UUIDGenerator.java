package org.ricky.core.common.util;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/9
 * @className UUIDGenerator
 * @desc Util class for generating unique IDs in Base64 format based on UUID.
 */
public class UUIDGenerator {

    /**
     * 编码器
     */
    private static final Base64.Encoder encoder = Base64.getUrlEncoder();

    /**
     * 生成UUID
     *
     * @return UUID
     */
    public static String newShortUUID() {
        UUID uuid = UUID.randomUUID();
        byte[] src = ByteBuffer.wrap(new byte[16])
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits())
                .array();
        return encoder.encodeToString(src).substring(0, 22);
    }

}
