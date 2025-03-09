package org.ricky.common.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className MongoConfig
 * @desc
 */
@Configuration
public class MongoConfig {

    /**
     * 获取配置文件中数据库信息
     */
    @Value("${spring.data.mongodb.database}")
    String db;

    /**
     * GridFSBucket用于打开下载流
     *
     * @param mongoClient MongoClient
     * @return GridFSBucket
     */
    @Bean
    public GridFSBucket getGridFsBucket(MongoClient mongoClient) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(db);
        return GridFSBuckets.create(mongoDatabase);
    }

    // TODO
    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory factory) {
        return new MongoTransactionManager(factory);
    }

}
