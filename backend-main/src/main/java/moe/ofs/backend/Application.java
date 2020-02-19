package moe.ofs.backend;

import moe.ofs.backend.box.BoxOfFlyableUnit;
import moe.ofs.backend.controllers.ExportObjectController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

	public static ConfigurableApplicationContext ctx;

	public static void main(String[] args) {
		ctx = SpringApplication.run(Application.class, args);

		ControlPanelShutdownObservable controlPanelShutdownObservable = () -> ctx.close();
		controlPanelShutdownObservable.register();

		ExportObjectController controller = ctx.getBean("exportObjectController", ExportObjectController.class);
		System.out.println(controller.getDesc());

		System.out.println(BoxOfFlyableUnit.box);
	}

}
