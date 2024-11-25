
package br.com.technology.tree;

import br.com.technology.tree.leituraPlanilha.LeitorExcel;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;

import static br.com.technology.tree.Log.*;
import br.com.technology.tree.banco.*;
import br.com.technology.tree.bucket.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // Instanciando o cliente S3 via S3Provider
        S3Provider s3Provider = new S3Provider();
        s3Provider.conectar();

        S3Client s3Client = s3Provider.getS3Client();
        String bucketName = "s3-tree-technology-teste";

        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        dbConnectionProvider.conectar();
        JdbcTemplate connection = dbConnectionProvider.getJdbcTemplate();

        // *************************************
        // *   Listando todos os buckets      *
        // *************************************
        S3Bucket s3Bucket = new S3Bucket();

        s3Bucket.listAllBuckets(connection, s3Client);
        System.out.println();

        // *************************************
        // *   Listando objetos do bucket      *
        // *************************************
        Log log = new Log();
        System.out.println(log.coletarDataHoraAtual());
        s3Bucket.listBucketObjects(connection, s3Client, bucketName);
        System.out.println();
        System.out.println();

        LeitorExcel leitorExcel = new LeitorExcel(s3Bucket, dbConnectionProvider);

        leitorExcel.processarAcidentes(s3Client, bucketName);
        log.enviarLogsParaS3(s3Client, bucketName);

        Slack slack = new Slack();
        slack.executarNotificacoes(connection);
    }
}
