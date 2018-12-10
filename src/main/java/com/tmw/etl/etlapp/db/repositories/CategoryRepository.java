package com.tmw.etl.etlapp.db.repositories;

import com.tmw.etl.etlapp.db.entities.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
    @Query(value = "SELECT * from categories " +
            "WHERE name = :name", nativeQuery = true)
    Optional<Category> findByName(@Param("name") String name);

    @Transactional
    @Modifying
    @Query("DELETE FROM categories")
    void restartDb();
}
