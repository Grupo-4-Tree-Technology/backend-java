package br.com.technology.tree.banco;

import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

import static br.com.technology.tree.Log.registrarErro;
import static br.com.technology.tree.Log.registrarLog;

public class BancoDeDados {
    public static void createTables(JdbcTemplate connection) {

        try {
            connection.execute("""
                CREATE TABLE IF NOT EXISTS empresa (
                id 			 	INT AUTO_INCREMENT, -- PRIMARY KEY\s
                razao_social 	VARCHAR(100) NOT NULL UNIQUE,
                nome_fantasia 	VARCHAR(45) NOT NULL,
                email 			VARCHAR(345) NOT NULL UNIQUE,
                cnpj 			VARCHAR(18) NOT NULL UNIQUE,
                senha 			VARCHAR(45) NOT NULL,
                                
                PRIMARY KEY pk_empresa (id)
                );
                """);
            System.out.println("\u001B[32mCriação da tabela empresa feita com sucesso!" + "\u001B[0m");
            registrarLog("Criação da tabela empresa feita com sucesso!");
        } catch (S3Exception e) {
            System.out.println("\u001B[31mErro ao criar a tabela empresa: " + e.getMessage() + "\u001B[0m");
            registrarErro("Erro ao criar a tabela empresa: " + e.getMessage());
        }

        try {
            connection.execute("""
                CREATE TABLE IF NOT EXISTS veiculo (
                placa 		VARCHAR(7), -- PRIMARY KEY
                fkEmpresa 	INT NOT NULL,
                                
                PRIMARY KEY pk_placa (placa),
                FOREIGN KEY ForeignKey_fkEmpresa (fkEmpresa) REFERENCES empresa (id)
                );
                """);
            System.out.println("\u001B[32mCriação da tabela veiculo feita com sucesso!" + "\u001B[0m");
            registrarLog("Criação da tabela veiculo feita com sucesso!");
        } catch (S3Exception e) {
            System.out.println("\u001B[31mErro ao criar a tabela veiculo: " + e.getMessage() + "\u001B[0m");
            registrarErro("Erro ao criar a tabela veiculo: " + e.getMessage());
        }

        try {
            connection.execute("""
                CREATE TABLE IF NOT EXISTS rota (
                id 				INT NOT NULL AUTO_INCREMENT,
                data_hora 		DATETIME,
                ponto_partida 	VARCHAR(100),
                ponto_destino 	VARCHAR(100),
                tempo_estimado 	TIME,
                                
                PRIMARY KEY pk_rota (id)
                );
                """);
            System.out.println("\u001B[32mCriação da tabela rota feita com sucesso!" + "\u001B[0m");
            registrarLog("Criação da tabela rota feita com sucesso!");
        } catch (S3Exception e) {
            System.out.println("\u001B[31mErro ao criar a tabela rota: " + e.getMessage() + "\u001B[0m");
            registrarErro("Erro ao criar a tabela rota: " + e.getMessage());
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
        } catch (S3Exception e) {
            System.out.println("\u001B[31mErro ao criar a tabela rotas_has_veiculo: " + e.getMessage() + "\u001B[0m");
            registrarErro("Erro ao criar a tabela rotas_has_veiculo: " + e.getMessage());
        }

        try {
            connection.execute("""
                CREATE TABLE IF NOT EXISTS acidente_transito (
                id 						INT,
                data 					DATE NOT NULL,
                horario 				TIME NOT NULL,
                uf 						CHAR(2) NOT NULL,
                municipio 				VARCHAR(100) NOT NULL,
                causa_acidente 			VARCHAR(100) NOT NULL,
                fase_dia 				VARCHAR(45) NOT NULL,
                condicao_metereologica 	VARCHAR(45) NOT NULL,
                qtd_veiculos_envolvidos INT NOT NULL,
                                
                PRIMARY KEY pk_evento_transito (id),
                CONSTRAINT CHECK (fase_dia IN ('Plena Noite', 'Amanhecer', 'Pleno dia', 'Anoitecer')),
                CONSTRAINT CHECK (condicao_metereologica IN ('Céu Claro', 'Chuva', 'Sol', 'Nublado', 'Garoa/Chuvisco'))
                );
                """);
            System.out.println("\u001B[32mCriação da tabela acidente_transito feita com sucesso!" + "\u001B[0m");
            registrarLog("Criação da tabela acidente_transito feita com sucesso!");
        } catch (S3Exception e) {
            System.out.println("\u001B[31mErro ao criar a tabela acidente_transito: " + e.getMessage() + "\u001B[0m");
            registrarErro("Erro ao criar a tabela acidente_transito: " + e.getMessage());
        }
    }
}
