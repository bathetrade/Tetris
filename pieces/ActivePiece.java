package pieces;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.newdawn.slick.Color;

import point.*;
import pieces.GameBoardSquare.MoveType;
import logic.GameBoard;
import logic.GameBoard.PieceType;
import logic.SubsquareCollision;
import logic.SubsquareCollisionList;

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
		COL_BOTTOM,
		COL_TOP
	}



	
	private boolean isLeftOfPiece(Point subsquare, Point[] piece) {
		for (int i = 0; i < 4; ++i) {
			if (!(subsquare.y < piece[i].y))
				return false;
		}
		return true;
	}
	
	
	
	private boolean isRightOfPiece(Point subsquare, Point[] piece) {
		for (int i = 0; i < 4; ++i) {
			if (!(subsquare.y > piece[i].y))
				return false;
		}
		return true;
	}
	
	
	
	
	private boolean isUnderPiece(Point subsquare, Point[] piece) {
		for (int i = 0; i < 4; ++i) {
			if (!(subsquare.x > piece[i].x))
				return false;
		}
		return true;
	}
	
	
	
	
	@SuppressWarnings("unused") private boolean isAbovePiece(Point subsquare, Point[] piece) {
		for (int i = 0; i < 4; ++i) {
			if (!(subsquare.x < piece[i].x))
				return false;
		}
		return true;
	}
	
	
	
	
	private Vec2D getOOBKickVector(SubsquareCollisionList subsquareOOBList) {
		
		try {
			if (!subsquareOOBList.hasSameCollisionType())
				throw new Exception("A piece cannot be out of bounds on more than one side.");
		}
		catch(Exception e) {
			e.printStackTrace();
			return new Vec2D(0,0);
		}
		
		
		Comparator<SubsquareCollision> outstandingSubsquareOrdering;
		
		Point outstandingSubsquare;
		Vec2D kickVector = new Vec2D(0,0);
		int kickAmount = 0;
		
		switch (subsquareOOBList.getCollisionType()) {
		
		case COL_LEFT:
			
			//Ascending sort-by-col comparator (use min() to find left-most OOB subsquare)
			outstandingSubsquareOrdering = new Comparator<SubsquareCollision>() {
				public int compare(SubsquareCollision s1, SubsquareCollision s2) {
					return s1.getSubsquare().y - s2.getSubsquare().y;
				}
			};
			outstandingSubsquare = Collections.min(subsquareOOBList.getList(), outstandingSubsquareOrdering).getSubsquare();
			kickAmount = -outstandingSubsquare.y;
			kickVector.set(0, kickAmount);
			return kickVector;
		
		case COL_RIGHT:
			
			//Ascending sort-by-col comparator (use max() to find right-most OOB subsquare)
			outstandingSubsquareOrdering = new Comparator<SubsquareCollision>() {
				public int compare(SubsquareCollision s1, SubsquareCollision s2) {
					return s1.getSubsquare().y - s2.getSubsquare().y;
				}
			};
			outstandingSubsquare = Collections.max(subsquareOOBList.getList(), outstandingSubsquareOrdering).getSubsquare();
			kickAmount = (theBoard.getCols() - 1) - outstandingSubsquare.y;
			kickVector.set(0, kickAmount);
			return kickVector;
			
		case COL_BOTTOM:
			
			//Ascending sort-by-row comparator (use max() to find bottom-most OOB subsquare)
			outstandingSubsquareOrdering = new Comparator<SubsquareCollision>() {
				public int compare(SubsquareCollision s1, SubsquareCollision s2) {
					return s1.getSubsquare().x - s2.getSubsquare().x;
				}
			};
			outstandingSubsquare = Collections.max(subsquareOOBList.getList(), outstandingSubsquareOrdering).getSubsquare();
			kickAmount = (theBoard.getRows() - 1) - outstandingSubsquare.x;
			kickVector.set(kickAmount, 0);
			return kickVector;
		
		default:    //Should never happen
			break;
		}
		
		return kickVector; //Should never happen
	}
	
	
	
	
	private Vec2D getCollisionKickVector(SubsquareCollisionList subsquareCollisionList) {
		Vec2D kickVector = new Vec2D(0,0);
		
		//Scan the collision list for number of "unique" subsquares in the 
		//direction of the collision. For example, if the subsquare collision list
		//consists of the subsquares {(1,5), (2,5), (2,4)}, which are ordered pairs of
		//rows and columns, respectively, and the collision happened on the left side
		//of the piece, then we take "unique" to mean collisions with a unique column
		//index. In this case, unique entries would be either {(1,5), (2,4)} or 
		//{(2,5), (2,4)}, and the number of unique entries would be 2. This is 
		//the magnitude of the kick amount. (*sigh*)
		List<SubsquareCollision> collisionList = subsquareCollisionList.getList();
		int listSize = collisionList.size();
		if (listSize == 0) {
			System.out.println("Error in getCollisionKickVector(): collision list is empty");
			return kickVector;
		}
		
		
		int previous = 0;
		int numUniqueSubsquares = 1; //List is nonempty; at least one unique entry.
		
		switch(subsquareCollisionList.getCollisionType()) {
		
		case COL_LEFT:
			previous = collisionList.get(0).getSubsquare().y;
			for (int i = 1; i < listSize; ++i) {
				if (collisionList.get(i).getSubsquare().y != previous)
					++numUniqueSubsquares;
			}
			kickVector.set(0, numUniqueSubsquares);
			return kickVector;
			
			
		case COL_RIGHT:
			previous = collisionList.get(0).getSubsquare().y;
			for (int i = 1; i < listSize; ++i) {
				if (collisionList.get(i).getSubsquare().y != previous)
					++numUniqueSubsquares;
			}
			kickVector.set(0, -numUniqueSubsquares);
			return kickVector;
			
			
		case COL_BOTTOM:
			previous = collisionList.get(0).getSubsquare().x;
			for (int i = 1; i < listSize; ++i) {
				if (collisionList.get(i).getSubsquare().x != previous)
					++numUniqueSubsquares;
			}
			kickVector.set(-numUniqueSubsquares, 0);
			return kickVector;
			
			
		case COL_TOP:
			previous = collisionList.get(0).getSubsquare().x;
			for (int i = 1; i < listSize; ++i) {
				if (collisionList.get(i).getSubsquare().x != previous)
					++numUniqueSubsquares;
			}
			kickVector.set(numUniqueSubsquares, 0);
			return kickVector;
			
		default:
			break;
			
			
		}
		return kickVector;
	}
	
	
	
	
	public ActivePiece(GameBoard theBoard) {
		this.theBoard = theBoard;
		piece         = new Point[4];
		for (int i=0; i<4; ++i)
			piece[i] = new Point();
		this.color   = new Color(Color.white);
	}
	
	
	
	
	public void dropPiece() {
		while (move(MoveType.MOVE_DOWN, 1));
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
		
		//This is the source of the bug that causes mayhem when pieces are rotated above the board.
		boolean pieceInBounds = true;
		for (int i=0; i<4; ++i) {
			if (!theBoard.setSquare(piece[i].x, piece[i].y, this.color))
				//return false;
				pieceInBounds = false;
		}
		
		for (int i=0; i<4; ++i)
			this.piece[i].set(piece[i].x, piece[i].y);
		
		return pieceInBounds;
	}
	
	
	
	
	public void clearPiece() {
		for (int i=0; i<4; ++i) {
			theBoard.clearSquare(piece[i].x, piece[i].y);
			piece[i].set(-1, -1);
		}
	}
	
	
	
	
	/**
	 * Moves the active Tetris piece being controlled by input, if possible.
	 * @param type - An enum of type MoveType. Possible values are MOVE_DOWN, MOVE_RIGHT, and MOVE_LEFT.
	 * @param numUnits - The number of units to move the piece.
	 * @return Returns true if the move was successful, and false otherwise.
	 */
	public boolean move(MoveType type, int numUnits) {
		
		Vec2D moveVector = new Vec2D();
		switch (type) {
		
		case MOVE_LEFT:
			moveVector.set(0, -numUnits);
			break;
			
		case MOVE_RIGHT:
			moveVector.set(0, numUnits);
			break;
			
		case MOVE_DOWN:
			moveVector.set(numUnits, 0);
			break;
			
		}
		
		//Copy the piece
		Point[] originalPiece = new Point[4];
		Point[] movedPiece = new Point[4];
		for (int i = 0; i < 4; ++i) {
			originalPiece[i] = new Point(this.piece[i]);
			movedPiece[i] = new Point(this.piece[i]);
		}
		
		//Clear the active piece from the board (to get it out of the way).
		clearPiece();
		
		//Try moving.
		for (int i = 0; i < 4; ++i)
			movedPiece[i].add(moveVector);
		
		//Check whether the moved piece is colliding with anything or out of bounds.
		//We don't care if the piece is above the board; only left, right, or below.
		//(Otherwise, a line sticking above the top of the board wouldn't move.)
		boolean inBounds = theBoard.isPieceInBoundsLeftRightBottom(movedPiece);
		boolean pieceCollision = theBoard.checkPieceCollision(movedPiece);
		boolean moveSuccessful = inBounds && !pieceCollision;
		
		if (moveSuccessful) {
			setPiece(movedPiece, this.type);
			return true;
		}
		
		else {
			setPiece(originalPiece, this.type);
			return false;
		}

	}
	


	/*General "wallkicking" algorithm:

	1) Copy the active piece (twice) so we can set it back later if the rotation fails.
	
	2) Clear the active piece to get it out of the way.
	
	3) Rotate the copy.
	
	4) Add all out-of-bounds collisions to a list. An out-of-bounds collision consists
		of the point at which the rotated copy is out of bounds, and a collision type, which
		signifies which side of the board it happened on.
		
	5) Add all piece collisions to a list. Again, a piece collision consists of a
		point where the collision happened, and a collision type. The collision type
		specifies which side of the ORIGINAL piece the collision happened on.
	
	6) Now, handle four cases.
		I) The piece rotated out of bounds and is NOT colliding with another piece.
			In this case, try moving the piece back in bounds. If there's no collision,
			then the rotation is successful, and we jump to 7.
		II) The piece is out of bounds and colliding with another piece. In most cases,
			this means the piece can't be rotated (imagine a piece wedged between another
			piece and a wall), but not always.		
		III) The rotated piece induces collisions on two sides of the ORIGINAL piece.
			In this case, the rotation isn't possible because we can't kick the piece 
			without causing another collision.
		IV) The rotated piece is colliding with at least another subsquare. In this case,
			we try to move the piece back out of the collision (i.e., "kick" it),
			and make sure that doesn't cause another collision or an out-of-bounds.
			
	7) If the rotation was successful, set the rotated piece on the board and return
		true.
	
	8) If the rotation was not successful, set the original piece back on the board
		and return false.
*/
	
	
	public boolean rotate(boolean leftRotate) {
		
		if (type == PieceType.PIECE_SQUARE)
			return true;
		 
		
		//1) Clear the active piece to get it out of the way, but save it for later.
		Point[] originalPiece = new Point[4];
		for (int i = 0; i < 4; ++i)
			originalPiece[i] = new Point(piece[i]);
		clearPiece();
		
		
		
		// Make a copy of the original piece.
		//"Pivot" is subsquare that the tetris piece rotates about. The pivot is always the first point.
		//In order to rotate, we translate the pivot's center to the origin.
		//The origin is at the top-left of the game board.
		
		Point pivot          = new Point(originalPiece[0]);
		Vec2D originVec      = new Vec2D(-pivot.x, -pivot.y);
		Point[] rotatedPiece = new Point[4];
		for (int i=0; i<4; ++i)
			rotatedPiece[i] = new Point(originalPiece[i]);
		
		
		//Rotate the piece.
		//Add out of bounds points and piece collision points to lists.
		SubsquareCollisionList subsquareCollisionList = new SubsquareCollisionList(); //Keep track of piece collisions
		SubsquareCollisionList subsquareOOBList = new SubsquareCollisionList(); //Keep track of out of bounds collisions
		
		boolean outOfBounds = false;
		boolean pieceCollision = false;
		boolean rotationSuccessful = true;
		
		//Fit everything into one loop to decrease loop iterations
		//and readability. =)
		for (int i=0; i<4; ++i) {
			
			//Translate to origin before rotating.
			rotatedPiece[i].add(originVec);
			
			if (leftRotate)
				rotatedPiece[i].set(-rotatedPiece[i].y, rotatedPiece[i].x); //Rotate left 90 degrees.
			
			else 
				rotatedPiece[i].set(rotatedPiece[i].y, -rotatedPiece[i].x); //Rotate right 90 degrees.
			
			//Translate piece back to its original position.
			rotatedPiece[i].subtract(originVec);
			
			
			
			
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
			if (theBoard.outOfBoundsLeft(rotatedPiece[i])) {
				System.out.println("Subsquare is to the left of the board");
				subsquareOOBList.add(new SubsquareCollision(rotatedPiece[i], CollisionType.COL_LEFT));
				
			}
			
			//If the subsquare is to the right of the board...
			else if (theBoard.outOfBoundsRight(rotatedPiece[i])) {
				System.out.println("Subsquare is to the right of the board");
				subsquareOOBList.add(new SubsquareCollision(rotatedPiece[i], CollisionType.COL_RIGHT));
			}
			
			//If the subsquare is under the board
			else if (theBoard.outOfBoundsBottom(rotatedPiece[i])) {
				System.out.println("Subsquare is under the board!");
				subsquareOOBList.add(new SubsquareCollision(rotatedPiece[i], CollisionType.COL_BOTTOM));
			}
			
			//If the subsquare is colliding with another subsquare...
			//determine on which side of the original piece the collision occurs
			else if (theBoard.isSet(rotatedPiece[i])) { //Redundant out of bounds check; cleaner code.
				
				if (isLeftOfPiece(rotatedPiece[i], originalPiece))
					subsquareCollisionList.add(new SubsquareCollision(rotatedPiece[i], CollisionType.COL_LEFT));
				
				else if (isRightOfPiece(rotatedPiece[i], originalPiece))
					subsquareCollisionList.add(new SubsquareCollision(rotatedPiece[i], CollisionType.COL_RIGHT));
				
				else if (isUnderPiece(rotatedPiece[i], originalPiece))
					subsquareCollisionList.add(new SubsquareCollision(rotatedPiece[i], CollisionType.COL_BOTTOM));
				
				else
					subsquareCollisionList.add(new SubsquareCollision(rotatedPiece[i], CollisionType.COL_TOP));
				
			}
		}
		
		
		outOfBounds = !subsquareOOBList.isEmpty();
		pieceCollision = !subsquareCollisionList.isEmpty();
		
		
		//If the piece is only out of bounds, try kicking it back.
		if (outOfBounds && !pieceCollision) {
			
			boolean kickable = false;
			Vec2D offset = getOOBKickVector(subsquareOOBList);
			for (int i = 0; i < 4; ++i)
				rotatedPiece[i].add(offset);
			kickable = !theBoard.checkPieceCollision(rotatedPiece);
			rotationSuccessful = kickable;
		}
		
		//This is a bug. A piece can be successfully kicked if it's out of
		//bounds and colliding with another piece, although this can only happen
		//with a line. Will fix later if less lazy.
		else if (outOfBounds && pieceCollision)
			rotationSuccessful = false;
		
		
		//If there is only a piece collision, then we check to see whether the collision is entirely to one
		//side of the original piece. If so, we try to kick it away from the collision, which succeeds if 
		//the kicked piece isn't out of bounds or in another piece.
		//If the collision isn't entirely to one side, then the rotation isn't possible.
		else if (!outOfBounds && pieceCollision) {
			
			//If the collision happened only on one side of the active piece
			if (!subsquareCollisionList.hasSameCollisionType())
				rotationSuccessful = false;
			
			else {
				boolean kickable = false;  //Worst case: piece doesn't rotate.
				Vec2D offset = getCollisionKickVector(subsquareCollisionList);
				for (int i = 0; i < 4; ++i)
					rotatedPiece[i].add(offset);
				
				kickable = !theBoard.checkPieceCollision(rotatedPiece) && theBoard.isPieceInBounds(rotatedPiece);
				rotationSuccessful = kickable;
			}
			
		}
		
		
		if (rotationSuccessful)
			setPiece(rotatedPiece, this.type);
		else
			setPiece(originalPiece, this.type);
		
		return rotationSuccessful;
	}
	
}
