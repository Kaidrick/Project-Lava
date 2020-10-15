package moe.ofs.backend.function.triggermessage;

import moe.ofs.backend.request.server.ServerExecRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.util.LuaScripts;
import org.springframework.stereotype.Service;

@Service
public class TriggerMessageServiceImpl implements TriggerMessageService {

    private static final String triggerMessageByGroupId = LuaScripts.load("send_message_by_group_id.lua");

    private final RequestTransmissionService requestTransmissionService;

    public TriggerMessageServiceImpl(RequestTransmissionService requestTransmissionService) {
        this.requestTransmissionService = requestTransmissionService;
    }

    @Override
    public TriggerMessage.TriggerMessageBuilder getTriggerMessageTemplate() {
        return new TriggerMessage.TriggerMessageBuilder();
    }

    @Override
    public void sendTriggerMessage(TriggerMessage triggerMessage) {
        String preparedString = String.format(triggerMessageByGroupId,
                triggerMessage.getReceiverGroupId(), triggerMessage.getMessage(),
                triggerMessage.getDuration(), triggerMessage.isClearView());
        System.out.println(preparedString);
        requestTransmissionService.send(new ServerExecRequest(preparedString));
    }
}
