package com.tribalscale.test

import com.tribalscale.test.MoneyTransferApplication
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@EnableCaching
@SpringBootApplication
open class MoneyTransferApplication {

    @Bean
    open fun methodValidationPostProcessor(): MethodValidationPostProcessor {
        return MethodValidationPostProcessor()
    }

    @Bean
    open fun initializer(connectionFactory: ConnectionFactory?): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)
        initializer.setDatabasePopulator(ResourceDatabasePopulator(ClassPathResource("schema.sql")))
        return initializer
    }

    @Bean
    open fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(MoneyTransferApplication::class.java, *args)
        }
    }
}