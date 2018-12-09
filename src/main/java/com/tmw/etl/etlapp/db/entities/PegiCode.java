package com.tmw.etl.etlapp.db.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;


@Entity(name = "pegi_codes")
@Table(name = "pegi_codes")
public class PegiCode {

    @Id
    private int id;
    private String imgUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        return id + ", " + imgUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PegiCode pegiCode = (PegiCode) o;
        return Objects.equals(imgUrl, pegiCode.imgUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imgUrl);
    }
}
