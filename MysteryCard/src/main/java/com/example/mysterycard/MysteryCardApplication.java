package com.example.mysterycard;

import com.example.mysterycard.configuration.MomoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class MysteryCardApplication {

    public static void main(String[] args) {
        SpringApplication.run(MysteryCardApplication.class, args);
    }

}
