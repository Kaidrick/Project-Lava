package moe.ofs.backend.request.export;

import moe.ofs.backend.domain.Handle;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.request.BaseRequest;
import moe.ofs.backend.request.Processable;
import moe.ofs.backend.request.Resolvable;

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
        while(true) {
            if(result != null) {
                return result;
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
