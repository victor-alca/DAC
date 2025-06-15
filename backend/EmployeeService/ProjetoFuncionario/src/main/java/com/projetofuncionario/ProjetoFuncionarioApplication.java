package com.projetofuncionario;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class ProjetoFuncionarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetoFuncionarioApplication.class, args);
	}

}
