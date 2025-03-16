package org.ricky.core.doc.domain;

import org.ricky.core.doc.domain.es.EsFile;

import java.io.IOException;
import java.util.List;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/16
 * @className DocSearchRepository
 * @desc
 */
public interface DocSearchRepository {

    void upload(EsFile esFile) throws IOException;

    /**
     * 查询所有包含关键词的文档
     *
     * @param keyword 关键词
     * @return 文档集合
     */
    List<Doc> search(String keyword) throws IOException;

    // /**
    //  * 在给定文档ID集合中查询所有包含关键词的文档
    //  *
    //  * @param keyword  String
    //  * @param docIdSet 文档ID集合
    //  * @return result
    //  * @throws IOException exception
    //  * TODO 修改PageVO的名字
    //  */
    // Map<String, List<PageVO>> search(String keyword, Set<String> docIdSet) throws IOException;

    // BaseApiResult getWordStat() throws IOException;

}
