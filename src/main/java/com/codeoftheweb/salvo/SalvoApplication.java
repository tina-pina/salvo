package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Date;

import static java.util.Arrays.asList;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initGamePlayerData(PlayerRepository playerRepo,
                                              GameRepository gameRepo,
                                              GamePlayerRepository gamePlayerRepo,
                                              ShipRepository shipRepo,
                                              SalvoRepository salvoRepo,
                                              ScoreRepository scoreRepo) {
		return (args) -> {

      Player p1 = new Player("", "", "j.bauer@ctu.gov", "24"); // playing game 1
      Player p2 = new Player("", "", "c.obrian@ctu.gov", "42" ); // playing game 1,2
      Player p3 = new Player("", "", "t.almeida@ctu.gov", "kb"); // playing game 2
      Player p4 = new Player("", "", "d.palmer@whitehouse.gov", "mole"); // playing game 2
      playerRepo.save(p1);
			playerRepo.save(p2);
			playerRepo.save(p3);
			playerRepo.save(p4);

      Game g1 = new Game(new Date()); // played by player 1,2
      Game g2 = new Game(new Date()); // played by player 2,3,4
      gameRepo.save(g1);
			gameRepo.save(g2);

      GamePlayer GP1 = new GamePlayer(g1, p1); // has 5 ships, has 2 salvos
      GamePlayer GP2 = new GamePlayer(g1, p2); // has 0 ship, has 2 salvos
      GamePlayer GP3 = new GamePlayer(g2, p2); // has 5 ships,
      GamePlayer GP4 = new GamePlayer(g2, p3); // has 0 ship
      GamePlayer GP5 = new GamePlayer(g2, p4); // has 0 ship
      gamePlayerRepo.save(GP1);
      gamePlayerRepo.save(GP2);
      gamePlayerRepo.save(GP3);
      gamePlayerRepo.save(GP4);
      gamePlayerRepo.save(GP5);

      Ship S1 = new Ship("Aircraft Carrier", new ArrayList<String>(asList("B5", "B6", "B7", "B8", "B9")), GP1);
			Ship S2 = new Ship("Battleship", new ArrayList<String>(asList("D4", "D5", "D6", "D7")) , GP1);
			Ship S3 = new Ship("Submarine", new ArrayList<String>(asList("F3", "G3", "H3")) , GP1);
			Ship S4 = new Ship("Destroyer", new ArrayList<String>(asList("F5", "G5", "H5")) , GP1);
			Ship S5 = new Ship("Patrol Boat", new ArrayList<String>(asList("I8", "I9")) , GP1);
			Ship S11 = new Ship("Aircraft Carrier", new ArrayList<String>(asList("C2", "C3", "C4", "C5", "C6")) , GP3);
			Ship S12 = new Ship("Battleship", new ArrayList<String>(asList("E2", "E3", "E4", "E5")) , GP3);
			Ship S13 = new Ship("Submarine", new ArrayList<String>(asList("G2", "H2", "I2")) , GP3);
			Ship S14 = new Ship("Destroyer", new ArrayList<String>(asList("G7", "G8", "G9")) , GP3);
			Ship S15 = new Ship("Patrol Boat", new ArrayList<String>(asList("J9", "J10")) , GP3);
			shipRepo.save(S1);
      shipRepo.save(S2);
      shipRepo.save(S3);
      shipRepo.save(S4);
      shipRepo.save(S5);
      shipRepo.save(S11);
      shipRepo.save(S12);
      shipRepo.save(S13);
      shipRepo.save(S14);
      shipRepo.save(S15);


			Salvo Sa1 = new Salvo(new ArrayList<String>(asList("C2", "C3", "C4", "C5", "C6")), GP1,  1);
      Salvo Sa2 = new Salvo(new ArrayList<String>(asList("C1", "C5", "D1", "A1", "B4")), GP1,  2);
      Salvo Sa3 = new Salvo(new ArrayList<String>(asList("B1", "D5", "D1", "A2", "B6")), GP2,  1);
      Salvo Sa4 = new Salvo(new ArrayList<String>(asList("C4", "C6", "D4", "A3", "B5")), GP2,  2);
      salvoRepo.save(Sa1);
      salvoRepo.save(Sa2);
      salvoRepo.save(Sa3);
      salvoRepo.save(Sa4);


//      scores player:
//      lose: 0 points;
//      tie: 0.5 points;
//      win: 1 point;

      Score Sc1 = new Score(g1, p1, 0.5, new Date());
      Score Sc2 = new Score(g1, p2, 0.5, new Date());

      Score Sc3 = new Score(g2, p2, 0, new Date());
      Score Sc4 = new Score(g2, p3, 1, new Date());
      Score Sc5 = new Score(g2, p4, 0, new Date());

      scoreRepo.save(Sc1);
      scoreRepo.save(Sc2);
      scoreRepo.save(Sc3);
      scoreRepo.save(Sc4);
      scoreRepo.save(Sc5);

		};
	}
}

