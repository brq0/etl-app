package com.tmw.etl.etlapp.db.repositories;

import com.tmw.etl.etlapp.db.entities.Producer;
import org.springframework.data.repository.CrudRepository;

public interface ProducerRepository extends CrudRepository<Producer, Integer> {
}
