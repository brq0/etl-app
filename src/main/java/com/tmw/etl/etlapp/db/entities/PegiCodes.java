package com.tmw.etl.etlapp.db.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity(name = "pegicodes")
@Table(name = "pegicodes")
public class PegiCodes {

    @Id
    String id;
    String imgUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return id = ", " + imgUrl;
    }
}
