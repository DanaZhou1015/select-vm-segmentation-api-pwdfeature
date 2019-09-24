package com.acxiom.ams.config;

import org.hibernate.SessionFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

/**
 * Created by cldong on 12/5/2017.
 */
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement  //开启事务支持
public class JpaConfig {
    @Bean
    public SessionFactory sessionFactory(EntityManagerFactory factory) {
        if (factory.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("Factory is not a hibernate factory");
        }
        return factory.unwrap(SessionFactory.class);
    }
}
