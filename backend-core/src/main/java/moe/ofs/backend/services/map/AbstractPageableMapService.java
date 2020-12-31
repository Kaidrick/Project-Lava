package moe.ofs.backend.services.map;

import moe.ofs.backend.domain.BaseEntity;
import moe.ofs.backend.pagination.PageObject;
import moe.ofs.backend.pagination.PageVo;
import moe.ofs.backend.services.ByPageSearchable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AbstractPageableMapService<T extends BaseEntity>
        extends AbstractMapService<T> implements ByPageSearchable<T> {

    protected List<T> list;

    @Override
    public void deleteById(Long id) {
        super.deleteById(id);

        if (list != null) {
            list.removeIf(entry -> entry.getId().equals(id));
        }
    }

    @Override
    public T save(T object) {
        T t = super.save(object);

        if (list != null) {
            list.add(t);
        }

        return t;
    }

    @Override
    public void deleteAll() {
        super.deleteAll();

        if (list != null) {
            list.clear();
        }
        list = null;
    }

    @Override
    public void delete(T t) {
        super.delete(t);

        if (list != null) {
            list.remove(t);
        }
    }

    @Override
    public PageVo<T> findPage(PageObject pageObject) {
        if (list == null) {
            list = map.values().stream()
                    .sorted(Comparator.comparingLong(T::getId)).parallel()
                    .collect(Collectors.toList());
        }

        return paginate(pageObject, list);
    }

    @Override
    public PageVo<T> findPageByCriteria(PageObject pageObject, Predicate<T> predicate) {
        if (list == null) {
            list = map.values().stream()
                    .sorted(Comparator.comparingLong(T::getId)).parallel()
                    .collect(Collectors.toList());
        }

        return paginate(pageObject, list.stream().filter(predicate).collect(Collectors.toList()));
    }

    private PageVo<T> paginate(PageObject pageObject, List<T> source) {
        int start = (pageObject.getCurrentPageNo().intValue() - 1) * pageObject.getPageSize();
        int end = pageObject.getCurrentPageNo().intValue() * pageObject.getPageSize();

        if (start > source.size() - 1) {
            return new PageVo<>(pageObject.getCurrentPageNo(), 0L,
                    Collections.emptyList());
        }

        return new PageVo<>(pageObject.getCurrentPageNo(), (long) source.size(),
                source.subList(start, Math.min(source.size(), end)));
    }
}
