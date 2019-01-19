package com.codeoftheweb.salvo.dto;

// {
//    "player": { "id": nn, "name": username },
//    "games": [ ... ]
// }

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerInfoDTO {

  Map<String, String> player = new HashMap<>();
  List<GameDTO> games = new ArrayList<>();

  public Map<String, String> getPlayer() {
    return player;
  }

  public void setPlayer(Map<String, String> player) {
    this.player = player;
  }

  public List<GameDTO> getGames() {
    return games;
  }

  public void setGames(List<GameDTO> games) {
    this.games = games;
  }
}
