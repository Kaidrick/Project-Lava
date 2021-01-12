package moe.ofs.backend.function.motd.services.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import moe.ofs.backend.annotations.ListenLavaMessage;
import moe.ofs.backend.domain.dcs.poll.ExportObject;
import moe.ofs.backend.function.motd.services.MotdManageService;
import moe.ofs.backend.function.motd.services.MotdTransmitService;
import moe.ofs.backend.function.triggermessage.factories.MessageQueueFactory;
import moe.ofs.backend.function.triggermessage.model.MessageQueue;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@Service
public class MotdTransmitServiceImpl implements MotdTransmitService {
    private final MessageQueueFactory factory;

    private final MotdManageService service;

    public MotdTransmitServiceImpl(MessageQueueFactory factory, MotdManageService service) {
        this.factory = factory;
        this.service = service;
    }

    @Async
    @Override
    public void transmit(ExportObject exportObject) {
        factory.setExportObject(exportObject);
        MessageQueue queue = factory.getObject();

        if (queue != null) {
            // find roles of a player

//            service.findAll().forEach(queue::pend);

            queue.send();
        }
    }

    @ListenLavaMessage(destination = "lava.spawn-control.export-object", selector = "type = 'spawn'")
    @Override
    public void trigger(Object trigger) {
        if (trigger instanceof TextMessage) {
            try {
                ExportObject exportObject =
                        new Gson().fromJson(((TextMessage) trigger).getText(), ExportObject.class);
                transmit(exportObject);
            } catch (JsonSyntaxException | JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
