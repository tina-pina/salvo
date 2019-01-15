package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;


@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private int id;

    @OneToMany(mappedBy="game", fetch= FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

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

    public Set<GamePlayer> getGamePlayers() { return gamePlayers; }

    public void setDate(Date date) {
        this.date = new Date();
    }

    @Override
    public String toString() {
        return "[" + id + "]";
    }
}
