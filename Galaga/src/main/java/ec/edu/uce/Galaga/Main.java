package ec.edu.uce.Galaga;

import ec.edu.uce.Galaga.view.GameFrame;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
public class Main {

	public static void main(String[] args) {
		//SpringApplication.run(Main.class, args);

		GameFrame game = new GameFrame("Galaga");
		game.setVisible(true);

	}

}
