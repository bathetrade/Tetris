package pieces;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import point.Point;
import point.Vec2D;
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
		isOccupied = false;
	}
	
	
	public Point boardToScreen(int row, int col, GameContainer container) {
		
		Point screenCenter = new Point(container.getWidth()/2, container.getHeight()/2);
		int hbw            = (TetrisGame.pieceSize*TetrisGame.blockWidth)/2;
		int hbh            = (TetrisGame.pieceSize*TetrisGame.blockHeight)/2;
		Point topLeftBoard = new Point(screenCenter.x - hbw, screenCenter.y - hbh);
		
		//Get the vector that translates the origin of the visible game area to the origin of the
		//	on-screen playing area.
		//For instance, if there are two invisible rows, and we want the game area to have a blockHeight of
		//	10 (a total of 12 rows), then the 0th row and the 1st row are invisible. The rows 2 through 11 are visible. So, we
		//	want to map (2,0) (2nd row, 0th column) to the on-screen playing area's origin (i.e., topLeftBoard).
		
		Vec2D originOffset = new Vec2D(topLeftBoard.x, topLeftBoard.y - TetrisGame.numInvisRows * TetrisGame.pieceSize);
		int screenX = originOffset.x + col * TetrisGame.pieceSize;
		int screenY = originOffset.y + row * TetrisGame.pieceSize;
		
		return new Point(screenX,screenY);
	}
	
	
	public void render(GameContainer container, Graphics g, int row, int col) {
		
		//Don't render the invisible rows
		if (row < TetrisGame.numInvisRows)
			return;
		
		if (isOccupied) {
			g.setColor(color);
			int size = TetrisGame.pieceSize;
			Point p  = boardToScreen(row, col, container);
			g.fillRect(p.x, p.y, size, size);
		}
	}
}
