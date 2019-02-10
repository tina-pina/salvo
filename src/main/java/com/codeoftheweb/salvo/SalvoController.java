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

    @Autowired
    private ScoreRepository scoreRepository;


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
    private Map<String, Object> createShipObj(Ship ship) {
        Map<String, Object> oneShip = new LinkedHashMap<String, Object>();
        oneShip.put("type", ship.getType());
        oneShip.put("locations", ship.getLocations());
        return oneShip;
    }

    private Map<String, Object> createSalvoGpObj(GamePlayer gamePlayer) {
        Map<String, Object> gpSalvo = new LinkedHashMap<String, Object>();
        gpSalvo.put("gamePlayerId", gamePlayer.getId());
        gpSalvo.put("gamePlayerEmail", gamePlayer.getPlayer().getUsername());
        gpSalvo.put("salvos", gamePlayer.getSalvos().stream()
          .collect(Collectors.toMap(Salvo::getTurnNum, Salvo::getLocations)));
        return gpSalvo;
    }

    //create game object
    public Map<String, Object> createGameObj(Game game) {
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


    private Map<String, Object> createHits(Salvo salvo, String userName) {
        /* How many hits are made by our salvo for each of opponents' ships */

        Map<String, Object> shipObj = new HashMap<String, Object>();

        GamePlayer opponentPlayer = salvo.getGamePlayer().getGame().getGamePlayers().stream().filter(gp -> gp.getPlayer().getUsername() != userName).findAny().orElse(null);
        if (opponentPlayer == null) {
            return shipObj;
        }

        Set<Ship> opponentShips = opponentPlayer.getShips();
        if (opponentShips.size() < 5) {
            return shipObj;
        }

        String[] arrTypeArr = new String[]{"AircraftCarrier", "Battleship", "Submarine", "Destroyer", "PatrolBoat"};
        for (String shipType : arrTypeArr) {
            Ship targetShip = opponentShips.stream().filter(s -> s.getType() == shipType).findAny().orElse(null);
            if (targetShip == null) {
                break;
            }

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
          .collect(Collectors.toMap(
            salvo -> salvo.getTurnNum().toString(),
            salvo -> createHits(salvo, userName)
          )));
        return obj;
    }

    /*
     * all games (no login needed)
     * */
    @RequestMapping(path = "/api/games", method = RequestMethod.POST)
    public ResponseEntity<Object> createGame(Authentication authentication) {

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

    private String isGameFinished(GamePlayer currentPlayer, GamePlayer opponent) {

        /* get all current players ships and salvos*/
        Set<Ship> currPlayerShips = currentPlayer.getShips();
        Set<Ship> opponShips = opponent.getShips();

        /* get all opponent ships and salvos*/
        Set<Salvo> currPlayerSalvos = currentPlayer.getSalvos();
        Set<Salvo> opponSalvos = opponent.getSalvos();

        /* get all current players ships locations*/
        List<String> allCurrPlayerShipLocs = currPlayerShips.stream()
          .map(ship -> ship.getLocations())
          .flatMap(Collection::stream)
          .collect(Collectors.toList());

        /* get all opponent ships locations*/
        List<String> allOpponentShipLocs = opponShips.stream()
          .map(ship -> ship.getLocations())
          .flatMap(Collection::stream)
          .collect(Collectors.toList());

        /* get all current players salvos locations*/
        List<String> allCurrPlayerSalvosLocs = currPlayerSalvos.stream()
          .map(salvo -> salvo.getLocations())
          .flatMap(Collection::stream)
          .collect(Collectors.toList());

        /* get all opponent salvos locations*/
        List<String> allOpponentSalvosLocs = opponSalvos.stream()
          .map(salvo -> salvo.getLocations())
          .flatMap(Collection::stream)
          .collect(Collectors.toList());

        /*check if current players ship locations are inside the opponent salvos loc*/
        List<String> allCurrPlayerShipLocsAttacked = allCurrPlayerShipLocs.stream().filter(loc -> allOpponentSalvosLocs.contains(loc)).collect(Collectors.toList());

        /*check if opponent ship locations are inside the current players salvos loc*/
        List<String> allOpponentShipLocsAttacked = allOpponentShipLocs.stream().filter(loc -> allCurrPlayerSalvosLocs.contains(loc)).collect(Collectors.toList());

        Boolean currPlayerShipsSunk = false;
        Boolean opponPlayerShipsSunk = false;

        /* check if all the original locations of curr players ships are attacked -> check size attacked ones and original ones*/

        if (allCurrPlayerShipLocs.size() == allCurrPlayerShipLocsAttacked.size()) currPlayerShipsSunk = true;
        if (allOpponentShipLocs.size() == allOpponentShipLocsAttacked.size()) opponPlayerShipsSunk = true;

        // tie
        if (currPlayerShipsSunk && opponPlayerShipsSunk) return "tie";
        // current Player wins
        else if (!currPlayerShipsSunk && opponPlayerShipsSunk) return "win";
        // opponent wins
        else if (currPlayerShipsSunk && !opponPlayerShipsSunk) return "lose";
        // game not finished
        else return "continue";
    }

    private void registerScore(GamePlayer gp, GamePlayer opponent, double gpScore, double opponScore) {
        if(gp.getScore() == null) {
            Score scoreGp = new Score(gp.getGame(), gp.getPlayer(), gpScore, gp.getGame().getDate());
            scoreRepository.save(scoreGp);
        }
        if(opponent.getScore() == null) {
            Score scoreOpp = new Score(opponent.getGame(), opponent.getPlayer(), opponScore, opponent.getGame().getDate());
            scoreRepository.save(scoreOpp);
        }
    }

    private Object currentState(Long gamePlayerId, Integer turn) {
        HashMap<String, Object> stateObj = new HashMap<String, Object>();

        GamePlayer gp = gamePlayerRepository.findOne(gamePlayerId);
        GamePlayer opponent = gp.getGame().getGamePlayers().stream().filter(gamepl -> gamepl.getId() != gp.getId()).findAny().orElse(null);

        /* Check if there is an opponent */
        if (opponent == null) {
            stateObj.put("message", "no opponent yet");
            stateObj.put("state", 0);
            return stateObj;
        }

        /* Check if ships are placed */
        Set<Ship> gamePlayerShips = gp.getShips();
        if (gamePlayerShips.size() < 5) {
            stateObj.put("message", "place ships first!");
            stateObj.put("state", 1);
            return stateObj;
        } else if (opponent.getShips().size() < 5) {
            stateObj.put("message", "wait for opponents' ships");
            stateObj.put("state", 2);
            return stateObj;
        }

        /* Check gameFinished */
        String gameFinished = isGameFinished(gp, opponent);
        if (gameFinished == "win") {
            registerScore(gp, opponent, 1.0, 0.0);
            stateObj.put("message", "game finished! you won!");
            stateObj.put("state", 3);
            return stateObj;
        } else if (gameFinished == "lose") {
            registerScore(gp, opponent, 0.0, 1.0);
            stateObj.put("message", "game finished! you lost!");
            stateObj.put("state", 4);
            return stateObj;
        } else if (gameFinished == "tie") {
            registerScore(gp, opponent, 0.5, 0.5);
            stateObj.put("message", "game finished! tie! good match!!");
            stateObj.put("state", 5);
            return stateObj;
        }

        /* Check if salvos are placed */
        Salvo currTurnGpSalvo = gp.getSalvos().stream().filter(salvo -> salvo.getTurnNum() == turn).findAny().orElse(null);
        Salvo currTurnOppSalvo = opponent.getSalvos().stream().filter(salvo -> salvo.getTurnNum() == turn).findAny().orElse(null);
        if (currTurnGpSalvo == null) {
            stateObj.put("message", "place salvo for turn " + turn + " !");
            stateObj.put("state", 6);
            return stateObj;
        }
        if (currTurnOppSalvo == null) {
            stateObj.put("message", "wait for opponents' salvo!");
            stateObj.put("state", 7);
            return stateObj;
        }

        stateObj.put("message", "turn " + turn + " is completed. Going to the next turn!");
        stateObj.put("state", 8);
        return stateObj;

    }

    private Object getSalvoResult(Salvo s, Set<Ship> ships) {

//      { "Destroyer" {"hits": 1}}

        HashMap<String, Object> shipObj = new HashMap<>();
        List<String> salvoLocations = s.getLocations();

        for (Ship ship : ships) {
            HashMap<String, Object> hitsLeftObj = new HashMap<>();
            String shipName = ship.getType();
            List<String> shipLocs = ship.getLocations();
            List<String> shipAttacked = shipLocs.stream().filter(loc -> salvoLocations.contains(loc)).collect(Collectors.toList());

            int attackedShipLen = shipAttacked.size();
            hitsLeftObj.put("hits", attackedShipLen);
            shipObj.put(shipName, hitsLeftObj);

        }

        return shipObj;

    }

    private Object createNewHits(Set<Salvo> currentGPSalvos, Set<Ship> opponentShips) {

        // {1: { "Destroyer" {"hits": 1}} }

        return currentGPSalvos.stream().collect(Collectors.toMap(
          s -> s.getTurnNum(),
          s -> getSalvoResult(s, opponentShips)
        ));

    }

    private Object createNewLeft(Set<Salvo> opponentSalvos, Set<Ship> currentGPShips) {

        return opponentSalvos.stream().collect(Collectors.toMap(
          s -> s.getTurnNum(),
          s -> getSalvoResult(s, currentGPShips)
        ));

    }

    private Object createYourShipsLeft(int currentTurn, Set<Salvo> salvos, Set<Ship> ships) {

        int shipSunk = 0;

        /* get all opponent salvos locations in a list*/
        List<String> allSalvoLoc = salvos.stream()
          .filter(s -> s.getTurnNum() <= currentTurn)
          .map(s -> s.getLocations())
          .flatMap(Collection::stream)
          .collect(Collectors.toList());

        /* iterate all salvos to check if  */
        for (Ship ship : ships) {
            List<String> shipLocInSalvo = ship.getLocations().stream().filter(loc -> allSalvoLoc.contains(loc)).collect(Collectors.toList());
            if (shipLocInSalvo.size() == ship.getLocations().size()) shipSunk += 1;
        }
        return 5 - shipSunk;
    }

    private int findCurrentTurn(GamePlayer gp, GamePlayer op) {

        int cpTurn = gp.getSalvos().stream().map(s -> s.getTurnNum()).collect(Collectors.toList()).size();
        int opTurn = op.getSalvos().stream().map(s -> s.getTurnNum()).collect(Collectors.toList()).size();

        if (cpTurn == opTurn) {
            cpTurn++;
            return cpTurn;
        }
        else if (cpTurn > opTurn) return cpTurn;
        else return opTurn;

    }

    /*
     * the game player defined in the url in all the recent games with other game players
     * */
    @RequestMapping("/api/game_view/{gamePlayerId}")
    public Object getGameView(@PathVariable Long gamePlayerId, Authentication authentication) {

        String currentUsername = authentication.getName();
        GamePlayer currentGP = gamePlayerRepository.findOne(gamePlayerId);
        GamePlayer opponent = currentGP.getGame().getGamePlayers().stream()
          .filter(gamepl -> gamepl.getId() != currentGP.getId())
          .findAny().orElse(null);

        int currentTurn;
        if(opponent == null) currentTurn = 1;
        else currentTurn = findCurrentTurn(currentGP, opponent);

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
            gpObj.put("gameState", currentState(gamePlayerId, currentTurn));
            gpObj.put("turn", currentTurn);

            if(opponent != null) {
                // you want to get hits - the result of your salvos
                // to get this, you need your salvos & opponents' ships
                gpObj.put("yourAttackHistory", createNewHits(currentGP.getSalvos(), opponent.getShips()));

                // you want to get left - the result of opponents' salvo
                // to get this, you need opponents' salvos & your ships
                gpObj.put("opponentAttackHistory", createNewLeft(opponent.getSalvos(), currentGP.getShips()));

                gpObj.put("yourShipsLeft", opponent.getSalvos().stream().collect(Collectors.toMap(
                  s -> s.getTurnNum(),
                  s -> createYourShipsLeft(s.getTurnNum(), opponent.getSalvos(), currentGP.getShips()))
                ));
                gpObj.put("opponentShipsLeft", currentGP.getSalvos().stream().collect(Collectors.toMap(
                  s -> s.getTurnNum(),
                  s -> createYourShipsLeft(s.getTurnNum(), currentGP.getSalvos(), opponent.getShips()))
                ));
            }

            return new ResponseEntity<Object>(gpObj, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<Object>(makeMap("error", "unauthorized"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping("/api/game/{gameId}/players")
    public Object joinGame(@PathVariable Long gameId, Authentication authentication) {
        String currentUsername = authentication.getName();
        if (currentUsername == null) {
            return new ResponseEntity<String>("not logged-in", HttpStatus.UNAUTHORIZED);
        } else {

            Game game = gameRepo.getOne(gameId);
            if (game == null) {
                return new ResponseEntity<String>("forbidden", HttpStatus.FORBIDDEN);
            }

            Integer playerSize = game.getGamePlayers().size();

            if (playerSize > 1) {
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

    @RequestMapping(value = "/api/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    //body of the request should be parsed into a list of ships
    //Use ResponseEntity<Map<String,Object>> if you are returning a map for an JSON object
    public ResponseEntity<Map<String, Object>> addShips(@PathVariable Long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication) {
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
        Set<Ship> currentPlayerShips = currentPlayer.getShips();
        if (currentPlayerShips.size() != 0) {
            return new ResponseEntity<>(makeMap("error", "game player with this id doesn't exist"), HttpStatus.FORBIDDEN);
        }


        ///check if all ships are placed
        if (ships.size() < 5) {
            return new ResponseEntity<Map<String, Object>>(makeMap("error", "not enough ships are placed!"), HttpStatus.FORBIDDEN);
        }

        // //  // // // // // //
        if (ships.size() != 5) {
            return new ResponseEntity<Map<String, Object>>(makeMap("error", "five ships minimum need to be sent"), HttpStatus.FORBIDDEN);
        }


        //Otherwise, the ship should be added to the game player and saved, and a Created response should be sent.

        GamePlayer gp = gamePlayerRepository.findOne(gamePlayerId);

        for (Ship s : ships) {
            ArrayList<String> shipLocations = new ArrayList<String>(s.getLocations());

            shipRepository.save(new Ship(s.getType(), shipLocations, gp));
        }
        return new ResponseEntity<>(makeMap("success", "ships are created"), HttpStatus.CREATED);

    }

    @RequestMapping(value = "/api/games/players/{gamePlayerId}/salvos", method = RequestMethod.POST)
    //body request should be parsed into a salvo object consisting of turn and list of locations
    public ResponseEntity<Map<String, Object>> addSalvos(@PathVariable Long gamePlayerId, @RequestBody Salvo salvo, Authentication authentication) {
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
        //not allowed to fire more than one salvo in a turn
        //check if the current players salvos for one turn are already in the database -> means not null
        Salvo currTurn = currentPlayer.getSalvos().stream().filter(s -> s.getTurnNum() == salvo.getTurnNum()).findAny().orElse(null);
        if (currTurn != null) {
            return new ResponseEntity<Map<String, Object>>(makeMap("error", "already used salvo for this turn before"), HttpStatus.FORBIDDEN);
        }

        int currTurnNum = salvo.getTurnNum();
        Salvo salvoBefore = currentPlayer.getSalvos().stream().filter(s -> s.getTurnNum() == currTurnNum - 1).findAny().orElse(null);
        if (salvoBefore == null && salvo.getTurnNum() > 1) {
            return new ResponseEntity<Map<String, Object>>(makeMap("error", "turn number not correct"), HttpStatus.FORBIDDEN);
        }


        //4. A Forbidden response should be sent if the user already has submitted a salvo for the turn listed
        // Post method from frontend sending data to this request method in the backend -> user
        Integer userTurn = salvo.getTurnNum();
        // get salvos from the user already existing in the database
        Set<Salvo> gamePlayerSalvos = currentPlayer.getSalvos();
        for (Salvo s : gamePlayerSalvos) {
            if (s.getTurnNum() == userTurn) {
                return new ResponseEntity<>(makeMap("error", "salvo already existing!"), HttpStatus.FORBIDDEN);
            }
        }

        ArrayList<String> salvoLocations = new ArrayList<String>(salvo.getLocations());
        salvoRepository.save(new Salvo(salvoLocations, currentPlayer, salvo.getTurnNum()));

        return new ResponseEntity<>(makeMap("success", "salvos are created"), HttpStatus.CREATED);
    }

}




