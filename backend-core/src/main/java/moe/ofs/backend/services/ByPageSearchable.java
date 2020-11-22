package moe.ofs.backend.services;

import moe.ofs.backend.pagination.PageObject;
import moe.ofs.backend.pagination.PageVo;

import java.util.function.Predicate;

public interface ByPageSearchable<T> {
    PageVo<T> findPage(PageObject pageObject);

    PageVo<T> findPageByCriteria(PageObject pageObject, Predicate<T> predicate);
}
