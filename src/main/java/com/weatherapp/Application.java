package com.weatherapp;

import com.weatherapp.model.Role;
import com.weatherapp.dao.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application-secret.properties")
public class Application {

  public static void main(String[] args) {

    SpringApplication.run(Application.class, args);
  }

  @Bean
  public CommandLineRunner demo(RoleRepository roleRepository) {
    return (args) -> {
      Role role = new Role();
      role.setName("ROLE_ADMIN");
      roleRepository.save(role);
    };
  }
}
