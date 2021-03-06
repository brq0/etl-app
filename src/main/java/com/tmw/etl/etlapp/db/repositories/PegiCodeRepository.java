package com.tmw.etl.etlapp.db.repositories;

import com.tmw.etl.etlapp.db.entities.PegiCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PegiCodeRepository extends CrudRepository<PegiCode, Integer> {
    @Query(value = "SELECT * from pegi_codes " +
            "WHERE img_url = :imgUrl", nativeQuery = true)
    Optional<PegiCode> findByPegiImgUrl(@Param("imgUrl") String imgUrl);

    @Transactional
    @Modifying
    @Query("DELETE FROM pegi_codes")
    void restartDb();
}
