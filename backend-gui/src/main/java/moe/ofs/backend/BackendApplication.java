package moe.ofs.backend;

import moe.ofs.backend.handlers.ControlPanelShutdownObservable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BackendApplication {

	public static ConfigurableApplicationContext ctx;

	public static void main(String[] args) {
		ctx = SpringApplication.run(BackendApplication.class, args);

		ControlPanelApplication.applicationContext = ctx;

		ControlPanelShutdownObservable controlPanelShutdownObservable = () -> ctx.close();
		controlPanelShutdownObservable.register();
	}

}
