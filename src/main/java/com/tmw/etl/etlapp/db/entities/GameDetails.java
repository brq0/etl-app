package com.tmw.etl.etlapp.db.entities;


import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity(name = "gamesDetails")
@Table(name = "gamesDetails")
public class GameDetails {
    public static final int MAX_DESC_LENGTH = 3000;

    @Id
    private String id;
    private int categoryId;
    private String price;
    private String imgUrl;
    private Integer position;
    @Lob
    @Length(max = MAX_DESC_LENGTH)
    private String description;
    private int producerId;
    private String releaseDate;
    private int pegiCodeId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProducerId() {
        return producerId;
    }

    public void setProducerId(int producerId) {
        this.producerId = producerId;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getPegiCodeId() {
        return pegiCodeId;
    }

    public void setPegiCodeId(int pegiCodeId) {
        this.pegiCodeId = pegiCodeId;
    }


    @Override
    public String toString() {
//        @TODO wydruk jak do csv
        return "GameDetails{" +
                "id='" + id + '\'' +
                ", category='" + categoryId + '\'' +
                ", price='" + price + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", position=" + position +
                '}';
    }


    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(obj instanceof Game) {
            GameDetails game = (GameDetails) obj;

//            @TODO
            return true;

        }else return false;
    }
}
