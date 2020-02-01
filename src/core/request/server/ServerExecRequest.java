package core.request.server;

import core.request.BaseRequest;
import core.request.RequestToServer;

/**
 * The constructor of ServerExecRequest can take a RequestToMission
 */

public class ServerExecRequest extends RequestToServer {
    {
        handle = Handle.EXEC;
        port = 3010;
        state = State.SERVER;
    }

    private transient String env;
    private transient String luaString;


    public ServerExecRequest(String luaString) {
        this.luaString = luaString;
        this.env = this.state.name().toLowerCase();
    }


    @Override
    public void resolve(String object) {
        System.out.println(object);
    }

    public static void main(String[] args) {
        BaseRequest baseRequest = new ServerExecRequest("trigger.action.outText('test', 1)");
        baseRequest.send();
        System.out.println(baseRequest.toString());
    }
}

