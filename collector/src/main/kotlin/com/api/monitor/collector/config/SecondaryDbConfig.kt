package com.api.monitor.collector.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(
    basePackages = ["com.api.monitor.collector.repository.secondary"],
    mongoTemplateRef = "secondaryMongoTemplate"
)
class SecondaryDbConfig {

    @Bean(name = ["secondaryMongoProperties"])
    @ConfigurationProperties(prefix = "spring.data.mongodb.secondary")
    fun getSecondaryProperties(): MongoProperties = MongoProperties()

    @Bean(name = ["secondaryMongoFactory"])
    fun secondaryMongoFactory(@Qualifier("secondaryMongoProperties") mongoProperties: MongoProperties): MongoDatabaseFactory {
        return SimpleMongoClientDatabaseFactory(mongoProperties.uri)
    }

    @Bean(name = ["secondaryMongoTemplate"])
    fun secondaryMongoTemplate(@Qualifier("secondaryMongoFactory") mongoFactory: MongoDatabaseFactory): MongoTemplate {
        return MongoTemplate(mongoFactory)
    }
}