package com.myapp.app.config;

import com.myapp.app.frameworks.Config;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@ComponentScan("com.myapp.app.frameworks")
public class InfraConfig {

    @Bean
    public DataSource dataSource(Config config) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(config.getHibernateDriver());
        ds.setUrl(config.getDbUrl());
        ds.setUsername(config.getDbUser());
        ds.setPassword(config.getDbPassword());
        return ds;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource, Config config) {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        factory.setDataSource(dataSource);

        factory.setPackagesToScan(
                "com.myapp.app.domain.entities",
                "com.myapp.app.frameworks.dataAccess.hibernate.persistence.entities"
        );

        Properties properties = new Properties();
        properties.put("hibernate.dialect", config.getHibernateDialect());
        properties.put("hibernate.hbm2ddl.auto", config.getHibernateHbm2ddlAuto());
        factory.setHibernateProperties(properties);

        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }
}