package hr.modulit.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@PropertySource({ "classpath:/persistence-derby.properties" })
@EnableJpaRepositories(basePackages = { "hr.modulit.persistence.repos" })
public class PersistenceConfig {

    @Autowired
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
       return new LocalContainerEntityManagerFactoryBean(){{
           setDataSource(dataSource());
           setPackagesToScan("hr.modulit.persistence.models");
           setJpaVendorAdapter(new HibernateJpaVendorAdapter());
           setJpaProperties(additionalProperties());
       }};
    }

    private Properties additionalProperties() {
        return new Properties(){{
            setProperty("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
            setProperty("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));
            setProperty("hibernate.format_sql", env.getRequiredProperty("hibernate.format_sql"));
            setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        }};
    }

    @Bean
    public DataSource dataSource() {
        return new DriverManagerDataSource() {{
            setDriverClassName(env.getRequiredProperty("persistence.jdbc.driver"));
            setUrl(env.getRequiredProperty("persistence.jdbc.url"));
            setUsername(env.getRequiredProperty("persistence.jdbc.user"));
            setPassword(env.getRequiredProperty("persistence.jdbc.password"));
        }};
    }

    @Bean
    public JpaTransactionManager transactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}