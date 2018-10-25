package com.tmw.etl.etlapp.db.repositories;

import com.tmw.etl.etlapp.db.entities.OutputEntity;
import org.springframework.data.repository.CrudRepository;

public interface OutputRepository extends CrudRepository<OutputEntity, Integer> {
}
