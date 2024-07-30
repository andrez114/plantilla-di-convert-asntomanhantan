package com.coppel.config;


import com.coppel.entities.AsnToManhattan;
import com.coppel.entities.OriginalOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "com.coppel.repository.asn",
        entityManagerFactoryRef = "AsnEntityManagerFactory")
public class AsnToManhattanConfig {


    @Primary
    @Bean(name = "dsAsnManhattan")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource conexionAsnDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "jdbcAsnManhattan")
    @Autowired
    public NamedParameterJdbcTemplate jdbcTemplateAsn(@Qualifier("dsAsnManhattan") DataSource dsAsnManhattan){
        return new NamedParameterJdbcTemplate(dsAsnManhattan);
    }

    @Primary
    @Bean(name = "AsnEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean AsnEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dsAsnManhattan") DataSource dataSource) {
        return builder.dataSource(conexionAsnDataSource())
                .packages(AsnToManhattan.class)
                .packages(OriginalOrder.class)
                .build();
    }

}
