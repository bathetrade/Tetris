package pieces;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import point.Point;
import tetrisgame.TetrisGame;

//This class represents an individual "square" on the game board.
public class GameBoardSquare {
	
	private boolean isOccupied;
	private Color color;
	public enum MoveType {  //type of movement
		MOVE_DOWN,
		MOVE_RIGHT,
		MOVE_LEFT
	}
	public GameBoardSquare() {
		isOccupied = false;
		color      = Color.white;
	}
	
	public GameBoardSquare(Color color) {
		this.color = color;
		isOccupied = true;
	}
	
	public boolean isOccupied() {
		return isOccupied;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * Sets the current GameBoardSquare to the specified color.
	 * @param color - The square's color.
	 * @return Returns a boolean indicating whether the square was set. If the square has already been set,
	 * then it returns false. This is primarily for the spawnPiece() function; if the GameBoard is full, and 
	 * we try to spawn a piece on top of squares that are already set, then the function returns false and
	 * the game is over.
	 */
	public boolean setSquare(Color color) {
		if (isOccupied)
			return false;
		else {
			isOccupied = true;
			this.color = color;
			return true;
		}
	}
	public void unsetSquare() {
		this.isOccupied = false;
	}
	public Point boardToScreen(int row, int col, GameContainer container) {
		Point screenCenter = new Point(container.getWidth()/2, container.getHeight()/2);
		int hbw            = (TetrisGame.pieceSize*TetrisGame.blockWidth)/2;
		int hbh            = (TetrisGame.pieceSize*TetrisGame.blockHeight)/2;
		Point topLeftBoard = new Point(screenCenter.x - hbw, screenCenter.y - hbh);
		int sx          = topLeftBoard.x + col*TetrisGame.pieceSize;
		int sy          = topLeftBoard.y + row*TetrisGame.pieceSize;
		return new Point(sx,sy);
	}
	public void render(GameContainer container, Graphics g, int row, int col) {
		if (isOccupied) {
			g.setColor(color);
			int size = TetrisGame.pieceSize;
			Point p  = boardToScreen(row, col, container);
			g.fillRect(p.x, p.y, size, size);
		}
	}
}
