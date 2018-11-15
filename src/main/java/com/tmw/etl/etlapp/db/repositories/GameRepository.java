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
            "product_name = :name, " +
            "product_category = :category, " +
            "product_price = :price, " +
            "product_image_url = :imgUrl, " +
            "position = :position " +
            "WHERE product_id = :id")
    void updateGame(@Param("id") String id,
                    @Param("name") String name,
                    @Param("category") String category,
                    @Param("price") String price,
                    @Param("imgUrl") String imgUrl,
                    @Param("position") Integer position);

    @Transactional
    @Modifying
    @Query("DELETE FROM games")
    void restartDb();
}
