package com.chatapp; //this is the base package for my app

import org.springframework.boot.SpringApplication; // spring boot runner
import org.springframework.boot.autoconfigure.SpringBootApplication; // auto config magic

//@SpringBootApplication basically says: this is my main spring boot class.
//it will scan components, configs, etc. inside this package.
@SpringBootApplication
public class ChatApplication {

    //main() is the entry point, like normal Java.
    //when i run this class, it starts an embedded server (tomcat by default).
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args); // boot up the app
    }
}