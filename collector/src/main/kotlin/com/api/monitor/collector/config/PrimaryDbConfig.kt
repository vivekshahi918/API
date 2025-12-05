package com.api.monitor.collector.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(
    basePackages = ["com.api.monitor.collector.repository.primary"],
    mongoTemplateRef = "primaryMongoTemplate"
)
class PrimaryDbConfig {

    @Primary
    @Bean(name = ["primaryMongoProperties"])
    @ConfigurationProperties(prefix = "spring.data.mongodb.primary")
    fun getPrimaryProperties(): MongoProperties = MongoProperties()

    @Primary
    @Bean(name = ["primaryMongoFactory"])
    fun primaryMongoFactory(@Qualifier("primaryMongoProperties") mongoProperties: MongoProperties): MongoDatabaseFactory {
        return SimpleMongoClientDatabaseFactory(mongoProperties.uri)
    }

    @Primary
    @Bean(name = ["primaryMongoTemplate"])
    fun primaryMongoTemplate(@Qualifier("primaryMongoFactory") mongoFactory: MongoDatabaseFactory): MongoTemplate {
        return MongoTemplate(mongoFactory)
    }
}