package sptech.school.bucket;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class S3Bucket {

    public static void createNewBucket(S3Client s3Client, String bucketName) {
        try {
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.createBucket(createBucketRequest);
            System.out.println("Bucket criado com sucesso: " + bucketName);
        } catch (S3Exception e) {
            System.err.println("Erro ao criar o bucket: " + e.getMessage());
        }
    }

    public static void listAllBuckets(S3Client s3Client) {
        try {
            List<Bucket> buckets = s3Client.listBuckets().buckets();
            System.out.println("Lista de buckets:");
            for (Bucket bucket : buckets) {
                System.out.println("- " + bucket.name());
            }
        } catch (S3Exception e) {
            System.err.println("Erro ao listar buckets: " + e.getMessage());
        }
    }

    public static void listBucketObjects(S3Client s3Client, String bucketName) {
        try {
            ListObjectsRequest listObjects = ListObjectsRequest.builder()
                    .bucket(bucketName)
                    .build();

            List<S3Object> objects = s3Client.listObjects(listObjects).contents();
            System.out.println("Objetos no bucket " + bucketName + ":");
            for (S3Object object : objects) {
                System.out.println("- " + object.key());
            }
        } catch (S3Exception e) {
            System.err.println("Erro ao listar objetos no bucket: " + e.getMessage());
        }
    }

    public static void uploadFiles(S3Client s3Client, String bucketName) {
        try {
            String uniqueFileName = UUID.randomUUID().toString();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .build();

            File file = new File("file.txt");
            s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));

            System.out.println("Arquivo '" + file.getName() + "' enviado com sucesso com o nome: " + uniqueFileName);
        } catch (S3Exception e) {
            System.err.println("Erro ao fazer upload do arquivo: " + e.getMessage());
        }
    }

    public static void downloadFiles(S3Client s3Client, String bucketName) {
        try {
            String downloadDirectory = System.getProperty("user.dir") + File.separator + "filesBucketS3" + File.separator;
            new File(downloadDirectory).mkdirs();

            List<S3Object> objects = s3Client.listObjects(ListObjectsRequest.builder().bucket(bucketName).build()).contents();
            for (S3Object object : objects) {
                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(object.key())
                        .build();

                File downloadFile = new File(downloadDirectory + object.key());

                InputStream inputStream = s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream());
                Files.copy(inputStream, downloadFile.toPath());
                System.out.println("Arquivo baixado: " + object.key());
            }
        } catch (IOException | S3Exception e) {
            String mensagem = "Erro ao fazer download do(s) arquivo(s): " + e.getMessage();
            System.err.println(mensagem);
        }
    }

    public static void deleteBucketObject(S3Client s3Client, String bucketName) {
        try {
            String objectKeyToDelete = "identificador-do-arquivo";
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKeyToDelete)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);

            System.out.println("Objeto deletado com sucesso: " + objectKeyToDelete);
        } catch (S3Exception e) {
            System.err.println("Erro ao deletar objeto: " + e.getMessage());
        }
    }
}
