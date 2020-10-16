package moe.ofs.backend.request.export;

import moe.ofs.backend.domain.Handle;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.request.BaseRequest;
import moe.ofs.backend.request.Processable;
import moe.ofs.backend.request.Resolvable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ExportDataRequest extends BaseRequest implements Resolvable {

    private transient String luaString;

    private volatile String result;
    private List<Processable> list = new ArrayList<>();

    public ExportDataRequest(String luaString) {
        super(Level.EXPORT);
        port = Level.EXPORT.getPort();
        handle = Handle.EXEC;

        this.luaString = luaString;
    }

    @Override
    public void resolve(String object) {
        this.result = object;
        notifyProcessable(object);
    }

    /**
     * blocking call
     * @return result
     */
    public String get() {
        Instant entryTime = Instant.now();
        while(true) {
            if(result != null) {
                if (result.isEmpty()) {
                    return "<LUA EMPTY STRING>";
                }

                return result;
            } else {  // if result is null, but timeout is reached
                if (Instant.now().minusMillis(2000).isAfter(entryTime)) {
                    return "<NO RESULT OR LUA TIMED OUT>";
                }
            }
        }
    }

    public int getAsInt() {
        return Integer.parseInt(get());
    }

    public double getAsDouble() {
        return Double.parseDouble(get());
    }


    public ExportDataRequest addProcessable(Processable processable) {
        list.add(processable);
        return this;
    }

    public void notifyProcessable(String object) {
        list.forEach(p -> p.process(object));
    }
}
