package com.example.backend_siop.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.url}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket-name}")
    private String bucket;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    @SneakyThrows
    public Boolean minioBucketInitialise(MinioClient minioClient) {
        boolean existe = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build());
        if (!existe) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
        return true;
    }
}