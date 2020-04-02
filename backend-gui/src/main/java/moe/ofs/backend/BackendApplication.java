package moe.ofs.backend;

import javafx.application.Application;
import moe.ofs.backend.handlers.ControlPanelShutdownObservable;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.spring.SpringFxWeaver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

//	public static ConfigurableApplicationContext ctx;

	public static void main(String[] args) {
//		ctx = SpringApplication.run(BackendApplication.class, args);

//		ControlPanelApplication.applicationContext = ctx;

//		ControlPanelShutdownObservable controlPanelShutdownObservable = () -> ctx.close();
//		controlPanelShutdownObservable.register();

		Application.launch(ControlPanelApplication.class, args);
	}

	@Bean
	public FxWeaver fxWeaver(ConfigurableApplicationContext applicationContext) {
		return new SpringFxWeaver(applicationContext);
	}


}
