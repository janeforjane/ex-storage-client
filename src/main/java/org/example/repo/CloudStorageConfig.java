package org.example.repo;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudStorageConfig {

    @Value("${application.cloudstorage.login}")
    private String cloudLogin;

    @Value("${application.cloudstorage.password}")
    private String cloudPassword;


    @Bean
    public Sardine getCloudClient(){
        return SardineFactory.begin(cloudLogin, cloudPassword);
    }

}
