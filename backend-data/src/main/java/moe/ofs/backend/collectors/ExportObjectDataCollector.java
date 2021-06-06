package moe.ofs.backend.collectors;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.common.UpdatableService;
import moe.ofs.backend.domain.dcs.poll.ExportObject;
import moe.ofs.backend.pollservices.DataUpdateBundle;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@Slf4j
@Component
public class ExportObjectDataCollector {

    private final UpdatableService<ExportObject> exportObjectUpdatableService;

    public ExportObjectDataCollector(UpdatableService<ExportObject> exportObjectUpdatableService) {
        this.exportObjectUpdatableService = exportObjectUpdatableService;
    }

    @JmsListener(destination = "luap", containerFactory = "jmsQueueListenerContainerFactory",
            selector = "JMSType = 'export-data'")
    public void collect(TextMessage message) throws JMSException {
        String jsonMessage = message.getText();
        Gson gson = new Gson();
        DataUpdateBundle bundle = gson.fromJson(jsonMessage, DataUpdateBundle.class);
//        log.info(bundle.toString());

        String action = bundle.getAction();

        if ("create".equals(action)) {
            exportObjectUpdatableService.add(bundle.getData());
        } else if ("update".equals(action)) {
            exportObjectUpdatableService.update(bundle.getData());
        } else {  // delete
            exportObjectUpdatableService.remove(bundle.getData());
        }
    }
}
