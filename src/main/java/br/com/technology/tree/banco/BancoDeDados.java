package br.com.technology.tree.banco;

import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

import static br.com.technology.tree.Log.*;

public class BancoDeDados {
    public static void createTables(JdbcTemplate connection) {

        try {
            connection.execute("""
                CREATE TABLE IF NOT EXISTS empresa (
                id 			 	INT AUTO_INCREMENT,
                razao_social 	VARCHAR(100) NOT NULL UNIQUE,
                nome_fantasia 	VARCHAR(45) NOT NULL,
                email 			VARCHAR(345) NOT NULL UNIQUE,
                cnpj 			VARCHAR(18) NOT NULL UNIQUE,
                senha 			VARCHAR(16) NOT NULL,
                                
                PRIMARY KEY pk_empresa (id)
                );
                """);
            System.out.println("\u001B[32mCriação da tabela empresa feita com sucesso!" + "\u001B[0m");
            registrarLog("Criação da tabela empresa feita com sucesso!");

            inserirLog(connection, "SUCESSO", "Criação da tabela", "Criação da tabela empresa feita com sucesso!");
        } catch (S3Exception e) {
            System.out.println("\u001B[31mErro ao criar a tabela empresa: " + e.getMessage() + "\u001B[0m");
            registrarErro("Erro ao criar a tabela empresa: " + e.getMessage());

            inserirLog(connection, "ERRO", "Falha na criação da tabela empresa", e.getMessage());
        }

        try {
            connection.execute("""
                CREATE TABLE IF NOT EXISTS veiculo (
                placa 		VARCHAR(7),
                fkEmpresa 	INT NOT NULL,
                                
                PRIMARY KEY pk_placa (placa),
                FOREIGN KEY ForeignKey_fkEmpresa (fkEmpresa) REFERENCES empresa (id)
                );
                """);
            System.out.println("\u001B[32mCriação da tabela veiculo feita com sucesso!" + "\u001B[0m");
            registrarLog("Criação da tabela veiculo feita com sucesso!");

            inserirLog(connection, "SUCESSO", "Criação da tabela", "Criação da tabela veiculo feita com sucesso!");
        } catch (S3Exception e) {
            System.out.println("\u001B[31mErro ao criar a tabela veiculo: " + e.getMessage() + "\u001B[0m");
            registrarErro("Erro ao criar a tabela veiculo: " + e.getMessage());

            inserirLog(connection, "ERRO", "Falha na criação da tabela veiculo", e.getMessage());
        }

        try {
            connection.execute("""
                CREATE TABLE IF NOT EXISTS rota (
                id 				INT NOT NULL AUTO_INCREMENT,
                ponto_partida 	VARCHAR(100) NOT NULL,
                ponto_destino 	VARCHAR(100) NOT NULL,
                                
                PRIMARY KEY pk_rota (id)
                );
                """);
            System.out.println("\u001B[32mCriação da tabela rota feita com sucesso!" + "\u001B[0m");
            registrarLog("Criação da tabela rota feita com sucesso!");

            inserirLog(connection, "SUCESSO", "Criação da tabela", "Criação da tabela rota feita com sucesso!");
        } catch (S3Exception e) {
            System.out.println("\u001B[31mErro ao criar a tabela rota: " + e.getMessage() + "\u001B[0m");
            registrarErro("Erro ao criar a tabela rota: " + e.getMessage());

            inserirLog(connection, "ERRO", "Falha na criação da tabela rota", e.getMessage());
        }

        try {
            connection.execute("""
                CREATE TABLE IF NOT EXISTS rotas_has_veiculo (
                fkRota 	INT NOT NULL,
                fkPlaca VARCHAR(7) NOT NULL,
                                
                PRIMARY KEY pk_rotas_has_veiculo (fkRota, fkPlaca),
                FOREIGN KEY ForeignKey_fkRota (fkRota) REFERENCES rota (id),
                FOREIGN KEY ForeignKey_fkPlaca (fkPlaca) REFERENCES veiculo (placa)
                );
                """);
            System.out.println("\u001B[32mCriação da tabela rotas_has_veiculo feita com sucesso!" + "\u001B[0m");
            registrarLog("Criação da tabela rotas_has_veiculo feita com sucesso!");

            inserirLog(connection, "SUCESSO", "Criação da tabela", "Criação da tabela rotas_has_veiculo feita com sucesso!");
        } catch (S3Exception e) {
            System.out.println("\u001B[31mErro ao criar a tabela rotas_has_veiculo: " + e.getMessage() + "\u001B[0m");
            registrarErro("Erro ao criar a tabela rotas_has_veiculo: " + e.getMessage());

            inserirLog(connection, "ERRO", "Falha na criação da tabela rotas_has_veiculo", e.getMessage());
        }

        try {
            connection.execute("""
                CREATE TABLE IF NOT EXISTS ruas_intermediarias (
                id 		INT NOT NULL AUTO_INCREMENT,
                rua 	VARCHAR(100) NOT NULL,
                ordem 	INT NOT NULL,
                fkRota 	INT NOT NULL,
                                
                PRIMARY KEY pk_ruas_intermediarias (id),
                FOREIGN KEY ForeignKey_fkRota_ruas (fkRota) REFERENCES rota (id)
                );
                """);
            System.out.println("\u001B[32mCriação da tabela ruas_intermediarias feita com sucesso!" + "\u001B[0m");
            registrarLog("Criação da tabela ruas_intermediarias feita com sucesso!");

            inserirLog(connection, "SUCESSO", "Criação da tabela", "Criação da tabela ruas_intermediarias feita com sucesso!");
        } catch (S3Exception e) {
            System.out.println("\u001B[31mErro ao criar a tabela ruas_intermediarias: " + e.getMessage() + "\u001B[0m");
            registrarErro("Erro ao criar a tabela ruas_intermediarias: " + e.getMessage());

            inserirLog(connection, "ERRO", "Falha na criação da tabela ruas_intermediarias", e.getMessage());
        }

        try {
            connection.execute("""
                CREATE TABLE IF NOT EXISTS acidente_transito (
                id 						INT,
                data 					DATE NOT NULL,
                dia_semana				VARCHAR(45) NOT NULL,
                horario 				TIME NOT NULL,
                uf 						CHAR(2) NOT NULL,
                municipio 				VARCHAR(100) NOT NULL,
                causa_acidente 			VARCHAR(100) NOT NULL,
                fase_dia 				VARCHAR(45) NOT NULL,
                condicao_metereologica 	VARCHAR(45) NOT NULL,
                qtd_veiculos_envolvidos INT NOT NULL,
                                
                PRIMARY KEY pk_evento_transito (id),
                CONSTRAINT CHECK (fase_dia IN ('Plena Noite', 'Amanhecer', 'Pleno dia', 'Anoitecer')),
                CONSTRAINT CHECK (condicao_metereologica IN ('Ceu Claro', 'Chuva', 'Sol', 'Nublado', 'Garoa/Chuvisco'))
                );
                """);
            System.out.println("\u001B[32mCriação da tabela acidente_transito feita com sucesso!" + "\u001B[0m");
            registrarLog("Criação da tabela acidente_transito feita com sucesso!");

            inserirLog(connection, "SUCESSO", "Criação da tabela", "Criação da tabela acidente_transito feita com sucesso!");
        } catch (S3Exception e) {
            System.out.println("\u001B[31mErro ao criar a tabela acidente_transito: " + e.getMessage() + "\u001B[0m");
            registrarErro("Erro ao criar a tabela acidente_transito: " + e.getMessage());

            inserirLog(connection, "ERRO", "Falha na criação da tabela acidente_transito", e.getMessage());
        }

        try {
            connection.execute("""
                CREATE TABLE IF NOT EXISTS log (
                id INT NOT NULL AUTO_INCREMENT,
                data_hora TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'Data e hora da leitura com milissegundos',
                status VARCHAR(45) NOT NULL,
                titulo TEXT NOT NULL,
                descricao TEXT NOT NULL,
                                
                PRIMARY KEY pk_log (id)
                );
                """);
            System.out.println("\u001B[32mCriação da tabela log feita com sucesso!" + "\u001B[0m");
            registrarLog("Criação da tabela log feita com sucesso!");

            inserirLog(connection, "SUCESSO", "Criação da tabela", "Criação da tabela log feita com sucesso!");
        } catch (S3Exception e) {
            System.out.println("\u001B[31mErro ao criar a tabela log: " + e.getMessage() + "\u001B[0m");
            registrarErro("Erro ao criar a tabela log: " + e.getMessage());

            inserirLog(connection, "ERRO", "Falha na criação da tabela log", e.getMessage());
        }
    }
}
