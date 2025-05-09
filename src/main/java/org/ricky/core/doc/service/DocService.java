package org.ricky.core.doc.service;

import org.ricky.core.common.domain.page.PageVO;
import org.ricky.core.doc.domain.dto.DocPageDTO;
import org.ricky.core.doc.domain.dto.RemoveDocDTO;
import org.ricky.core.doc.domain.dto.UpdateDocDTO;
import org.ricky.core.doc.domain.dto.UploadDocDTO;
import org.ricky.core.doc.domain.vo.DocVO;

import java.io.IOException;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DocService
 * @desc
 */
public interface DocService {
    String upload(UploadDocDTO dto) throws IOException;

    Boolean remove(RemoveDocDTO dto);

    Boolean update(UpdateDocDTO dto);

    DocVO getById(String docId);

    PageVO<DocVO> page(DocPageDTO dto);
}
