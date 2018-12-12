package com.tmw.etl.etlapp.db.responses;

public class GameResponse {
    private String id;
    private String name;
    private String category;
    private String price;
    private String imgUrl;
    private Integer position;
    private String description;
    private String producer;
    private String releaseDate;
    private String pegiUrl;

    public GameResponse() {
    }

    public GameResponse(String id, String name, String category, String price, String imgUrl, Integer position, String description, String producer, String releaseDate, String pegiUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.imgUrl = imgUrl;
        this.position = position;
        this.description = description;
        this.producer = producer;
        this.releaseDate = releaseDate;
        this.pegiUrl = pegiUrl;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPegiUrl() {
        return pegiUrl;
    }

    public void setPegiUrl(String pegiUrl) {
        this.pegiUrl = pegiUrl;
    }

    @Override
    public String toString() {
        return id + ", " + name + ", " + category + ", " + price.replace(',','.') + ", " + releaseDate + ", " + producer + ", " + position + ", " + pegiUrl  + ", " + imgUrl;
    }
}
