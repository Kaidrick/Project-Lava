package moe.ofs.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

	public static ConfigurableApplicationContext ctx;

	public static void main(String[] args) {
		ctx = SpringApplication.run(Application.class, args);

		System.out.println("Spring Boot application started");

		new Thread(() -> javafx.application.Application.launch(BackendMain.class)).start();
	}

}
