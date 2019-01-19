package com.codeoftheweb.salvo.dto;

// {
//  "id" : 1,
//  "created" : 1456438201629,
//  "gamePlayers": [ ... ],
//  "ships": [ ... ],
//  "salvoes": ...
// }

import com.codeoftheweb.salvo.GamePlayer;
import com.codeoftheweb.salvo.Player;
import com.codeoftheweb.salvo.Salvo;
import com.codeoftheweb.salvo.Ship;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GameDTO {

  private long id;
  private Date created;
  private ArrayList<Map<String, Object>> gamePlayers = new ArrayList<>(); // [{"...", [...]}, {}]
  private ArrayList<Map<String, Object>> ships = new ArrayList<>();
  private ArrayList<Map<String, Object>> salvos = new ArrayList<>();

  public long getId() {
    return id;
  }

  public GameDTO() {}

  public GameDTO(long id,
                 Date created,
                 ArrayList<Map<String, Object>> gamePlayers,
                 ArrayList<Map<String, Object>> ships,
                 ArrayList<Map<String, Object>> salvos) {
    this.id = id;
    this.created = created;
    this.gamePlayers = gamePlayers;
    this.ships = ships;
    this.salvos = salvos;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public ArrayList<Map<String, Object>> getGamePlayers() {
    return gamePlayers;
  }

  public void setGamePlayers(ArrayList<Map<String, Object>> gamePlayers) {
    this.gamePlayers = gamePlayers;
  }

  public ArrayList<Map<String, Object>> getShips() {
    return ships;
  }

  public void setShips(ArrayList<Map<String, Object>> ships) {
    this.ships = ships;
  }

  public ArrayList<Map<String, Object>> getSalvos() {
    return salvos;
  }

  public void setSalvos(ArrayList<Map<String, Object>> salvos) {
    this.salvos = salvos;
  }

  public void addGamePlayer(GamePlayer gp) {
    // gamePlayers: [{id: ..., player: {id: ..., email: ...}}]
    Map<String, Object> gpObj = new HashMap<String, Object>();
    gpObj.put("id", gp.getId());
    Map<String, Object> playerObj = new HashMap<String, Object>();
    Player player = gp.getPlayer();
    playerObj.put("id", player.getId());
    playerObj.put("email", player.getUsername());
    gpObj.put("player", playerObj);
    this.gamePlayers.add(gpObj);
  }

  public void addShip(GamePlayer gp) {
    for (Ship s : gp.getShips()) {
      Map<String, Object> shipObj = new HashMap<String, Object>();
      shipObj.put("type", s.getType());
      shipObj.put("locations", s.getLocations());
      this.ships.add(shipObj);
    }
  }

  public void addSalvo(GamePlayer gp) {
    for(Salvo sa: gp.getSalvos()) {
      Map<String, Object> salvoObj = new HashMap<String, Object>();
      salvoObj.put("turn", sa.getTurnNum());
      salvoObj.put("locations", sa.getLocations());
      salvoObj.put("player", gp.getPlayer().getId());
      this.salvos.add(salvoObj);
    }
  }


}
