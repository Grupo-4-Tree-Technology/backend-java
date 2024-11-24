package br.com.technology.tree.bucket;

import br.com.technology.tree.ConexaoBase;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class S3Provider extends ConexaoBase {

    private S3Client s3Client;

    @Override
    public void conectar() {
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Override
    public void desconectar() {
        if (s3Client != null) {
            s3Client.close();
        }
    }

//    @Override
//    public S3Client getS3Client() {
//        return s3Client;
//    }

    // ========================================================================
    //        Descomentar abaixo e comentar o método "getS3Client" acima
    //        somente para teste local (inserir as variáveis de ambiente):
    // ========================================================================


    private final AwsSessionCredentials credentials;

    public S3Provider() {
        this.credentials = AwsSessionCredentials.create(
                System.getenv("AWS_ACCESS_KEY_ID"),
                System.getenv("AWS_SECRET_ACCESS_KEY"),
                System.getenv("AWS_SESSION_TOKEN")
        );
    }

    @Override
    public S3Client getS3Client() {
        return S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(() -> credentials)
                .build();
    }


}
