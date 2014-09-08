package pieces;

//import org.newdawn.slick.Color;
//import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import point.Point;
import tetrisgame.TetrisGame;

//This class represents an individual "square" on the game board.
public class GameBoardSquare {
	
	private boolean isSet;
	private Image color;
	//private Color color;
	
	
	public enum MoveType {  //type of movement
		MOVE_DOWN,
		MOVE_RIGHT,
		MOVE_LEFT
	}
	
	public GameBoardSquare() {
		isSet = false;
		this.color = null;
	}
	
	public GameBoardSquare(Image color) {
		this.color = color;
		isSet = true;
	}
	
	
	public boolean isSet() {
		return isSet;
	}

	
	public Image getColor() {
		return color;
	}

	
	public void setColor(Image color) {
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
	
	public boolean setSquare(Image color) {
		if (isSet)
			return false;
		else {
			isSet = true;
			this.color = color;
			return true;
		}
	}
	
	
	public void clearSquare() {
		isSet = false;
		//color = null;
	}
	
	
	public static Point boardToScreen(int row, int col) {
		
		int screenX = TetrisGame.boardToScreenOffsetVector.x + col * TetrisGame.pieceSize;
		int screenY = TetrisGame.boardToScreenOffsetVector.y + row * TetrisGame.pieceSize;
		
		return new Point(screenX,screenY);
	}
	
	
	public static Point screenToBoard(float screenX, float screenY) {
		
		//Translate the playing area space to the top left of the entire window.
		screenX = screenX - TetrisGame.boardToScreenOffsetVector.x;
		screenY = screenY - TetrisGame.boardToScreenOffsetVector.y;
		
		int logicX = Math.round(screenX / TetrisGame.pieceSize);
		int logicY = Math.round(screenY / TetrisGame.pieceSize);
		
		return new Point(logicY, logicX);
		
	}
	
	
	public void render(Graphics g, int row, int col) {
		
		//Don't render the invisible rows
		if (row < TetrisGame.numInvisRows)
			return;
		
		if (isSet) {
			//g.setColor(this.color);
			Point p  = boardToScreen(row, col);
			//g.fillRect(p.x, p.y, size, size);
			g.drawImage(this.color, p.x, p.y);
		}
	}
}
