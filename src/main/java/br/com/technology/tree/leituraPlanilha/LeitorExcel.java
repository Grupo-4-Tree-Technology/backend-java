package br.com.technology.tree.leituraPlanilha;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.technology.tree.leituraPlanilha.Acidente;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static br.com.technology.tree.Log.*;

public class LeitorExcel {

    public List<Acidente> extrairAcidentes(String nomeArquivo, InputStream arquivo, JdbcTemplate connection) {
        try {
            System.out.println("\nIniciando leitura do arquivo %s".formatted(nomeArquivo));

            // Criando um objeto Workbook a partir do arquivo recebido
            Workbook workbook;
            if (nomeArquivo.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(arquivo);
            } else {
                workbook = new HSSFWorkbook(arquivo);
            }

            Sheet sheet = workbook.getSheetAt(0);

            List<Acidente> acidentesExtraidos = new ArrayList<>();

            // Iterando sobre as linhas da planilha

            Integer contadorLinhas = 0;
            Integer linhaInicial = 0;

            List<Acidente> acidentesDoBanco = connection.query("SELECT id FROM acidente_transito", new BeanPropertyRowMapper<>(Acidente.class));

            if (!acidentesDoBanco.isEmpty()) {
                linhaInicial = acidentesDoBanco.getLast().getId();
            }

            int[] indicesColuna = new int[]{0, 1, 3, 4, 7, 8, 11, 13, 24};

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    System.out.println("\nLendo cabeçalho");

                    for (int i : indicesColuna) {
                        String coluna = row.getCell(i).getStringCellValue();
                        System.out.println("Coluna " + i + ": " + coluna);

                    }

                    System.out.println("--------------------");

                } else if (contadorLinhas < 10) {
                    String colunaID = String.valueOf((int) row.getCell(0).getNumericCellValue());

                    if (Integer.parseInt(colunaID) <= linhaInicial) {
                        continue;
                    }
                    // Extraindo valor das células e criando objeto Acidente
                    System.out.println("Lendo linha " + row.getRowNum() + 1);

                    Acidente acidente = new Acidente();

                    if (row.getCell(4).getStringCellValue().equals("SP")) {

                        String faseDia = row.getCell(11).getStringCellValue();
                        System.out.println("Fase do dia: " + faseDia);
                        if (faseDia.equals("Plena Noite") || faseDia.equals("Amanhecer") ||
                            faseDia.equals("Pleno dia") || faseDia.equals("Anoitecer")) {

                            String condicaoMetereologica = row.getCell(13).getStringCellValue();
                            System.out.println("Condicao Metereológica: " + condicaoMetereologica);
                            if (condicaoMetereologica.equals("Céu Claro") || condicaoMetereologica.equals("Chuva") ||
                                condicaoMetereologica.equals("Sol") || condicaoMetereologica.equals("Nublado") ||
                                condicaoMetereologica.equals("Garoa/Chuvisco")) {

                                String condicaoFormatada = condicaoMetereologica.equals("Céu Claro") ? condicaoMetereologica.replace('é', 'e') : condicaoMetereologica;

                                acidente.setId((int) row.getCell(0).getNumericCellValue());
                                acidente.setData(row.getCell(1).getLocalDateTimeCellValue().toString().substring(0, 10));
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
                                        (id, data, horario, uf, municipio, causa_acidente, fase_dia, condicao_metereologica, qtd_veiculos_envolvidos)
                                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                                        acidente.getId(), acidente.getData(), acidente.getHorario(), acidente.getUf(), acidente.getMunicipio(),
                                        acidente.getCausa(), acidente.getFase_dia(), acidente.getCondicao_metereologica(), acidente.getQuantidade_veiculos());
                                    System.out.println("\u001B[32m" + coletarDataHoraAtual());
                                    System.out.println("Inserção dos dados na tabela acidente_transito feita com sucesso!" + "\u001B[0m");
                                    registrarLog("Inserção dos dados na tabela acidente_transito feita com sucesso!");
                                } catch (S3Exception e) {
                                    System.out.println("\u001B[31mErro ao inserir na tabela acidente_transito: " + e.getMessage() + "\u001B[0m");
                                    registrarErro("Erro ao inserir na tabela acidente_transito: " + e.getMessage());
                                }
                                contadorLinhas++;
                            }
                        }
                    }
                } else {
                    break;
                }
            }
            linhaInicial = acidentesExtraidos.getLast().getId();
            System.out.println("""

                    \u001B[32m[INFO] Leitura do arquivo finalizada com sucesso!
                    \u001B[0m""");

            // Fechando o workbook após a leitura
            workbook.close();

            return acidentesExtraidos;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // IOException serve para gerar exceções de entrada e saída
    public static void processarAcidentes(JdbcTemplate connection) throws IOException {

        String nomeArquivo = "acidentes - datatran2024.xlsx";
        String downloadDirectory = System.getProperty("user.dir") + File.separator + "filesBucketS3" + File.separator + nomeArquivo;

        Path caminho = Path.of(downloadDirectory);
        try (InputStream arquivo = Files.newInputStream(caminho)) {

            LeitorExcel leitorExcel = new LeitorExcel();
            List<Acidente> acidentesExtraidos = leitorExcel.extrairAcidentes(nomeArquivo, arquivo, connection);

            int contadorLinhas = 0;

            System.out.println("Acidentes extraídos:");
            for (Acidente acidente : acidentesExtraidos) {
                System.out.println(acidente);
                contadorLinhas++;
            }

            System.out.println("Acidentes em SP coletados: " + contadorLinhas);
        } catch (IOException e) {
            System.out.println("\u001B[31m" + "[ERROR] " + "Erro ao processar o arquivo: " + e.getMessage() + "\u001B[33m");
            System.out.println("""
                    1. Tente verificar o nome do arquivo.
                    2. Tente verificar o diretório de download (downloadDirectory).
                    """+ "\u001B[0m");
        }
    }

    private LocalDate converterDate(Date data) {
        return data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
