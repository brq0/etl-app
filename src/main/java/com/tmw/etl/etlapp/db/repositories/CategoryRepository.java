package com.tmw.etl.etlapp.db.repositories;

import com.tmw.etl.etlapp.db.entities.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
}
