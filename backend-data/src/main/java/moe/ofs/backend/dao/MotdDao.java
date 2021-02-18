package moe.ofs.backend.dao;

import moe.ofs.backend.domain.admin.message.MotdMessageSet;

import java.util.Set;

public interface MotdDao {
    Set<MotdMessageSet> findAllMotdMessageSet();
}
