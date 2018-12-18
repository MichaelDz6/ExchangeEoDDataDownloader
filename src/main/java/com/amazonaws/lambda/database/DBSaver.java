package com.amazonaws.lambda.database;

import com.amazonaws.lambda.entities.EoDOHLC;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public class DBSaver {


    private static DataSource dataSource = null;



    public static void saveBatch(EoDOHLC[] values) throws SQLException {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("stock_eod_ohlc");

        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(values);
        jdbcInsert.executeBatch(batch);
    }


    public static void setup() throws IllegalArgumentException{
        DriverManagerDataSource managerDataSource = new DriverManagerDataSource();

        String jdbcURL = System.getenv("JDBC_URL");
        if(jdbcURL == null || jdbcURL.isEmpty()){
            throw new IllegalArgumentException("Missing JDBC_URL paramter");
        }
        managerDataSource.setUrl(jdbcURL);

        String user = System.getenv("DB_USER");
        if(user == null || user.isEmpty()){
            throw new IllegalArgumentException("Missing DB_USER paramter");
        }
        managerDataSource.setUsername(user);

        String password = System.getenv("DB_PASSWORD");
        if(password == null || password.isEmpty()){
            throw new IllegalArgumentException("Missing DB_PASSWORD paramter");
        }
        managerDataSource.setPassword(password);

        dataSource = managerDataSource;
    }


}
