package pieces;

import org.newdawn.slick.Color;
//import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import point.Point;
import point.Vec2D;
import tetrisgame.TetrisGame;

//This class represents an individual "square" on the game board.
public class GameBoardSquare {
	
	private boolean isSet;
	private Color color;
	
	
	public enum MoveType {  //type of movement
		MOVE_DOWN,
		MOVE_RIGHT,
		MOVE_LEFT
	}
	public GameBoardSquare() {
		isSet = false;
		color      = Color.white;
	}
	
	
	public GameBoardSquare(Color color) {
		this.color = color;
		isSet = true;
	}
	
	
	public boolean isSet() {
		return isSet;
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
	}
	
	
	public static Point boardToScreen(int row, int col, Vec2D originOffsetVector) {
		
		int screenX = originOffsetVector.x + col * TetrisGame.pieceSize;
		int screenY = originOffsetVector.y + row * TetrisGame.pieceSize;
		
		return new Point(screenX,screenY);
	}
	
	
	public void render(Graphics g, int row, int col, Vec2D originOffsetVector) {
		
		//Don't render the invisible rows
		if (row < TetrisGame.numInvisRows)
			return;
		
		if (isSet) {
			g.setColor(color);
			int size = TetrisGame.pieceSize;
			Point p  = boardToScreen(row, col, originOffsetVector);
			g.fillRect(p.x, p.y, size, size);
		}
	}
}
