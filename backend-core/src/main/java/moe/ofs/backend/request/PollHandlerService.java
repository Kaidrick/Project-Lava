package moe.ofs.backend.request;

import java.io.IOException;

public interface PollHandlerService {
    void poll() throws IOException;

    void init();
}
