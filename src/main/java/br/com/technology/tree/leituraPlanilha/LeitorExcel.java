package br.com.technology.tree.leituraPlanilha;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static br.com.technology.tree.Log.*;
import static br.com.technology.tree.bucket.S3Bucket.getFirstObjectInSpecificBucket;

public class LeitorExcel {

    public List<Acidente> extrairAcidentes(String nomeArquivo, InputStream arquivoS3Stream, JdbcTemplate connection) {
        try {
            System.out.println("\nIniciando leitura do arquivo %s".formatted(nomeArquivo));

            String consulta = "SELECT COUNT(*) FROM log WHERE arquivo_lido = ?";
            Integer arquivoJaProcessado = connection.queryForObject(consulta, Integer.class, nomeArquivo);

            if (arquivoJaProcessado != null && arquivoJaProcessado > 0) {
                String registroArquivoProcessado = String.format("Arquivo '%s' já foi processado anteriormente. Processo interrompido.%n", nomeArquivo);
                System.out.printf(registroArquivoProcessado);

                registrarErro(registroArquivoProcessado);
                try {
                    connection.update("""
                    INSERT INTO log (status, arquivo_lido, titulo, descricao)
                    VALUES (?, ?, ?, ?)""", "ERRO", nomeArquivo, "Arquivo já processado", registroArquivoProcessado);
                    System.out.println();
                    System.out.println("Log inserido com sucesso!" + "\u001B[0m");
                    System.out.println("===========================");
                } catch (Exception e) {
                    System.out.println();
                    System.out.println("Erro ao inserir log: " + e.getMessage() + "\u001B[0m");
                    System.out.println("===========================");

                }
                return new ArrayList<>();
            }

            Workbook workbook;
            if (nomeArquivo.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(arquivoS3Stream);
            } else {
                workbook = new HSSFWorkbook(arquivoS3Stream);
            }

            Sheet sheet = workbook.getSheetAt(0);

            List<Acidente> acidentesExtraidos = new ArrayList<>();

            Integer contadorLinhas = 0;
            Integer linhaInicial = 0;

            int[] indicesColuna = new int[]{0, 1, 2, 3, 4, 7, 8, 11, 13, 24};

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    System.out.println("\nLendo cabeçalho");

                    for (int i : indicesColuna) {
                        String coluna = row.getCell(i).getStringCellValue();
                        System.out.println("Coluna " + i + ": " + coluna);

                    }

                    System.out.println("--------------------");

                } else {

                    System.out.println("Lendo linha " + row.getRowNum() + 1);

                    Acidente acidente = new Acidente();

                    if (row.getCell(4).getStringCellValue().equals("SP")) {

                        String faseDia = row.getCell(11).getStringCellValue();
                        if (faseDia.equals("Plena Noite") || faseDia.equals("Amanhecer") ||
                            faseDia.equals("Pleno dia") || faseDia.equals("Anoitecer")) {

                            String condicaoMetereologica = row.getCell(13).getStringCellValue();
                            if (condicaoMetereologica.equals("Céu Claro") || condicaoMetereologica.equals("Chuva") ||
                                condicaoMetereologica.equals("Sol") || condicaoMetereologica.equals("Nublado") ||
                                condicaoMetereologica.equals("Garoa/Chuvisco")) {

                                String condicaoFormatada = condicaoMetereologica.equals("Céu Claro") ? condicaoMetereologica.replace('é', 'e') : condicaoMetereologica;

                                acidente.setId((int) row.getCell(0).getNumericCellValue());
                                acidente.setData(row.getCell(1).getLocalDateTimeCellValue().toString().substring(0, 10));
                                acidente.setDia_semana(row.getCell(2).getStringCellValue());
                                acidente.setHorario(row.getCell(3).getLocalDateTimeCellValue().toString().substring(11, 16));
                                acidente.setUf(row.getCell(4).getStringCellValue());
                                acidente.setMunicipio(row.getCell(7).getStringCellValue());
                                acidente.setCausa(row.getCell(8).getStringCellValue());
                                acidente.setFase_dia(row.getCell(11).getStringCellValue());
                                acidente.setCondicao_metereologica(condicaoFormatada);
                                acidente.setQuantidade_veiculos((int) row.getCell(24).getNumericCellValue());

                                acidentesExtraidos.add(acidente);

                                try {
                                    connection.update("""
                                        INSERT INTO acidente_transito\s
                                        (id, data, dia_semana, horario, uf, municipio, causa_acidente, fase_dia, condicao_metereologica, qtd_veiculos_envolvidos)
                                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                                        acidente.getId(), acidente.getData(), acidente.getDia_semana(), acidente.getHorario(), acidente.getUf(), acidente.getMunicipio(),
                                        acidente.getCausa(), acidente.getFase_dia(), acidente.getCondicao_metereologica(), acidente.getQuantidade_veiculos());
                                    System.out.println("\u001B[32m" + coletarDataHoraAtual());
                                    System.out.println(acidente);
                                    System.out.println("Insercao dos dados na tabela acidente_transito feita com sucesso!" + "\u001B[0m");
                                    registrarLog("Insercao dos dados na tabela acidente_transito feita com sucesso!");

                                    inserirLog(connection, "SUCESSO", nomeArquivo, "Insert bem sucedido", "Insercao dos dados na tabela acidente_transito feita com sucesso.");
                                    System.out.println("\u001B[32m" + coletarDataHoraAtual());
                                } catch (S3Exception e) {
                                    System.out.println("\u001B[31m" + coletarDataHoraAtual());
                                    System.out.println("Erro ao inserir na tabela acidente_transito: " + e.getMessage() + "\u001B[0m");
                                    registrarErro("Erro ao inserir na tabela acidente_transito: " + e.getMessage());

                                    inserirLog(connection, "ERRO", nomeArquivo, "Erro ao inserir na tabela acidente_transito", e.getMessage());

                                }
                                contadorLinhas++;
                            }
                        }
                    }
                }
            }
            System.out.println("""

                    \u001B[32m[INFO] Leitura do arquivo finalizada com sucesso!
                    \u001B[0m""");

            registrarArquivosLidos( "O arquivo " + nomeArquivo + " foi lido com sucesso.");

            workbook.close();

            return acidentesExtraidos;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void processarAcidentes(JdbcTemplate connection, S3Client s3Client) throws IOException {

        String bucketName = "s3-tree-technology-teste";

        String nomeArquivo = getFirstObjectInSpecificBucket(bucketName, s3Client);

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(nomeArquivo)
                    .build();

            try (ResponseInputStream<?> arquivoS3Stream = s3Client.getObject(getObjectRequest)) {
                LeitorExcel leitorExcel = new LeitorExcel();
                List<Acidente> acidentesExtraidos = leitorExcel.extrairAcidentes(nomeArquivo, arquivoS3Stream, connection);

                System.out.println("Acidentes extraídos: " + acidentesExtraidos.size());
            }
        } catch (IOException e) {
            System.out.println("\u001B[31m" + "[ERROR] " + "Erro ao processar o arquivo no S3: " + e.getMessage() + "\u001B[33m");
            System.out.println("""
                    1. Verifique o nome do arquivo.
                    2. Verifique se o arquivo está disponível no bucket.
                    """+ "\u001B[0m");
        }
    }
}
