package com.tmw.etl.etlapp.db.repositories;

import com.tmw.etl.etlapp.db.entities.Producer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ProducerRepository extends CrudRepository<Producer, Integer> {
    @Query(value = "SELECT * from producers " +
            "WHERE name = :name", nativeQuery = true)
    Optional<Producer> findByName(@Param("name") String name);

    @Transactional
    @Modifying
    @Query("DELETE FROM producers")
    void restartDb();
}
