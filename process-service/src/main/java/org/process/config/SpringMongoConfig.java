package org.process.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories(basePackages = "org.process.repository")
@PropertySource("classpath:application.properties")
@EnableMongoAuditing
public class SpringMongoConfig extends AbstractMongoConfiguration {

	@Value("${spring.data.mongodb.database}")
    private String dbName;
 
    @Value("${spring.data.mongodb.authentication-database}")
    private String authDB;
 
    @Value("${spring.data.mongodb.host}")
    private String host;
 
    @Value("${spring.data.mongodb.port}")
    private String port;

	@Override
	public String getDatabaseName() {
		return this.dbName;
	}

	@Override
	@Bean
	public MongoClient mongoClient() {
		//ServerAddress seed =new ServerAddress(this.host, Integer.parseInt(this.port.trim()));
        //MongoCredential userCredential = MongoCredential.createCredential(this.username, this.authDB, this.password.toCharArray());
		//MongoClientOptions options = MongoClientOptions.builder().socketTimeout(2000).build();
        
        return new MongoClient(this.host);
	}
}