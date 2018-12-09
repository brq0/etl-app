package com.tmw.etl.etlapp.db.repositories;

import com.tmw.etl.etlapp.db.entities.Game;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface GameRepository extends CrudRepository<Game, String> {
    @Transactional
    @Modifying
    @Query("UPDATE games SET " +
            "name = :name, " +
            "category_id = :categoryId, " +
            "price = :price, " +
            "img_url = :imgUrl, " +
            "position = :position, " +
            "description = :description, " +
            "producer_id = :producerId, " +
            "release_date = :releaseDate, " +
            "pegi_code_id = :pegiCodeId " +
            "WHERE id = :id")
    void updateGame(@Param("id") String id,
                    @Param("name") String name,
                    @Param("categoryId") int categoryId,
                    @Param("price") String price,
                    @Param("imgUrl") String imgUrl,
                    @Param("position") int position,
                    @Param("description") String description,
                    @Param("producerId") int producerId,
                    @Param("releaseDate") String releaseDate,
                    @Param("pegiCodeId") int pegiCodeId
                    );

    @Transactional
    @Modifying
    @Query("DELETE FROM games")
    void restartDb();
}
