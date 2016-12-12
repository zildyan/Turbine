package hr.modulit.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@PropertySource("classpath:email.properties")
public class AppConfig {

    @Autowired
    private Environment env;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public JavaMailSenderImpl javaMailSenderImpl() {
        return new JavaMailSenderImpl() {{
            setHost(env.getRequiredProperty("smtp.host"));
            setPort(env.getRequiredProperty("smtp.port", Integer.class));
            setProtocol(env.getRequiredProperty("smtp.protocol"));
            setUsername(env.getRequiredProperty("smtp.username"));
            setPassword(env.getRequiredProperty("smtp.password"));
            setJavaMailProperties(getJavaMailProperties());
        }};
    }

    private Properties getJavaMailProperties() {
        return new Properties() {{
                put("mail.smtp.auth", true);
                put("mail.smtp.starttls.enable", true);
            }};
    }

}
