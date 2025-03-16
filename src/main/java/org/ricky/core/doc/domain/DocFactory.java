package org.ricky.core.doc.domain;

import lombok.RequiredArgsConstructor;
import org.ricky.common.properties.SystemProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DocFactory
 * @desc
 */
@Component
@RequiredArgsConstructor
public class DocFactory {

    private final SystemProperties systemProperties;

    public Doc file2doc(MultipartFile file, String md5, String desc) {
        String name = handlePath(file.getOriginalFilename());
        Boolean adminReview = systemProperties.getAdminReview();
        return new Doc(name, file.getSize(), md5, file.getContentType(), desc, adminReview);
    }

    private String handlePath(String originalFilename) {
        String path = originalFilename.replace("\\", "/");
        if (!path.contains("/")) {
            return path;
        }

        String[] split = path.split("/");
        return split[split.length - 1];
    }
}
