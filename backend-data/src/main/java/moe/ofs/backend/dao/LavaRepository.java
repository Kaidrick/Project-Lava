package moe.ofs.backend.dao;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface LavaRepository<T, ID> {
    List<T> findAll();

    List<T> findAll(Sort var1);

    List<T> findAllById(Iterable<ID> var1);

    <S extends T> List<S> saveAll(Iterable<S> var1);

//    void flush();
//
//    <S extends T> S saveAndFlush(S var1);
//
//    void deleteInBatch(Iterable<T> var1);

    void deleteAllInBatch();

//    T getOne(ID var1);
//
//    <S extends T> List<S> findAll(Example<S> var1);
//
//    <S extends T> List<S> findAll(Example<S> var1, Sort var2);
}
