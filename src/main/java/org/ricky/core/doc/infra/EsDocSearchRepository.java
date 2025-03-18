package org.ricky.core.doc.infra;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocSearchRepository;
import org.ricky.core.doc.domain.es.EsFile;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

import static org.ricky.common.constants.ConfigConstant.DOC_INDEX_NAME;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/16
 * @className EsDocSearchRepository
 * @desc
 */
@Repository
@RequiredArgsConstructor
public class EsDocSearchRepository implements DocSearchRepository {

    private final RestHighLevelClient restHighLevelClient;

    @Override
    public void upload(EsFile esFile) throws IOException {
        IndexRequest indexRequest = new IndexRequest(DOC_INDEX_NAME);
        // 上传同时，使用attachment pipeline 进行提取文件
        indexRequest.source(JSON.toJSONString(esFile), XContentType.JSON);
        indexRequest.setPipeline("attachment");
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    @Override
    public List<Doc> search(String keyword) throws IOException {
        return null;
    }
}
