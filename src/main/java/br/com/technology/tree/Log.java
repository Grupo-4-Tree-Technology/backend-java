package br.com.technology.tree;

import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class Log {

    private static final String LOG_DIRECTORY = "logs/";

    private static void createLogDirectory() {
        File directory = new File(LOG_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    private static String obterCaminhoDoArquivoDiario() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DAY_OF_MONTH, 0);

        String simulatedDate = sdf.format(cal.getTime());
        return LOG_DIRECTORY + "logs_" + simulatedDate + ".log";
    }

    public static void registrarLog(String mensagem) {
        createLogDirectory();
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

    public static void registrarErro(String mensagem) {
        createLogDirectory();
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

    public static void inserirLog(JdbcTemplate connection, String status, String titulo, String descricao) {
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

    public static String coletarDataHoraAtual() {
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
