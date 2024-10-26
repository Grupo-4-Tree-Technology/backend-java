
package br.com.technology.tree;

import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;

import static br.com.technology.tree.Log.*;
import br.com.technology.tree.banco.*;
import br.com.technology.tree.bucket.*;
import static br.com.technology.tree.leituraPlanilha.LeitorExcel.processarAcidentes;

public class Main {
    public static void main(String[] args) throws IOException {
        // Instanciando o cliente S3 via S3Provider
        S3Client s3Client = new S3Provider().getS3Client();
        String bucketName = "s3-tree-technology-bucket";

        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getConnection();

        // *************************************
        // *   Listando todos os buckets      *
        // *************************************
        // DESCOMENTE A LINHA ABAIXO CASO QUEIRA LISTAR TODOS OS BUCKETS:

        S3Bucket.listAllBuckets(connection, s3Client);
        System.out.println();

        // *************************************
        // *   Listando objetos do bucket      *
        // *************************************
        // DESCOMENTE A LINHA ABAIXO CASO QUEIRA LISTAR OBJETOS DE UM BUCKET:
        System.out.println(coletarDataHoraAtual());
        S3Bucket.listBucketObjects(connection, s3Client, bucketName);
        System.out.println();
        System.out.println();

        // BancoDeDados.createTables(connection);

        processarAcidentes(connection, s3Client);
        enviarArquivosParaS3(s3Client, bucketName);
    }
}
