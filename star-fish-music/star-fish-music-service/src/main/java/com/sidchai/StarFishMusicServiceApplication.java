package com.sidchai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.sidchai.music"})
public class StarFishMusicServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarFishMusicServiceApplication.class, args);
    }

}
