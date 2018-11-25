package com.tmw.etl.etlapp.db.repositories;

import com.tmw.etl.etlapp.db.entities.GameDetails;
import org.springframework.data.repository.CrudRepository;

public interface GameDetailsRepository extends CrudRepository<GameDetails, String> {



}
