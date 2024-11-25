package br.com.technology.tree;

import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;

public class Log {

    private static final String LOG_DIRECTORY = "logs/";

    private void criarDiretorioLog() {
        File directory = new File(LOG_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    private String obterCaminhoDoArquivoDiario() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DAY_OF_MONTH, 0);

        String simulatedDate = sdf.format(cal.getTime());
        return LOG_DIRECTORY + "logs_" + simulatedDate + ".log";
    }

    public void registrarLog(String mensagem) {
        criarDiretorioLog();
        String dataHoraAtual = coletarDataHoraAtual();
        String logMessage = dataHoraAtual + " [SUCESSO] " + mensagem;
        String logFilePath = obterCaminhoDoArquivoDiario();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao registrar log: " + e.getMessage());
        }
    }

    public void registrarErro(String mensagem) {
        criarDiretorioLog();
        String dataHora = coletarDataHoraAtual();
        String logMessage = dataHora + " [ERRO] " + mensagem;
        String logFilePath = obterCaminhoDoArquivoDiario();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao registrar log: " + e.getMessage());
        }
    }

    public void registrarArquivosLidos(String mensagem) {
        criarDiretorioLog();
        String logMessage = coletarDataHoraAtual() + " " + mensagem;
        String logFilePath = LOG_DIRECTORY + "arquivos_lidos.log";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao registrar log: " + e.getMessage());
        }
    }

    public void inserirLog(JdbcTemplate connection, String status, String nomeArquivo, String titulo, String descricao) {
        try {
            connection.update("""
            INSERT INTO log (status, arquivo_lido, titulo, descricao)
            VALUES (?, ?, ?, ?)""", status, nomeArquivo, titulo, descricao);
            System.out.println();
            System.out.println("Log inserido com sucesso!" + "\u001B[0m");
            System.out.println("===========================");
        } catch (Exception e) {
            System.out.println();
            System.out.println("Erro ao inserir log: " + e.getMessage() + "\u001B[0m");
            System.out.println("===========================");
            System.out.println();
        }
    }

    // Fiz uma sobrecarga do método inserirLog (Overload)
    public void inserirLog(JdbcTemplate connection, String status, String titulo, String descricao) {
        try {
            connection.update("""
            INSERT INTO log (status, titulo, descricao)
            VALUES (?, ?, ?)""", status, titulo, descricao);
            System.out.println();
            System.out.println("Log inserido com sucesso!" + "\u001B[0m");
            System.out.println("===========================");
        } catch (Exception e) {
            System.out.println();
            System.out.println("Erro ao inserir log: " + e.getMessage() + "\u001B[0m");
            System.out.println("===========================");
            System.out.println();
        }
    }

    public void enviarLogsParaS3(S3Client s3Client, String bucketName) {
        File logDirectory = new File(LOG_DIRECTORY);
        File[] logFiles = logDirectory.listFiles();

        if (logFiles != null && logFiles.length > 0) {
            for (File logFile : logFiles) {
                if (logFile.isFile()) {
                    try {

                        Path path = logFile.toPath();
                        PutObjectRequest putRequest = PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(LOG_DIRECTORY + logFile.getName())
                                .build();

                        s3Client.putObject(putRequest, path);
                        System.out.println("Arquivo enviado para S3: " + logFile.getName());
                    } catch (Exception e) {
                        System.err.println("Erro ao enviar arquivo " + logFile.getName() + ": " + e.getMessage());
                    }
                }
            }
        } else {
            System.out.println("Nenhum arquivo encontrado na pasta de logs.");
        }
    }

    public String coletarDataHoraAtual() {
        /*
         * Utilizei o LocalDateTime.now() para pegar a data + hora atual.
         * Utilizei o System.currentTimeMillis() para pegar os milissegundos atuais e concatenar com o horário.
         */

        LocalDateTime dataHoraAtual = LocalDateTime.now();
        String dataHoraAtualString = dataHoraAtual.toString();

        Long milissegundosAtuais = System.currentTimeMillis();
        String milisegundosString = milissegundosAtuais.toString();

        String dataAtual = dataHoraAtualString.substring(8, 10) + '/' + dataHoraAtualString.substring(5, 7) + '/' + dataHoraAtualString.substring(0, 4);
        String horaAtual = String.format("%s" + ":" + "%s", dataHoraAtualString.substring(11, 19), milisegundosString.substring(0, 3));

        return "[" + dataAtual + " " + horaAtual + "]";
    }
}
