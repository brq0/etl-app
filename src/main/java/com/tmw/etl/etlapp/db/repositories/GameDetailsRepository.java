package com.tmw.etl.etlapp.db.repositories;

import com.tmw.etl.etlapp.db.entities.GameDetails;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface GameDetailsRepository extends CrudRepository<GameDetails, String> {

    @Transactional
    @Modifying
    @Query("UPDATE games SET " +
            "name = :name " +
            "WHERE id = :id")
    void updateRow(@Param("id") String id,
                    @Param("name") String name);

}
