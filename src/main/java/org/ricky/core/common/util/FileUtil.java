package org.ricky.core.common.util;

import lombok.extern.slf4j.Slf4j;
import org.ricky.common.exception.MyException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.ricky.common.exception.ErrorCodeEnum.FILE_DELETE_FAILED;
import static org.ricky.core.common.util.ValidationUtil.isNotBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/16
 * @className FileUtil
 * @desc
 */
@Slf4j
public class FileUtil {

    /**
     * 判断文件是否存在
     *
     * @param filepath 文件路径
     * @return true=存在 false=不存在
     */
    public static boolean isExists(String filepath) {
        return new File(filepath).exists();
    }

    /**
     * 判断文件是否不存在
     *
     * @param filepath 文件路径
     * @return true=不存在 false=存在
     */
    public static boolean isNotExists(String filepath) {
        return !new File(filepath).exists();
    }

    /**
     * 删除文件
     *
     * @param filepath 文件路径
     */
    public static void deleteFile(String filepath) {
        try {
            Files.delete(Paths.get(filepath));
        } catch (IOException e) {
            log.error("删除文件失败，路径：{}", filepath, e);
            throw new MyException(FILE_DELETE_FAILED, "删除文件失败", Map.of("filepath", filepath));
        }
    }

    /**
     * 若文件存在则删除，否则什么都不做
     *
     * @param filepath 文件路径
     */
    public static void deleteFileIfExists(String filepath) {
        if (isNotBlank(filepath) && isExists(filepath)) {
            deleteFile(filepath);
        }
    }

    /**
     * 构建文件路径
     *
     * @param filename 文件名
     * @param suffix   扩展名
     * @return 构建好的文件路径
     */
    public static String buildPath(String filename, String suffix) {
        return "./" + filename + suffix;
    }

    public static String getLastPart(String filepath) {
        String[] split = filepath.split("/");
        if (split.length == 0) {
            return "";
        }
        return split[split.length - 1];
    }

}
