package org.delcom.ifs25032pbo2026springbootcashflow.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Generic Repository Port.
 *
 * @param <T>  tipe entitas domain
 * @param <ID> tipe identifier entitas
 */
public interface IRepository<T, ID> {
    List<T> findAll();

    Optional<T> findById(ID id);

    T save(T entity);

    /** Menghapus berdasarkan ID; true jika ada yang terhapus. */
    boolean deleteById(ID id);

    void update(T entity);

    List<T> findBy(Predicate<T> predicate);
}
