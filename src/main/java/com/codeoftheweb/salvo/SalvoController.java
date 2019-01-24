package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.dto.GameDTO;
import com.codeoftheweb.salvo.dto.PlayerInfoDTO;
import com.sun.tools.internal.ws.processor.generator.CustomExceptionGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;


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

    /*
     * all games (no login needed)
     * */
    @RequestMapping(path = "/api/games", method = RequestMethod.POST)
    public ResponseEntity<Object> createGame (Authentication authentication) {

        if (authentication == null) {
            return new ResponseEntity<>("No user logged-in", HttpStatus.UNAUTHORIZED);
        }

        Date date = new Date();

        Player currentPlayer = playerRepo.findByUsername(authentication.getName());


        Game newGame = new Game(date);
        gameRepo.save(newGame);

        GamePlayer newGamePlayer = new GamePlayer(newGame, currentPlayer);
        gamePlayerRepository.save(newGamePlayer);

        Long gpID = (Long) newGamePlayer.getId();

        Map<String, String> body = new HashMap<>();
        body.put("gpid", gpID.toString());
        return new ResponseEntity<Object>(body, HttpStatus.CREATED);

    }
        // create gamePlayer and save



    @RequestMapping("/api/games")
    public Map<String, Object> getAllGames() {

        // empty array [] // {"", ...}
        List<Map<String, Object>> gameList = new ArrayList<Map<String, Object>>();

        // original data [{}, {}, {},...]
        List<Game> games = gameRepo.findAll();

        for (Game g : games) {

            // empty obj {}
            Map<String, Object> gameObj = new HashMap<String, Object>();

            // add key value id {"id": 1}
            gameObj.put("id", g.getId());

            // add key value created {
            //      "id": 1,
            //      "created": .....
            // }
            gameObj.put("created", g.getDate());

            // players array
            List<Map<String, Object>> gamePlayerList = new ArrayList<Map<String, Object>>();
            // playerList.put(...)

            Set<GamePlayer> gamePlayers = g.getGamePlayers();

            for (GamePlayer gp : gamePlayers) {

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

        Map<String, Object> newObj = new HashMap<String, Object>();
        newObj.put("games", gameList);

        // {games: [.......]}

        return newObj;

    }


    /*
     * create a new player with post method
     * */
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


    //return all ids of GamePlayer which are played by player --> gp id by current player

    @RequestMapping("/api/player/playerGameIds")
    private Object getAllGPIDs(Authentication authentication) {
        Map<String, Object> obj = new HashMap<>();
        obj.put("ids", playerRepo.findByUsername(authentication.getName()).getGamePlayers()
          .stream().map(gp -> gp.getId()).collect(Collectors.toList()));
        return obj;
    }

    /*
     * only the player who is logged in right now; api/login is to send data not to get information about player
     * */
    @RequestMapping("/api/player")
    private Object getPlayerInfo(Authentication authentication) {
        if (isGuest(authentication)) {
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
        for (GamePlayer gp : gamePlayers) {

            GameDTO gameDto = new GameDTO();
            Game game = gp.getGame();

            // get id
            long gameId = game.getId();
            gameDto.setId(gameId);
            //get date
            Date date = game.getDate();
            gameDto.setCreated(date);

            // For each Game, get players, ships, salvos
            Set<GamePlayer> gameGamePlayers = game.getGamePlayers();
            for (GamePlayer ggp : gameGamePlayers) {
                gameDto.addGamePlayer(ggp);
                gameDto.addSalvo(ggp);
                gameDto.addShip(ggp);
            }
            gamesList.add(gameDto);
        }

        dto.setGames(gamesList);
        return dto;
    }

    /*
     * the game player defined in the url in all the recent games with other game players
     * */
    @RequestMapping("/api/game_view/{gamePlayerId}")
    public Object getGameView(@PathVariable Long gamePlayerId, Authentication authentication) {

        String currentUsername = authentication.getName();
        String gameviewPlayer = gamePlayerRepository.findOne(gamePlayerId).getPlayer().getUsername();
        if (currentUsername.equals(gameviewPlayer)) {

            GameDTO dto = new GameDTO();

            GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);

            Game game = gamePlayer.getGame();


            dto.setId(game.getId());
            dto.setCreated(game.getDate());

            Set<GamePlayer> gamePlayers = game.getGamePlayers();

            //here we get several game players (and their locations) for our salvo array
            for (GamePlayer gp : gamePlayers) {
                // create player obj
                dto.addGamePlayer(gp);
                // create ship Array
                dto.addShip(gp);
                // create salvo Array
                dto.addSalvo(gp);
            }

            return new ResponseEntity<Object>(dto, HttpStatus.ACCEPTED);
        } else
            {
            return new ResponseEntity<Object>(makeMap("error", "unauthorized"), HttpStatus.UNAUTHORIZED);
        }

    }

    @RequestMapping("/api/game/{gameId}/players")
    public Object joinGame(@PathVariable Long gameId, Authentication authentication) {
        String currentUsername = authentication.getName();
        if(currentUsername == null) {
            return new ResponseEntity<String>("not logged-in", HttpStatus.UNAUTHORIZED);
        }
        else {

            Game game = gameRepo.getOne(gameId);
            if(game == null) {
                return new ResponseEntity<String>("forbidden", HttpStatus.FORBIDDEN);
            }

            Integer playerSize = game.getGamePlayers().size();

            if(playerSize > 1) {
                return new ResponseEntity<String>("Game if full", HttpStatus.FORBIDDEN);
            }

            Player player = playerRepo.findByUsername(currentUsername);

            GamePlayer gp = new GamePlayer(game, player);
            gamePlayerRepository.save(gp);

            Long gpId = (Long) gp.getId();
            Map<String, String> body = new HashMap<>();
            body.put("gpid", gpId.toString());
            return new ResponseEntity<Object>(body, HttpStatus.CREATED);

        }

    }

    @RequestMapping(value="/api/games/players/{gamePlayerId}/ships", method=RequestMethod.POST)
    //body of the request should be parsed into a list of ships
    //Use ResponseEntity<Map<String,Object>> if you are returning a map for an JSON object
    public ResponseEntity<Map<String,Object>> addShips (@PathVariable Long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication) {
        //1. An Unauthorized response should be sent if there is no current user logged in
        String curUsername = playerRepo.findByUsername(authentication.getName()).getUsername();
        Player currentUsername = playerRepo.findByUsername(curUsername);
        if (currentUsername == null) {
            return new ResponseEntity<>(makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
        }
        //2. there is no game player with the given ID
        GamePlayer currentPlayer = gamePlayerRepository.findOne(gamePlayerId);
        if (currentPlayer == null) {
            return new ResponseEntity<>(makeMap("error", "game player with this id doesn't exist"), HttpStatus.UNAUTHORIZED);
        }
        //3. the current user is not the game player the ID references
        String currentPlayerName = currentPlayer.getPlayer().getUsername();
        if (currentPlayerName != currentUsername.getUsername()) {
            return new ResponseEntity<>(makeMap("error", "game player with this id doesn't exist"), HttpStatus.UNAUTHORIZED);
        }
        //4. A Forbidden response should be sent if the user already has ships placed
        Set currentPlayerShips =  currentPlayer.getShips();
        if(currentPlayerShips.size() != 0) {
            return new ResponseEntity<>(makeMap("error", "game player with this id doesn't exist"), HttpStatus.FORBIDDEN);
        }

        else {
            for(Ship s: ships) {

//                List shipsL = Arrays.asList(s.getLocations());
                ArrayList<String> shipLocations = new ArrayList<String>(s.getLocations());

                shipRepository.save(new Ship(s.getType(), shipLocations, s.getGamePlayer()));
            }
            return new ResponseEntity<>(makeMap("success", "ships are created"), HttpStatus.CREATED);

        }

    }


}



