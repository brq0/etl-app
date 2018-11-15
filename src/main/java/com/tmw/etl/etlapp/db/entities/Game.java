package com.tmw.etl.etlapp.db.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "games")
@Table(name = "games")
public class Game {
    @Id
    private String productId;
    private String productName;
    private String productCategory;
    private String productPrice;
    private String productImageUrl;
    private Integer position;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return productId + ',' + productName + ',' + productCategory + ',' + productPrice + ',' + productImageUrl + ", " + position;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(obj instanceof Game) {
            Game game = (Game) obj;

            return productName.equals(game.getProductName())
                    && productCategory.equals(game.getProductCategory())
                    && productPrice.equals(game.getProductPrice())
                    && productImageUrl.equals(game.getProductImageUrl())
                    && position.equals(game.getPosition());

        }else return false;
    }
}
