package main;

import org.newdawn.slick.SlickException;
import tetrisgame.TetrisGame;
import org.newdawn.slick.AppGameContainer;


public class AppMain {
	
	public static void main(String[] args) {
		TetrisGame game = new TetrisGame("Tetris");
		try {
			AppGameContainer app = new AppGameContainer(game, 800, 600, false);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
	}

}
