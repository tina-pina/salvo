package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.dto.GameDTO;
import com.codeoftheweb.salvo.dto.PlayerInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

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
                String playerEmail = p.getUsername();

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

//    @RequestMapping("/api/players")
//    public List<Long> getAllPlayers() {
//        List<Player> players = playerRepo.findAll();
//        List<Long> idList = new ArrayList<Long>();
//        for(Player p : players) idList.add(p.getId());
//        return idList;
//    }

    @RequestMapping(path = "/api/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createPlayer(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        if (username.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No name"), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepo.findByUsername(username);
        if (player != null) {
            return new ResponseEntity<>(makeMap("error", "Username already exists"), HttpStatus.CONFLICT);
        }
        Player newPlayer = playerRepo.save(new Player(username, password));
        return new ResponseEntity<>(makeMap("id", newPlayer.getId()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @RequestMapping("/api/player")
    private Object getPlayerInfo (Authentication authentication) {
        if(isGuest(authentication)) {
            return "Login First";
        }

        PlayerInfoDTO dto = new PlayerInfoDTO();

        String username = authentication.getName();
        Player player = playerRepo.findByUsername(username);

        // Create player
        HashMap<String, String> playerObj = new HashMap<String, String>();
        playerObj.put("id", Long.toString(player.getId()));
        playerObj.put("name", username);
        dto.setPlayer(playerObj);

        // Create games
        List<GameDTO> gamesList = new ArrayList<GameDTO>();
        Set<GamePlayer> gamePlayers = player.getGamePlayers();

        // Get all games played by the current player
        for(GamePlayer gp: gamePlayers) {

            GameDTO gameDto = new GameDTO();
            Game game = gp.getGame();

            // get id
            int gameId = game.getId();
            gameDto.setId(gameId);
            //get date
            Date date = game.getDate();
            gameDto.setCreated(date);

            // For each Game, get players, ships, salvos
            Set<GamePlayer> gameGamePlayers = game.getGamePlayers();
            for(GamePlayer ggp: gameGamePlayers) {
                gameDto.addGamePlayer(ggp);
                gameDto.addSalvo(ggp);
                gameDto.addShip(ggp);
            }
            gamesList.add(gameDto);
        }

        dto.setGames(gamesList);
        return dto;
    }

    @RequestMapping("/api/game_view/{gamePlayerId}")
    public Object getGameView(@PathVariable Long gamePlayerId) {

        GameDTO dto = new GameDTO();

        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
        Game game = gamePlayer.getGame();

        dto.setId(game.getId());
        dto.setCreated(game.getDate());

        Set<GamePlayer> gamePlayers = game.getGamePlayers();

        //here we get several game players (and their locations) for our salvo array
        for(GamePlayer gp: gamePlayers) {
            // create player obj
            dto.addGamePlayer(gp);
            // create ship Array
            dto.addShip(gp);
            // create salvo Array
            dto.addSalvo(gp);
        }

        return dto;
    }



}
