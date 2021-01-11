package moe.ofs.backend.pollservices;

import java.io.IOException;

public interface PollHandlerService {
    void poll() throws IOException;

    void init();
}
