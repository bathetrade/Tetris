package pieces;

import org.newdawn.slick.Color;

import point.Point;
import pieces.GameBoardSquare.MoveType;
import logic.GameBoard;
import logic.GameBoard.PieceType;

public class ActivePiece {
	
	//Maybe have an array of GameBoardSquare? Makes more sense...
	private Point[] piece;
	private GameBoard theBoard;
	private Color color;
	private PieceType type;
	public enum CollisionType {
		COL_NONE,
		COL_SIDE,
		COL_BOTTOM
	}
	public ActivePiece(GameBoard theBoard) {
		this.theBoard = theBoard;
		piece         = new Point[4];
		for (int i=0; i<4; ++i)
			piece[i] = new Point();
		this.color   = new Color(Color.white);
	}
	
	/**
	 * Sets the active Tetris piece (the one being controlled by user input).
	 * @param piece - A Point array of size 4. This array represents the top left corner of each
	 * GameBoardSquare that comprises the TetrisPiece. The points refer to the row/column pair of the
	 * particular square on the GameBoard.
	 */
	public boolean setPiece(Point[] piece, PieceType type) {
		
		switch (type) {
		case PIECE_L:
			this.color = Color.blue;
			break;
		case PIECE_J:
			this.color = Color.yellow;
			break;
		case PIECE_S:
			this.color = Color.green;
			break;
		case PIECE_Z:
			this.color = Color.red;
			break;
		case PIECE_T:
			this.color = Color.magenta;
			break;
		case PIECE_SQUARE:
			this.color = Color.orange;
			break;
		case PIECE_LINE:
			this.color = Color.pink;
			break;
		default:
			this.color = Color.white;
			break;
		}
		this.type = type;
		
		for (int i=0; i<4; ++i) {
			if (!theBoard.setSquare(piece[i].x, piece[i].y, this.color))
				return false;
		}
		
		for (int i=0; i<4; ++i)
			this.piece[i].setPoint(piece[i].x, piece[i].y);
		
		return true;
	}
	public void unsetPiece() {
		for (int i=0; i<4; ++i) {
			theBoard.unsetSquare(piece[i].x, piece[i].y);
			piece[i].setPoint(-1, -1);
		}
	}
	/**
	 * This functions check a square on the gameboard at the position (row, col) to see if it's a part
	 * of the active piece (the piece being controlled by input). This is to avoid triggering a collision
	 * that is the result of one square in the active piece colliding with another square in the active piece.
	 * @param row - the row to check.
	 * @param col - the column to check.
	 * @return Returns a boolean indicating whether the specified square is a part of the active piece.
	 */
	private boolean isPartOfPiece(int row, int col) {
		for (int i=0; i<4; ++i) {
			if (piece[i].x == row && piece[i].y == col)
				return true;
		}
		return false;
	}
	private void translate(int row, int col) {
		Point[] movedPiece = new Point[4];
		for (int i=0; i<4; ++i)
			movedPiece[i] = new Point(piece[i].x + row, piece[i].y + col);
		unsetPiece();
		setPiece(movedPiece, this.type);
	}
	/**
	 * Checks to see if the move is possible without a collision. It is necessary to call this function
	 * before calling move().
	 * @param type - An enum of type moveType. Possible values are MOVE_DOWN, MOVE_RIGHT, and MOVE_LEFT.
	 * @return Indicates whether the move is possible without a collision.
	 */
	private boolean checkMove(MoveType type) {
		Point moveVector = new Point();
		switch (type) {
		case MOVE_DOWN:
			moveVector.setPoint(1, 0);
			break;
		case MOVE_LEFT:
			moveVector.setPoint(0, -1);
			break;
		case MOVE_RIGHT:
			moveVector.setPoint(0, 1);
			break;
		}
		
		//Check if moved piece will cause a collision
		//Dammit...I hate quadratic algorithms
		int moved_row;
		int moved_col;
		for (int i=0; i<4; ++i) {
			
			moved_row = piece[i].x+moveVector.x;
			moved_col = piece[i].y+moveVector.y;
			boolean inBounds    = theBoard.inBounds(moved_row, moved_col); //Is moved piece in bounds?
			boolean isSet       = theBoard.isSet(moved_row, moved_col); //Is it already set?
			boolean partOfPiece = isPartOfPiece(moved_row, moved_col); //Ignore when a piece collides
																	   //with itself.
			
			if (!inBounds)
				return false;
			
			if (isSet && !partOfPiece)  //Check if piece collides with another piece; ignore collisions with self.
				return false;
			
		}
		return true;
	}
	/**
	 * Moves the active Tetris piece being controlled by input.
	 * @param type - An enum of type moveType. Possible values are MOVE_DOWN, MOVE_RIGHT, and MOVE_LEFT.
	 * @return Returns a value in the enum collisionType. Possible values are COL_SIDE,
	 * COL_BOTTOM, and COL_NONE.
	 */
	public CollisionType move(MoveType type) {
		switch (type) {
		
		case MOVE_DOWN:
			
			//Make sure we can move it down without hitting anything
			if (!checkMove(type))
				return CollisionType.COL_BOTTOM;
			
			translate(1,0);
			break;
			
			
		case MOVE_LEFT:										//MOVE_LEFT and MOVE_RIGHT
			if (!checkMove(type))
				return CollisionType.COL_SIDE;
			
			translate(0,-1);
			break;
			
			
		case MOVE_RIGHT:
			if (!checkMove(type))
				return CollisionType.COL_SIDE;
			
			translate(0,1);
		}
		
		return CollisionType.COL_NONE;
	}
	
	/* Things to do....
	 * 1) Improve rotation so pieces "bounce" off walls and objects when rotated.
	 * 2) Add images to Tetris pieces.
	 * 3) Allow pieces to be rotated above the top of the GameBoard. Also, add an invisible "top row" so
	 *    pieces can spawn above the screen.
	 * 4) Add an animation for row deletion. Also, add the row deletion.
	 * */
	
	//TODO  Do shit
	public void rotate() {
		
		//"Pivot" is the center square of the Tetris piece.
		//In order to rotate, we translate the piece's center (the pivot square) to the origin.
		Point pivot          = new Point(piece[0]);
		Point originVec      = new Point(-pivot.x, -pivot.y);
		Point[] rotatedPoint = new Point[4];
		for (int i=0; i<4; ++i)
			rotatedPoint[i] = new Point(piece[i]);
		
		//Rotate the piece
		for (int i=0; i<4; ++i) {
			rotatedPoint[i].x += originVec.x;       //Translate piece so it's centered on origin.
			rotatedPoint[i].y += originVec.y;
			int temp           = rotatedPoint[i].x; //Rotate the piece
			rotatedPoint[i].x  = rotatedPoint[i].y;
			rotatedPoint[i].y  = -temp;
			rotatedPoint[i].x += -originVec.x;	    //Translate piece back to original position.
			rotatedPoint[i].y += -originVec.y;
		}
		unsetPiece();
		setPiece(rotatedPoint, this.type);
	}
	
	public void dropPiece() {
		//Do stuff
	}
}
