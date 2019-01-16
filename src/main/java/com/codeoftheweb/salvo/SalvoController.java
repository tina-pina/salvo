package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;


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

        Map<String, Object> obj = new HashMap<String, Object>();
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
        Game game = gamePlayer.getGame();
        obj.put("id", game.getId());
        obj.put("created", game.getDate());

        // gamePlayers
        ArrayList<Object> gpArr = new ArrayList<Object>();
        ArrayList<Object> shipArr = new ArrayList<Object>();
        ArrayList<Object> salvoArr = new ArrayList<Object>();

        Set<GamePlayer> gamePlayers = game.getGamePlayers();
        for(GamePlayer gp: gamePlayers) {
            Player player = gp.getPlayer();

            // create gamePlayer obj
            Map<String, Object> gpObj = new HashMap<String, Object>();
            Map<String, Object> playerObj = new HashMap<String, Object>();
            playerObj.put("id", player.getId());
            playerObj.put("email", player.getEmail());
            gpObj.put("id", gp.getId());
            gpObj.put("player", playerObj);

            gpArr.add(gpObj);

            // create ship obj
            for(Ship s: gp.getShips()) {
                Map<String, Object> shipObj = new HashMap<String, Object>();
                shipObj.put("type", s.getType());
                shipObj.put("locations", s.getLocations());
                shipArr.add(shipObj);
            }


            //create salvo

//        [
//            { "turn": "1", "player": "23", "locations": ["H1", "A2"] },
//            { "turn": "1", "player": "54", "locations": ["C5", "E6"] },
//            { "turn": "2", "player": "23", "locations": ["B4", "D8"] },
//            { "turn": "2", "player": "54", "locations": ["A7", "F1"] }
//        ]

//          create Array


            Map<String, Object> salvoObj = new HashMap<String, Object>();


            for(Salvo s : gp.getSalvos()) {
                salvoObj.put("turn", s.getTurnNum());
                salvoObj.put("player", gamePlayerId);
                salvoObj.put("locations", s.getLocations());
            }
            salvoArr.add(salvoObj);

        }
        obj.put("gamePlayers", gpArr);
        obj.put("ships", shipArr);
        obj.put("salvoes", salvoArr);

        return obj;

    }

}
