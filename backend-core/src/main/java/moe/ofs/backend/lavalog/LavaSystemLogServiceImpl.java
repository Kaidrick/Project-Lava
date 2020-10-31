package moe.ofs.backend.lavalog;

import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import moe.ofs.backend.config.model.PageVo;
import moe.ofs.backend.dao.LogEntryDao;
import moe.ofs.backend.domain.LogEntry;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

@Service
public class LavaSystemLogServiceImpl implements LavaSystemLogService {

    private final LogEntryDao entryDao;

    public LavaSystemLogServiceImpl(LogEntryDao entryDao) {
        this.entryDao = entryDao;
    }

    @Override
    public LogEntry findById(Long id) {
        return entryDao.selectById(id);
    }

    @Override
    public void save(LogEntry logEntry) {
        entryDao.insert(logEntry);
    }

    @Override
    public PageVo<LogEntry> findAllForCurrentSession(Date date, Long current, Integer size) {

        Page<LogEntry> page = new Page<>(current, size);
        entryDao.selectPage(page,
                Wrappers.<LogEntry>lambdaQuery().gt(LogEntry::getTime, date)
        );
        return new PageVo<>(current, page.getTotal(), page.hasNext(), page.hasPrevious(), page.getRecords());
    }

    @Override
    public void saveLogFile(Boolean toJson) throws IOException {
        List<LogEntry> list = entryDao.selectList(null);
        String path = "/LavaLog " + new Date();

        if (toJson) {
            path += ".json";
            FileWriter writer = new FileWriter(path);
            writer.write(new Gson().toJson(list));
            writer.close();
        } else {
            path += ".csv";
//            Excel读取CSV文件中含有中文时时必须为GBK编码（Windows平台下）
            Charset charset = System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS") ?
                    CharsetUtil.CHARSET_GBK : CharsetUtil.CHARSET_UTF_8;

            CsvWriter writer = CsvUtil.getWriter(path, charset);
            writer.write(list);
            writer.close();
        }
    }
}
