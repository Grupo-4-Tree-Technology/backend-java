package br.com.technology.tree.banco;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class DBConnectionProvider {

    private final DataSource dataSource;

    public DBConnectionProvider() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:mysql://container-bd-treetech:3306/TreeTechnology");
        basicDataSource.setUsername("root"); // Deve alterar pro que deseja (provavelmente vai ser root).
        basicDataSource.setPassword("urubu100"); // Deve alterar pro que deseja...

        this.dataSource = basicDataSource;
    }

    public JdbcTemplate getConnection() {
        return new JdbcTemplate(dataSource);
    }
}
