package com.tmw.etl.etlapp.db.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "games")
@Table(name = "games")
public class Game {
    @Id
    private String id;
    private String name;

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

    @Override
    public String toString() {
        return id + ',' + name ;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(obj instanceof Game) {
            Game game = (Game) obj;

            return name.equals(game.getName());

        }else return false;
    }
}
