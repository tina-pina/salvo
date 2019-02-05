package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Salvo {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  private long id;

  private int turnNum;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="game_player_id")
  private GamePlayer gamePlayer;

  @ElementCollection
  private List<String> locations = new ArrayList<String>();

  public Salvo() { }

  public Salvo(ArrayList<String> locations, GamePlayer gamePlayer, Integer turnNum) {
    this.locations = locations;
    this.gamePlayer = gamePlayer;
    this.turnNum = turnNum;
  }

  public GamePlayer getGamePlayer() {
    return gamePlayer;
  }

  public List<String> getLocations() {
    return locations;
  }

  public Integer getTurnNum() {
    return turnNum;
  }

}
