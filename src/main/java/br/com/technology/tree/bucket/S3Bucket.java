package br.com.technology.tree.bucket;

import br.com.technology.tree.Log;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

import static br.com.technology.tree.Log.*;

public class S3Bucket {
    private final Log log;

    public S3Bucket() {
        this.log = new Log();
    }

    public String getFirstObjectInSpecificBucket(S3Client s3Client, String bucketName) {
        ListObjectsRequest listObjects = ListObjectsRequest.builder()
                .bucket(bucketName)
                .build();

        List<S3Object> objects = s3Client.listObjects(listObjects).contents();
        return objects.getFirst().key(); // Retorna o primeiro objeto do bucket (na teoria tem que ser a planilha)
    }

    public void listAllBuckets(JdbcTemplate connection, S3Client s3Client) {
        try {
            List<Bucket> buckets = s3Client.listBuckets().buckets();
            System.out.println("Lista de buckets:");
            for (Bucket bucket : buckets) {
                System.out.println("- " + bucket.name());
            }
            this.log.registrarLog("Listagem de todos os buckets com sucesso.");

            this.log.inserirLog(connection, "SUCESSO", "Listagem dos buckets", "Listagem de todos os buckets com sucesso.");
        } catch (S3Exception e) {
            System.err.println("Erro ao listar buckets: " + e.getMessage());
            this.log.registrarErro("Erro ao listar todos os buckets: " + e.getMessage());

            this.log.inserirLog(connection, "ERRO", "Falha ao listar todos os buckets", e.getMessage());
        }
    }

    public void listBucketObjects(JdbcTemplate connection, S3Client s3Client, String bucketName) {
        try {
            ListObjectsRequest listObjects = ListObjectsRequest.builder()
                    .bucket(bucketName)
                    .build();

            List<S3Object> objects = s3Client.listObjects(listObjects).contents();
            System.out.println("Objetos no bucket " + bucketName + ":");
            for (S3Object object : objects) {
                System.out.println("- " + object.key());
            }
            this.log.registrarLog("Listagem de todos os objetos do bucket " + bucketName + " com sucesso.");

            this.log.inserirLog(connection, "SUCESSO", "Listagem bem sucedida", "Listagem de todos os objetos do bucket " + bucketName + " com sucesso.");
        } catch (S3Exception e) {
            System.err.println("Erro ao listar objetos no bucket " + bucketName + ": " + e.getMessage());
            this.log.registrarErro("Erro ao listar objetos no bucket " + bucketName + ": " + e.getMessage());

            this.log.inserirLog(connection, "ERRO", "Falha ao listar objetos no bucket" + bucketName, e.getMessage());
        }
    }

}
