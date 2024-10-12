package sptech.school.bucket;

import java.time.LocalDateTime;

public class Log {
    public static String coletarDataHoraAtual() {
        /*
         * Utilizei o LocalDateTime.now() para pegar a data + hora atual.
         * Utilizei o System.currentTimeMillis() para pegar os milissegundos atuais e concatenar com o horário.
         * */

        LocalDateTime dataHoraAtual = LocalDateTime.now();
        String dataHoraAtualString = dataHoraAtual.toString();

        Long milissegundosAtuais = System.currentTimeMillis();
        String milisegundosString = milissegundosAtuais.toString();

        String dataAtual = dataHoraAtualString.substring(8, 10) + '/' + dataHoraAtualString.substring(5, 7) + '/' + dataHoraAtualString.substring(0, 4);
        String horaAtual = String.format("%s" + ":" + "%s", dataHoraAtualString.substring(11, 19), milisegundosString.substring(0, 3));

        return "[" + dataAtual + " " + horaAtual + "]";
    }
}
