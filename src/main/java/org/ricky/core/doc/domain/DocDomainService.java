package org.ricky.core.doc.domain;

import cn.hutool.crypto.SecureUtil;
import lombok.RequiredArgsConstructor;
import org.ricky.common.exception.MyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.ricky.common.exception.ErrorCodeEnum.FILE_ALREADY_EXISTS;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DocDomainService
 * @desc
 */
@Service
@RequiredArgsConstructor
public class DocDomainService {

    private final DocRepository docRepository;
    private final DocFactory docFactory;

    public Doc upload(MultipartFile file) throws IOException {
        String fileMd5 = SecureUtil.md5(file.getInputStream());
        // 若md5已存在，拒接保存
        if (docRepository.existsByMd5(fileMd5)) {
            throw new MyException(FILE_ALREADY_EXISTS, "文档已存在",
                    Map.of("originalFilename", file.getOriginalFilename()));
        }

        return docFactory.file2doc(file, fileMd5, null);
    }
}
