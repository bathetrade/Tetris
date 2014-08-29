package pieces;

import org.newdawn.slick.Color;

import point.*;
import pieces.GameBoardSquare.MoveType;
import logic.GameBoard;
import logic.GameBoard.PieceType;
import java.util.Collection;
import java.util.ArrayList;

public class ActivePiece {
	
	//Maybe have an array of GameBoardSquare? Makes more sense...
	//Or, at least used collections instead of naked arrays.
	private Point[] piece;
	private GameBoard theBoard;
	private Color color;
	private PieceType type;
	public enum CollisionType {
		COL_NONE,
		COL_LEFT,
		COL_RIGHT,
		COL_SIDE,  //when we don't care which side was collided with
		COL_BOTTOM
	}
	/**
	 * Checks to see if the move is possible without a collision. It is necessary to call this function
	 * before calling move().
	 * @param type - An enum of type moveType. Possible values are MOVE_DOWN, MOVE_RIGHT, and MOVE_LEFT.
	 * @param numUnits - The number of units to move the piece.
	 * @return Indicates whether the move is possible without a collision.
	 */
	private boolean checkMove(MoveType type, int numUnits) {
		Vec2D moveVector = new Vec2D();
		switch (type) {
		case MOVE_DOWN:
			moveVector.set(numUnits, 0);
			break;
		case MOVE_LEFT:
			moveVector.set(0, -numUnits);
			break;
		case MOVE_RIGHT:
			moveVector.set(0, numUnits);
			break;
		}
		
		//Check if moved piece will cause a collision
		//This is potentially inefficient for two reasons. First, isSet() and
		//isPartOfPiece() are called even if the subsquare is out of bounds.
		//Second, this algorithm is quadratic, whereas it would be linear to
		//unset the piece, move it, check if it collides, and if so, move it back
		//(and set the piece back).
		int moved_row = 0;
		int moved_col = 0;
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
		clearPiece();
		setPiece(movedPiece, this.type);
	}




	public ActivePiece(GameBoard theBoard) {
		this.theBoard = theBoard;
		piece         = new Point[4];
		for (int i=0; i<4; ++i)
			piece[i] = new Point();
		this.color   = new Color(Color.white);
	}
	
	
	
	
	public void dropPiece() {
		while (move(MoveType.MOVE_DOWN, 1) != CollisionType.COL_BOTTOM);
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
			this.piece[i].set(piece[i].x, piece[i].y);
		
		return true;
	}
	
	
	
	
	public void clearPiece() {
		for (int i=0; i<4; ++i) {
			theBoard.unsetSquare(piece[i].x, piece[i].y);
			piece[i].set(-1, -1);
		}
	}
	
	
	
	
	/**
	 * Moves the active Tetris piece being controlled by input.
	 * @param type - An enum of type moveType. Possible values are MOVE_DOWN, MOVE_RIGHT, and MOVE_LEFT.
	 * @param numUnits - The number of units to move the piece.
	 * @return Returns a value in the enum collisionType. Possible values are COL_LEFT, COL_RIGHT, COL_SIDE,
	 * COL_BOTTOM, and COL_NONE.
	 */
	public CollisionType move(MoveType type, int numUnits) {
		
		numUnits = Math.abs(numUnits);
		
		switch (type) {
		
		case MOVE_DOWN:
			
			//Make sure we can move it down without hitting anything
			if (!checkMove(type, numUnits))
				return CollisionType.COL_BOTTOM;
			
			translate(numUnits,0);
			break;
			
			
		case MOVE_LEFT:										//MOVE_LEFT and MOVE_RIGHT
			if (!checkMove(type, numUnits))
				return CollisionType.COL_SIDE;
			
			translate(0,-numUnits);
			break;
			
			
		case MOVE_RIGHT:
			if (!checkMove(type, numUnits))
				return CollisionType.COL_SIDE;
			
			translate(0,numUnits);
		}
		
		return CollisionType.COL_NONE;
	}
	
	/* Things to do....
	 * 1) Improve rotation so pieces "bounce" off walls and objects when rotated.
	 * 2) Add images to Tetris pieces.
	 * 3) Add an animation for row deletion. Also, add the row deletion.
	 **/
	
	
	//Todo: clean up this code. The method is too unwieldy.
	//I tried using the existing move methods, but there are too many subtleties with the rotation algorithm, so I just decided
	//to rewrite all of the code in here with the intention of cleaning it up later. Hopefully I can think of a more elegant
	//way to structure the move system. 
	
	
	/*General "wallkicking" algorithm:

	1) Clear the active piece from the board so it doesn't get in the way (but save a copy so we 
		can reset it if the rotation fails).

	2) Create a copy of the active piece to play around with.

	3) Rotate the copy.

	4) a) If the rotated piece is out of bounds, try moving it back in bounds. If there's no collision,
	  then we're done. Jump to 7).
   	   b) If moving it back in bounds causes another collision, then the rotation isn't 
   		  possible. Jump to 6).

	5) Get a list of all the squares that the rotated piece is colliding with. 
		a) If the list is empty, then the rotation is possible and we're done. Jump to 7).
		b) Otherwise, if all the colliding squares are to one side of the original piece,
			then we find the most outstanding of these squares and try moving the rotated piece
			out of the other piece(s) by that amount (e.g., if all the colliding squares are to the
			left of the original piece, and the "farthest" colliding square is 2 units away after
			rotation, then we try moving the rotated piece 2 units to the right).
			i)  If we can do this, then the rotation is possible and we're done. Jump to 7).
			ii) If we can't, namely, the rotated piece ends up in another piece or out of bounds, then
				the rotation isn't possible. Jump to 6).
		c) Otherwise, the rotated piece is colliding on both sides and so the rotation isn't possible. Jump to 6).
  
	6) Set the original piece again. Return false.

	7) Set the rotated piece. Return true.
*/
	
	
	public boolean rotate(boolean leftRotate) {
		
		if (type == PieceType.PIECE_SQUARE)
			return true;
		 
		
		//1) Clear the active piece to get it out of the way, but save it for later.
		Point[] originalPiece = new Point[4];
		for (int i = 0; i < 4; ++i)
			originalPiece[i] = new Point(piece[i]);
		clearPiece();
		
		
		
		//2,3) Make a copy of the original piece and rotate it.
		//"Pivot" is subsquare that the tetris piece rotates about. The pivot is always the first point.
		//In order to rotate, we translate the pivot's center to the origin.
		//The origin is at the top-left of the game board.
		
		Point pivot          = new Point(originalPiece[0]);
		Vec2D originVec      = new Vec2D(-pivot.x, -pivot.y);
		Point[] rotatedPoint = new Point[4];
		for (int i=0; i<4; ++i)
			rotatedPoint[i] = new Point(originalPiece[i]);
		
		
		//Rotate the piece (rotate each subsquare about the pivot)
		//See if the rotation causes a piece to go out of bounds.
		CollisionType collisionType = CollisionType.COL_NONE;
		Point mostOutstandingSubsquare = new Point(); //Keep track of the subsquare that's the farthest out of bounds.
		Collection<Point> subsquareCollisionList = new ArrayList<Point>(4); //Keep track of piece collisions
		boolean outOfBounds = false;
		boolean pieceCollision = false;
		boolean rotationSuccessful = false;
		for (int i=0; i<4; ++i) {
			
			//Translate to origin before rotating.
			rotatedPoint[i].add(originVec);
			
			if (leftRotate)
				rotatedPoint[i].set(-rotatedPoint[i].y, rotatedPoint[i].x); //Rotate left 90 degrees.
			
			else 
				rotatedPoint[i].set(rotatedPoint[i].y, -rotatedPoint[i].x); //Rotate right 90 degrees.
			
			//Translate piece back to its original position.
			rotatedPoint[i].subtract(originVec);
			
			
			
			
			//Check if the rotated subsquare is out of bounds so we can "kick" it back in later.
			//We have to use our own custom out of bounds code, because we need to know which side the
			//piece is out of bounds on.
			//
			//The whole piece can't be out of bounds on two sides at once.
			//
			//We keep track of the "most outstanding subsquare," because, for instance, if the subsquare
			//is 2 units out of bounds on the right, then we want to "kick" the whole shape back to the 
			//left by 2 units.
			
			
			//If the subsquare is to the left of the board....
			if (rotatedPoint[i].y < 0) {//the y component corresponds to the column (confusing!)
				outOfBounds = true;
				System.out.println("Subsquare is to the left of the board");
				collisionType = CollisionType.COL_LEFT;
				if (rotatedPoint[i].y  < mostOutstandingSubsquare.y)
					mostOutstandingSubsquare.set(rotatedPoint[i]);
			}
			
			//If the subsquare is to the right of the board...
			else if (rotatedPoint[i].y >= theBoard.getCols()) {
				outOfBounds = true;
				System.out.println("Subsquare is to the right of the board");
				collisionType = CollisionType.COL_RIGHT;
				if (rotatedPoint[i].y > mostOutstandingSubsquare.y)
					mostOutstandingSubsquare.set(rotatedPoint[i]);
			}
			
			//If the subsquare is under the board
			else if (rotatedPoint[i].x >= theBoard.getRows()) {
				outOfBounds = true;
				System.out.println("Subsquare is under the board!");
				collisionType = CollisionType.COL_BOTTOM;
				if (rotatedPoint[i].x > mostOutstandingSubsquare.x)
					mostOutstandingSubsquare.set(rotatedPoint[i]);
			}
			else if (theBoard.isSet(rotatedPoint[i].x, rotatedPoint[i].y))
				subsquareCollisionList.add(rotatedPoint[i]);
		}
		
		//4) If the piece was ONLY rotated out of bounds, try to kick it back.
		//If the piece is out of bounds AND colliding with a piece, then the 
		//rotation isn't possible.
		pieceCollision = !subsquareCollisionList.isEmpty();
		int numCollisions = subsquareCollisionList.size();
		System.out.println("Number of piece collisions: " + numCollisions);
		
		
		if (outOfBounds && !pieceCollision) {
			
			int kickAmount = 0;
			boolean kickable = false;
			Vec2D offset = new Vec2D();
			
			//Note: the use of CollisionType does not imply a collision with another piece. It is being
			//		used here to signify which side the piece is out of bounds on.
			switch(collisionType) {
			
			case COL_LEFT:
				kickAmount = -mostOutstandingSubsquare.y;
				offset.set(0, kickAmount);
				for (int i = 0; i < 4; ++i)
					rotatedPoint[i].add(offset);
				kickable = !theBoard.checkCollision(rotatedPoint);
				break;
			
			case COL_RIGHT:
				kickAmount = (theBoard.getCols() - 1) - mostOutstandingSubsquare.y;
				offset.set(0, kickAmount);
				for (int i = 0; i < 4; ++i)
					rotatedPoint[i].add(offset);
				kickable = !theBoard.checkCollision(rotatedPoint);
				break;
				
			case COL_BOTTOM:
				kickAmount = (theBoard.getRows() -1) - mostOutstandingSubsquare.x;
				offset.set(kickAmount, 0);
				for (int i = 0; i < 4; ++i)
					rotatedPoint[i].add(offset);
				kickable = !theBoard.checkCollision(rotatedPoint);
				break;
				
			default:
				break;
			}
			rotationSuccessful = kickable;
		}
		else
			rotationSuccessful = true;
		
		
		System.out.println("Rotation success: " + rotationSuccessful);
		if (rotationSuccessful)
			setPiece(rotatedPoint, this.type);
		else
			setPiece(originalPiece, this.type);
		
		return true;
	}
	
	
	
	
}
