package ru.megy.config;


import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "ru.megy.repository")
@EnableTransactionManagement
@ComponentScan(basePackages="ru.megy.repository")
@ConfigurationProperties("jpa")
public class JpaConfig {
    private static final Logger logger = LoggerFactory.getLogger(JpaConfig.class);

    @Autowired
    private DataSource dataSource;

    @NotNull
    private String dialect;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan("ru.megy.repository.entity");
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties jpaProperties = new Properties();
        jpaProperties.put(Environment.DIALECT, dialect);
        jpaProperties.put(Environment.HBM2DDL_AUTO, false);
        jpaProperties.put(Environment.SHOW_SQL, false);
        jpaProperties.put(Environment.FORMAT_SQL, false);
        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }
}
