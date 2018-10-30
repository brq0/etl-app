package com.tmw.etl.etlapp.db.repositories;

import com.tmw.etl.etlapp.db.entities.Game;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface GameRepository extends CrudRepository<Game, String> {
    @Transactional
    @Modifying
    @Query("DELETE FROM games")
    void restartDb();
}
