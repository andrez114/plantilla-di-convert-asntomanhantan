package com.coppel.config;

import com.coppel.entities.CatBodegas;
import com.coppel.repository.bodega.CatBodegasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "com.coppel.repository.bodega",
        entityManagerFactoryRef = "bodegaEntityManagerFactory")
public class BodegaConfig {
    //Bodega
    @Bean(name = "dsBodega")
    @ConfigurationProperties(prefix = "bodega.datasource")
    public DataSource conexionBodegaDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "jdbcBodega")
    @Autowired
    public NamedParameterJdbcTemplate jdbcTemplateBodega(@Qualifier("dsBodega") DataSource dsBodega){
        return new NamedParameterJdbcTemplate(dsBodega);
    }

    @Bean(name = "bodegaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean bodegaEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dsBodega") DataSource dataSource) {
        return builder.dataSource(conexionBodegaDataSource())
                .packages(CatBodegas.class)
                .build();
    }


}
