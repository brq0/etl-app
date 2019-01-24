package com.tmw.etl.etlapp.db.entities;


import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Entity(name = "games")
@Table(name = "games")
public class Game {
    public static final int MAX_DESC_LENGTH = 3000;

    @Id
    private String id;
    private String name;
    private int categoryId;
    private String price;
    private String imgUrl;
    private Integer position;
    @Lob
    @Length(max = MAX_DESC_LENGTH)
    private String description;
    private int producerId;
    private Date releaseDate;
    private int pegiCodeId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        try {
            Format formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.format(releaseDate);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setReleaseDate(String releaseDate) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = dateFormat.parse(releaseDate);
            this.releaseDate = date;
        } catch (ParseException | IllegalArgumentException e) {
            e.printStackTrace();
            this.releaseDate = new Date();
        }
    }

    public int getPegiCodeId() {
        return pegiCodeId;
    }

    public void setPegiCodeId(int pegiCodeId) {
        this.pegiCodeId = pegiCodeId;
    }


    @Override
    public String toString() {
        return id + ", " + name + ", " + categoryId + ", " + price + ", " + releaseDate + ", " + producerId + ", " + position + ", " + pegiCodeId + ", " + imgUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game that = (Game) o;
        return categoryId == that.categoryId &&
                producerId == that.producerId &&
                pegiCodeId == that.pegiCodeId &&
                Objects.equals(name, that.name) &&
                Objects.equals(price, that.price) &&
                Objects.equals(imgUrl, that.imgUrl) &&
                Objects.equals(position, that.position) &&
                Objects.equals(description, that.description) &&
                releaseDate.compareTo(that.releaseDate) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, categoryId, price, imgUrl, position, description, producerId, releaseDate, pegiCodeId);
    }
}
