package moe.ofs.backend.common;

import moe.ofs.backend.domain.pagination.PageObject;
import moe.ofs.backend.domain.pagination.PageVo;

import java.util.function.Predicate;

public interface ByPageSearchable<T> {
    PageVo<T> findPage(PageObject pageObject);

    PageVo<T> findPageByCriteria(PageObject pageObject, Predicate<T> predicate);
}
