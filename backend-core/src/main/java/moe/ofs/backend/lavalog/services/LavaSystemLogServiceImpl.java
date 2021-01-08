package moe.ofs.backend.lavalog.services;

import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import moe.ofs.backend.connector.LavaSystemStatus;
import moe.ofs.backend.dao.LogEntryDao;
import moe.ofs.backend.domain.jms.LogEntry;
import moe.ofs.backend.domain.jms.LogLevel;
import moe.ofs.backend.domain.pagination.LavaSystemLogPageObject;
import moe.ofs.backend.domain.pagination.PageVo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.time.Instant;
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
    public PageVo<LogEntry> findAllForCurrentSession(Long current, Integer size) {

        Page<LogEntry> page = new Page<>(current, size);
//        page.setSearchCount(false);
        entryDao.selectPage(page,
                Wrappers.<LogEntry>lambdaQuery().gt(LogEntry::getTime, LavaSystemStatus.getStartTime())
        );

        return new PageVo<>(current, page.getTotal(), page.getRecords());
    }

    @Override
    public PageVo<LogEntry> findLogsWithCriteria(LavaSystemLogPageObject object) {
        Page<LogEntry> page = new Page<>(object.getCurrentPageNo(), object.getPageSize());

        // TODO: use a page object time field processor to deal with null Date or epoch date
        // TODO: aspects
        if (object.getTo().equals(Date.from(Instant.EPOCH))) {
            object.setTo(Date.from(Instant.now()));
        }

        entryDao.selectPage(page,
                Wrappers.<LogEntry>lambdaQuery()
                        .gt(LogEntry::getTime, object.getFrom())
                        .lt(LogEntry::getTime, object.getTo())
                        .like(LogEntry::getMessage, object.getKeyword())
                        .like(LogEntry::getLogLevel,
                                object.getLogLevel() != null ? LogLevel.valueOf(object.getLogLevel()) : Strings.EMPTY)
        );

        return new PageVo<>(page.getCurrent(), page.getTotal(), page.getRecords());
    }

    @Override
    public void saveLogFile(boolean toJson) throws IOException {
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

    /**
     * Listens to message queue topic and save entry log.
     * MQ listener implementation detail is trivial to service contract and
     * thus is not required in interface.
     *
     * @param objectMessage message object that holds the log entry
     * @throws JMSException on message conversion exception
     */
    @JmsListener(destination = "backend.entry", containerFactory = "jmsListenerContainerFactory")
    private void systemWiseLog(ObjectMessage objectMessage) throws JMSException {
        Serializable serializable = objectMessage.getObject();
        if (serializable instanceof LogEntry) {
            LogEntry entry = (LogEntry) serializable;
            save(entry);
        }
    }
}
