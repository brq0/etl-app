package com.tmw.etl.etlapp.db.repositories;

import com.tmw.etl.etlapp.db.entities.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Integer> {

}
