package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.*;
import java.util.Date;


@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private int id;
    private Date date;
    public Game() { }

    public Game(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return this.date;
    }

    public int getId() {
        return this.id;
    }

    public void setDate(Date date) {
        date = new Date();
        this.date = date;
    }

//    public void main(String[] args) {
//        System.out.println(getDate());
//    }

//    public String toString() {
//        return date;
//    }
}
