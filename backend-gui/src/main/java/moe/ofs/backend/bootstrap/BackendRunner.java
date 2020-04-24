package moe.ofs.backend.bootstrap;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.ControlPanelApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BackendRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Control Panel GUI");

//        new Thread(() -> javafx.application.Application.launch(ControlPanelApplication.class)).start();

    }
}
