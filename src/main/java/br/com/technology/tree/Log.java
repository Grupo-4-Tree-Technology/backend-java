package br.com.technology.tree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class Log {

    private static final String LOG_DIRECTORY = "logs/";
    private static final String LOG_FILE_PATH = "historico.log";

    private static void createLogDirectory() {
        File directory = new File(LOG_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public static void registrarLog(String mensagem) {
        createLogDirectory();
        String dataHoraAtual = coletarDataHoraAtual();
        String logMessage = dataHoraAtual + " [SUCESSO] " + mensagem;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_DIRECTORY + LOG_FILE_PATH, true))) {
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

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao registrar log: " + e.getMessage());
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
