package ec.edu.uce.Galaga;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

import ec.edu.uce.Galaga.view.GameFrame;

//@SpringBootApplication
public class Main {

	public static void main(String[] args) {
		//SpringApplication.run(Main.class, args);

		GameFrame game = new GameFrame("Galaga");
		game.setVisible(true);

	}

}
