package br.com.technology.tree;

import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;

public abstract class ConexaoBase {

    public abstract void conectar(); // Método abstrato para conexão

    public void desconectar() {
    }

    // Métodos genéricos para retornar conexões, que podem ser sobrepostos em outras classes filhas.
    public JdbcTemplate getJdbcTemplate() {
        return null;
    }

    public S3Client getS3Client() {
        return null;
    }
}
