package moe.ofs.backend.lavalog;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import moe.ofs.backend.config.model.PageVo;
import moe.ofs.backend.dao.LogEntryDao;
import moe.ofs.backend.domain.LogEntry;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

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
    public void saveLogFile() throws IOException {

    }
}
