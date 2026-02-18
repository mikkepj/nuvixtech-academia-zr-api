package com.nuvixtech.courses.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Normalizes JDBC URLs that may contain embedded credentials in the form
 * jdbc:postgresql://user:password@host/db (libpq format), which the
 * PostgreSQL JDBC driver does not accept. Extracts and removes the inline
 * credentials before configuring HikariCP.
 */
@Configuration
public class DataSourceConfig {

    private static final Pattern EMBEDDED_CREDENTIALS =
            Pattern.compile("jdbc:postgresql://([^:@]+):([^@]+)@(.+)");

    @Value("${spring.datasource.url}")
    private String rawUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(cleanUrl(rawUrl));
        config.setUsername(resolveUsername(rawUrl));
        config.setPassword(resolvePassword(rawUrl));
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }

    private String cleanUrl(String url) {
        Matcher m = EMBEDDED_CREDENTIALS.matcher(url);
        if (m.matches()) {
            return "jdbc:postgresql://" + m.group(3);
        }
        return url;
    }

    private String resolveUsername(String url) {
        Matcher m = EMBEDDED_CREDENTIALS.matcher(url);
        return m.matches() ? m.group(1) : username;
    }

    private String resolvePassword(String url) {
        Matcher m = EMBEDDED_CREDENTIALS.matcher(url);
        return m.matches() ? m.group(2) : password;
    }
}
