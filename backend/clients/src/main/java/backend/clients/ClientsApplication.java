package backend.clients;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class ClientsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientsApplication.class, args);
	}

}
