
package sptech.school;

import software.amazon.awssdk.services.s3.S3Client;

import sptech.school.bucket.S3Bucket;
import sptech.school.bucket.S3Provider;

import static sptech.school.bucket.Log.coletarDataHoraAtual;

public class Main {
    public static void main(String[] args) {
        // Instanciando o cliente S3 via S3Provider
        S3Client s3Client = new S3Provider().getS3Client();
        String bucketName = "s3-base-de-dados";

        // *************************************
        // *   Criando um novo bucket no S3    *
        // *************************************
        // DESCOMENTE A LINHA ABAIXO CASO QUEIRA CRIAR UM BUCKET:
        // S3Bucket.createNewBucket(s3Client, bucketName);

        // *************************************
        // *   Listando todos os buckets       *
        // *************************************
        // DESCOMENTE A LINHA ABAIXO CASO QUEIRA LISTAR TODOS OS BUCKETS:
        System.out.println(coletarDataHoraAtual());
        S3Bucket.listAllBuckets(s3Client);
        System.out.println();

        // *************************************
        // *   Listando objetos do bucket      *
        // *************************************
        // DESCOMENTE A LINHA ABAIXO CASO QUEIRA LISTAR OBJETOS DE UM BUCKET:
        System.out.println(coletarDataHoraAtual());
        S3Bucket.listBucketObjects(s3Client, bucketName);
        System.out.println();

        // *************************************
        // *   Fazendo upload de arquivo       *
        // *************************************
        // DESCOMENTE A LINHA ABAIXO CASO QUEIRA SUBIR UM ARQUIVO EM UM BUCKET:
        // S3Bucket.uploadFiles(s3Client, bucketName);

        // *************************************
        // *   Fazendo download de arquivos    *
        // *************************************
        // DESCOMENTE A LINHA ABAIXO CASO QUEIRA FAZER DOWNLOAD DE ARQUIVOS DE UM BUCKET:
        System.out.println(coletarDataHoraAtual());
        S3Bucket.downloadFiles(s3Client, bucketName);
        System.out.println();

        // *************************************
        // *   Deletando um objeto do bucket   *
        // *************************************
        // DESCOMENTE A LINHA ABAIXO CASO QUEIRA DELETAR UM OBJETO DE UM BUCKET:
        // S3Bucket.deleteBucketObject(s3Client, bucketName);
    }
}
