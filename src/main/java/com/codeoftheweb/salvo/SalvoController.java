package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private SalvoRepository salvoRepository;


    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private Map<String, Object> makeMap(String key, Object value) {
      Map<String, Object> map = new HashMap<>();
      map.put(key, value);
      return map;
    }

    // create player object
    private Map<String, Object> createPlayerObj(Player player) {
      Map<String, Object> playerObj = new HashMap<String, Object>();
      playerObj.put("id", player.getId());
      playerObj.put("email", player.getUsername());
      return playerObj;
    }

    //create shipsObj
    private  Map<String, Object> createShipObj(Ship ship) {
      Map<String, Object> oneShip = new LinkedHashMap<String, Object>();
      oneShip.put("type", ship.getType());
      oneShip.put("locations", ship.getLocations());
      return oneShip;
    }

    private Map<String, Object> createSalvoGpObj (GamePlayer gamePlayer) {
      Map<String, Object> gpSalvo = new LinkedHashMap<String, Object>();
      gpSalvo.put("gamePlayerId", gamePlayer.getId());
      gpSalvo.put("gamePlayerEmail", gamePlayer.getPlayer().getUsername());
      gpSalvo.put("salvos", gamePlayer.getSalvos().stream()
        .collect(Collectors.toMap(Salvo::getTurnNum, Salvo::getLocations)));
      return gpSalvo;
    }

    //create game object
    public Map<String, Object> createGameObj (Game game) {
      Map<String, Object> newGame = new HashMap<String, Object>();
      newGame.put("id", game.getId());
      newGame.put("created", game.getDate());
      newGame.put("gamePlayers", getGamePlayers(game));
      return newGame;
    }

    // fill the game player object with id, score, player
    private Map<String, Object> createGpObj(GamePlayer gamePlayer) {
      Map<String, Object> gp = new HashMap<String, Object>();
      gp.put("id", gamePlayer.getId());
      gp.put("player", createPlayerObj(gamePlayer.getPlayer()));

      try {
        gp.put("score", gamePlayer.getScore().getScore());
      } catch (NullPointerException ne) {
        gp.put("score", 0.0);
      }

      return gp;
    }

    //get gamePlayers in a game
    private List<Object> getGamePlayers(Game game) {
      return game.getGamePlayers().stream().map(gp -> createGpObj(gp)).collect(Collectors.toList());
    }

    // get games
    private List<Object> getGames() {
      return gameRepo.findAll().stream().map(game -> createGameObj(game)).collect(Collectors.toList());
    }


    private Map<String, Object> createHits(Salvo salvo, String userName) {
      /* How many hits are made by our salvo for each of opponents' ships */

      GamePlayer opponentPlayer = salvo.getGamePlayer().getGame().getGamePlayers().stream().filter(gp -> gp.getPlayer().getUsername() != userName).findAny().get();

      Set<Ship> opponentShips = opponentPlayer.getShips();
      String[] arrTypeArr = new String[] {"AircraftCarrier", "Battleship", "Submarine", "Destroyer", "PatrolBoat"};

      Map<String, Object> shipObj = new HashMap<String, Object>();

      for(String shipType: arrTypeArr) {
          Ship targetShip = opponentShips.stream().filter(s -> s.getType() == shipType).findAny().get();
          List<String> targetHits = targetShip.getLocations().stream().filter(loc -> salvo.getLocations().contains(loc)).collect(Collectors.toList());

          Map<String, Object> obj = new HashMap<String, Object>();
          obj.put("hits", targetHits.size());
          obj.put("left", targetShip.getLocations().size() - targetHits.size());

          shipObj.put(shipType, obj);
      }

      return shipObj;
    }

    private Map<String, Object> createLeft(Salvo salvo, String userName) {
      /* How many ships are hit by opponents' salvo */

      GamePlayer gamePlayer = salvo.getGamePlayer().getGame().getGamePlayers().stream().filter(gp -> gp.getPlayer().getUsername() == userName).findAny().get();

      Set<Ship> ourShips = gamePlayer.getShips();
      String[] arrTypeArr = new String[] {"AircraftCarrier", "Battleship", "Submarine", "Destroyer", "PatrolBoat"};

      Map<String, Object> shipObj = new HashMap<String, Object>();
      for(String shipType: arrTypeArr) {
        Ship targetShip = ourShips.stream().filter(s -> s.getType().equals(shipType)).findAny().get();
        List<String> targetHits = targetShip.getLocations().stream().filter(loc -> salvo.getLocations().contains(loc)).collect(Collectors.toList());

        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("hits", targetHits.size());
        obj.put("left", targetShip.getLocations().size() - targetHits.size());

        shipObj.put(shipType, obj);
      }

      return shipObj;
    }

    private Map<String, Object> createHitsLeft(GamePlayer gp, String userName) {
      GamePlayer gamePlayer = gp.getGame().getGamePlayers().stream().filter(_gp -> _gp.getPlayer().getUsername().equals(userName)).findAny().get();
      Map<String, Object> obj = new HashMap<String, Object>();
      obj.put("hitsLeft", gamePlayer.getSalvos().stream()
        //.map(salvo -> createHits(salvo, userName))
        .collect(Collectors.toMap(
          salvo -> salvo.getTurnNum().toString(),
          salvo -> createHits(salvo, userName)
        )));
//      obj.put("left", gamePlayer.getSalvos().stream()
//        //.map(salvo -> createHits(salvo, userName))
//        .collect(Collectors.toMap(
//          salvo -> salvo.getTurnNum().toString(),
//          salvo -> createHits(salvo, userName)
//        )));
      return obj;
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

    @RequestMapping("/api/games")
    public Map<String, Object> getAllGames() {
      HashMap<String, Object> gamesObj = new HashMap<String, Object>();
      gamesObj.put("games", gameRepo.findAll().stream()
        .map(game -> createGameObj(game)).collect(Collectors.toList()));
      return gamesObj;
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
        if (isGuest(authentication)) return "Login First";

        Player player = playerRepo.findByUsername(authentication.getName());

        HashMap<String, Object> playerObj = new HashMap<String, Object>();
        playerObj.put("player", createPlayerObj(player));

        playerObj.put("games", player.getGamePlayers().stream()
          .map(gp -> createGameObj(gp.getGame()))
          .collect(Collectors.toList()));

        playerObj.put("ships", player.getGamePlayers().stream()
            .map(gp -> gp.getShips())
            .flatMap(Collection::stream)
            .map(ship -> createShipObj(ship))
            .collect(Collectors.toList()));

        playerObj.put("salvos", player.getGamePlayers().stream()
          .map(gp -> createSalvoGpObj(gp))
          .collect(Collectors.toList()));

        return playerObj;
    }


    /*
     * the game player defined in the url in all the recent games with other game players
     * */
    @RequestMapping("/api/game_view/{gamePlayerId}")
    public Object getGameView(@PathVariable Long gamePlayerId, Authentication authentication) {

        String currentUsername = authentication.getName();
        String gameviewPlayer = gamePlayerRepository.findOne(gamePlayerId).getPlayer().getUsername();
        if (currentUsername.equals(gameviewPlayer)) {

            Map<String, Object> gpObj = new LinkedHashMap<String, Object>();
            GamePlayer gp = gamePlayerRepository.findOne(gamePlayerId);

            gpObj.put("id", gp.getGame().getId());
            gpObj.put("created", gp.getGame().getDate());
            gpObj.put("gamePlayers", gp.getGame().getGamePlayers().stream()
              .map(gamePl -> createGpObj(gamePl))
              .collect(Collectors.toList()));
            gpObj.put("ships", gp.getShips().stream()
              .map(oneShip -> createShipObj(oneShip))
              .collect(Collectors.toList()));
            gpObj.put("salvos", gp.getGame().getGamePlayers().stream()
              .map(gamePl -> createSalvoGpObj(gamePl))
              .collect(Collectors.toList()));
            gpObj.put("hitsLeftShips", gamePlayerRepository.findOne(gamePlayerId).getGame().getGamePlayers().stream()
              //.map(gamePl -> createHitsLeft(gamePl, gamePl.getPlayer().getUsername()))
              .collect(Collectors.toMap(
                  gamePl -> gamePl.getPlayer().getUsername(),
                  gamePl -> createHitsLeft(gamePl, gamePl.getPlayer().getUsername())))
            );

            return new ResponseEntity<Object>(gpObj, HttpStatus.ACCEPTED);
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
        Set<Ship> currentPlayerShips =  currentPlayer.getShips();
        if(currentPlayerShips.size() != 0) {
            return new ResponseEntity<>(makeMap("error", "game player with this id doesn't exist"), HttpStatus.FORBIDDEN);
        }

        //Otherwise, the ship should be added to the game player and saved, and a Created response should be sent.

        GamePlayer gp = gamePlayerRepository.findOne(gamePlayerId);

        for(Ship s: ships) {
            ArrayList<String> shipLocations = new ArrayList<String>(s.getLocations());

            shipRepository.save(new Ship(s.getType(), shipLocations, gp));
        }
        return new ResponseEntity<>(makeMap("success", "ships are created"), HttpStatus.CREATED);

    }

    @RequestMapping(value="/api/games/players/{gamePlayerId}/salvos", method=RequestMethod.POST)
    //body request should be parsed into a salvo object consisting of turn and list of locations
    public ResponseEntity<Map<String,Object>> addSalvos (@PathVariable Long gamePlayerId, @RequestBody Salvo salvo, Authentication authentication) {
        // {turn: 1, locations: ["A1", "B1", ...]}
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
        //4. A Forbidden response should be sent if the user already has submitted a salvo for the turn listed
        // Post method from frontend sending data to this request method in the backend -> user
        Integer userTurn = salvo.getTurnNum();
        // get salvos from the user already existing in the database
        Set<Salvo> gamePlayerSalvos = currentPlayer.getSalvos();
        for(Salvo s: gamePlayerSalvos) {
            if(s.getTurnNum() == userTurn) {
                return new ResponseEntity<>(makeMap("error", "salvo already existing!"), HttpStatus.FORBIDDEN);
            }
        }

        ArrayList<String> salvoLocations = new ArrayList<String>(salvo.getLocations());
        salvoRepository.save(new Salvo(salvoLocations, currentPlayer, salvo.getTurnNum()));

        return new ResponseEntity<>(makeMap("success", "salvos are created"), HttpStatus.CREATED);
    }

}




