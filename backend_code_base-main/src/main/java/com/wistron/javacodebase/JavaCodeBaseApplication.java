package com.wistron.javacodebase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class JavaCodeBaseApplication {

  public static void main(String[] args) {
    SpringApplication.run(JavaCodeBaseApplication.class, args);
  }
}
