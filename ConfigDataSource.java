package com.NguyenHuuTrong.LoginProject.Config;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class ConfigDataSource {
	@Value("${data.datasource.url}")
	private String urlDb;
	
	@Value("${data.datasource.username}")
	private String userName;
	
	@Value("${data.datasource.password}")
	private String password;
	
	@Value("${data.datasource.driver-class-name}")
	private String Driver;
	
	public String secreckey(){
		   String url = "E:\\Workspace\\recsecKey_MaHoaDsuLieu.txt";
		   String secrecKey ="";
	        // Đọc dữ liệu từ File với Scanner
	        FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(url);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        Scanner scanner = new Scanner(fileInputStream);

	        try {
	        	secrecKey = scanner.nextLine();
	        } finally {
	            try {
	                scanner.close();
	                fileInputStream.close();
	            } catch (IOException ex) {
	                Logger.getLogger(BufferedReader.class.getName())
	                                .log(Level.SEVERE, null, ex);
	            }
	        }
	        return secrecKey;
	    }
	private String secreckey = secreckey();
	
	@Bean(name="dataSource")
	protected DataSource dataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(AES.decrypt(urlDb, secreckey));
		hikariConfig.setUsername(AES.decrypt(userName, secreckey));
		hikariConfig.setPassword(AES.decrypt(password, secreckey));
		hikariConfig.setDriverClassName(AES.decrypt(Driver, secreckey));
		return new HikariDataSource(hikariConfig);
	}
	
	@Bean (name = "transactionManager")
	public DataSourceTransactionManager datasourceTransactionManeger() {
		return new DataSourceTransactionManager(dataSource());
	}
	
	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
		
		SqlSessionFactoryBean sqlsessionFactoryBean = new SqlSessionFactoryBean();
		sqlsessionFactoryBean.setDataSource(dataSource);
		
		try {
			sqlsessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:com/NguyenHuuTrong/LoginProject/Mapper/Sql/*.xml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sqlsessionFactoryBean.getObject();
	}
	
}
