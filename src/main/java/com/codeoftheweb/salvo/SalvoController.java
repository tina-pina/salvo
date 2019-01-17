package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
public class SalvoController {

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;



//    @RequestMapping("/api/games")
//    public List<Game> getAllGames() {
//        return gameRepo.findAll();
//    }

        @RequestMapping("/api/games")
    public List<Map<String, Object>> getAllGames() {

        // empty array [] // {"", ...}
        List<Map<String, Object>> gameList = new ArrayList<Map<String, Object>>();

        // original data [{}, {}, {},...]
        List<Game> games = gameRepo.findAll();

        for(Game g : games) {

            // empty obj {}
            Map<String, Object> gameObj = new HashMap<String, Object>();

            // add key value id {"id": 1}
            gameObj.put("id", g.getId());

            // add key value created {
            //      "id": 1,
            //      "created": .....
            // }
            gameObj.put("created", g.getDate());

            // todo add key gamePlayers array
            // players array
            List<Map<String, Object>> gamePlayerList = new ArrayList<Map<String, Object>>();
            // playerList.put(...)

            Set<GamePlayer> gamePlayers = g.getGamePlayers();

            for(GamePlayer gp: gamePlayers) {

                long gamePlayerId = gp.getId();
                Player p = gp.getPlayer();
                long playerId = p.getId();
                String playerEmail = p.getEmail();

                Map<String, Object> gamePlayerObj = new HashMap<String, Object>();
                gamePlayerObj.put("id", gamePlayerId);

                Map<String, Object> playerObj = new HashMap<String, Object>();
                playerObj.put("id", playerId);
                playerObj.put("email", playerEmail);

                try {
                    playerObj.put("score", gp.getScore().getScore());
                } catch (NullPointerException e) {

                }

                gamePlayerObj.put("player", playerObj);
                gamePlayerList.add(gamePlayerObj);

            }

            gameObj.put("gamePlayers", gamePlayerList);

            // [{}, ...]
            gameList.add(gameObj);
        }
        return gameList;

    }

    @RequestMapping("/api/players")
    public List<Long> getAllPlayers() {
        List<Player> players = playerRepo.findAll();
        List<Long> idList = new ArrayList<Long>();
        for(Player p : players) idList.add(p.getId());
        return idList;
    }


    @RequestMapping("/api/game_view/{gamePlayerId}")
    public Object findGamePlayer(@PathVariable Long gamePlayerId) {

        GameViewDTO dto = new GameViewDTO();

        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
        Game game = gamePlayer.getGame();

        dto.setId(game.getId());
        dto.setCreated(game.getDate());

        Set<GamePlayer> gamePlayers = game.getGamePlayers();
        for(GamePlayer gp: gamePlayers) {
            // create player obj
            dto.addGamePlayer(gp);
            // create ship Array
            dto.addShip(gp);
            // create salvo Array
            dto.addSalvo(gp);
        }

        return dto;




//        obj.put("gamePlayers", gpArr);
//        obj.put("ships", shipArr);
//        obj.put("salvos", salvoArr);
//
//        Map<String, Object> obj = new HashMap<String, Object>();
//        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
//        Game game = gamePlayer.getGame();
//        obj.put("id", game.getId());
//        obj.put("created", game.getDate());
//
//        ArrayList<Object> gpArr = new ArrayList<Object>();
//        ArrayList<Object> shipArr = new ArrayList<Object>();
//        ArrayList<Object> salvoArr = new ArrayList<Object>();
//
//        Set<GamePlayer> gamePlayers = game.getGamePlayers();
//        for(GamePlayer gp: gamePlayers) {
//
//            // create player obj
//            Map<String, Object> playerObj = new HashMap<String, Object>();
//            Player player = gp.getPlayer();
//            playerObj.put("id", player.getId());
//            playerObj.put("email", player.getEmail());
//
//            // create gamePlayer obj
//            Map<String, Object> gpObj = new HashMap<String, Object>();
//            gpObj.put("id", gp.getId());
//            gpObj.put("player", playerObj);
//
//            // push to gamePlayer Array
//            gpArr.add(gpObj);
//
//            // create ship Array
//            for (Ship s : gp.getShips()) {
//                Map<String, Object> shipObj = new HashMap<String, Object>();
//                shipObj.put("type", s.getType());
//                shipObj.put("locations", s.getLocations());
//                shipArr.add(shipObj);
//            }
//
//            // create salvo Array
//            for(Salvo sa: gp.getSalvos()) {
//                Map<String, Object> salvoObj = new HashMap<String, Object>();
//                salvoObj.put("turn", sa.getTurnNum());
//                salvoObj.put("locations", sa.getLocations());
//                salvoObj.put("player", gp.getPlayer().getId());
//                salvoArr.add(salvoObj);
//            }
//
//        }
//
//        obj.put("gamePlayers", gpArr);
//        obj.put("ships", shipArr);
//        obj.put("salvos", salvoArr);
//        return obj;
//
  }
}
