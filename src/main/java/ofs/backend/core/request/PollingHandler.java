package ofs.backend.core.request;

import java.io.IOException;

/**
 * PollingHandler class handles the connection to polling port in three different lua states.
 * It always connects to lua server to retrieve polling data from DCS.
 * It does not have to send any meaningful bytes to lua server.
 */
public abstract class PollingHandler {
    public enum PollEnv {
        SERVER(3011), EXPORT(3013);

        int portNumber;

        PollEnv(int portNumber) {
            this.portNumber = portNumber;
        }
    }

    private PollEnv env;

    protected PollingHandler(PollEnv env) {
        this.env = env;
    }

    protected int getPort() {
        return this.env.portNumber;
    }

    protected boolean isRequestDone = true;
    protected int batchCount = 0;

    protected int flipCount = 0;
    public abstract void poll() throws IOException;
}


