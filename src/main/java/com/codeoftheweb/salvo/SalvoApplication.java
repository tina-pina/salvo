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
												ShipRepository shipRepository) {
		return (args) -> {

			Player p1 = new Player("", "", "j.bauer@ctu.gov");
			Player p2 = new Player("", "", "c.obrian@ctu.gov");
			Player p3 = new Player("", "", "t.almeida@ctu.gov");
			Player p4 = new Player("", "", "d.palmer@whitehouse.gov");

			playerRepo.save(p1);
			playerRepo.save(p2);
			playerRepo.save(p3);
			playerRepo.save(p4);

			Game g1 = new Game(new Date());
			Game g2 = new Game(new Date());
			Game g3 = new Game(new Date());
			Game g4 = new Game(new Date());
			Game g5 = new Game(new Date());
			Game g6 = new Game(new Date());

			gameRepo.save(g1);
			gameRepo.save(g2);
			gameRepo.save(g3);
			gameRepo.save(g4);
			gameRepo.save(g5);
			gameRepo.save(g6);

			GamePlayer GP1 = new GamePlayer(g1, p1);
			GamePlayer GP2 = new GamePlayer(g2, p2);
			GamePlayer GP3 = new GamePlayer(g2, p1);
			GamePlayer GP4 = new GamePlayer(g2, p2);
			GamePlayer GP5 = new GamePlayer(g3, p2);
			GamePlayer GP6 = new GamePlayer(g3, p3);
			GamePlayer GP7 = new GamePlayer(g4, p1);
			GamePlayer GP8 = new GamePlayer(g4, p2);
			GamePlayer GP9 = new GamePlayer(g5, p3);
			GamePlayer GP10 = new GamePlayer(g5, p1);
			GamePlayer GP11 = new GamePlayer(g6, p4);

			gamePlayerRepo.save(GP1);
			gamePlayerRepo.save(GP2);
			gamePlayerRepo.save(GP3);
			gamePlayerRepo.save(GP4);
			gamePlayerRepo.save(GP5);
			gamePlayerRepo.save(GP6);
			gamePlayerRepo.save(GP7);
			gamePlayerRepo.save(GP8);
			gamePlayerRepo.save(GP9);
			gamePlayerRepo.save(GP10);
			gamePlayerRepo.save(GP11);

			Ship S1 = new Ship("Aircraft Carrier", new ArrayList<String>(asList("B5", "B6", "B7", "B8", "B9")), GP1);
//			Ship S2 = new Ship("Battleship", new ArrayList<String>(asList("D4", "D5", "D6", "D7")) , GP1);
//			Ship S3 = new Ship("Submarine", new ArrayList<String>(asList("F3", "G3", "H3")) , GP1);
//			Ship S4 = new Ship("Destroyer", new ArrayList<String>(asList("F5", "G5", "H5")) , GP1);
//			Ship S5 = new Ship("Patrol Boat", new ArrayList<String>(asList("I8", "I9")) , GP1);

			Ship S11 = new Ship("Aircraft Carrier", new ArrayList<String>(asList("C2", "C3", "C4", "C5", "C6")) , GP2);
//			Ship S12 = new Ship("Battleship", new ArrayList<String>(asList("E2", "E3", "E4", "E5")) , GP2);
//			Ship S13 = new Ship("Submarine", new ArrayList<String>(asList("G2", "H2", "I2")) , GP2);
//			Ship S14 = new Ship("Destroyer", new ArrayList<String>(asList("G7", "G8", "G9")) , GP2);
//			Ship S15 = new Ship("Patrol Boat", new ArrayList<String>(asList("J9", "J10")) , GP2);

			shipRepository.save(S1);
//			shipRepository.save(S2);
//			shipRepository.save(S3);
//			shipRepository.save(S4);
//			shipRepository.save(S5);
			shipRepository.save(S11);
//			shipRepository.save(S12);
//			shipRepository.save(S13);
//			shipRepository.save(S14);
//			shipRepository.save(S15);

		};
	}
}

