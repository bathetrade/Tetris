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
	
	/* Things to do....
	 * 1) Improve rotation so pieces "bounce" off walls and objects when rotated.
	 * 2) Add textures to Tetris pieces.
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
		
		
		
		//2) Make a copy of the original piece.
		//"Pivot" is subsquare that the tetris piece rotates about. The pivot is always the first point.
		//In order to rotate, we translate the pivot's center to the origin.
		//The origin is at the top-left of the game board.
		
		Point pivot          = new Point(originalPiece[0]);
		Vec2D originVec      = new Vec2D(-pivot.x, -pivot.y);
		Point[] rotatedPoint = new Point[4];
		for (int i=0; i<4; ++i)
			rotatedPoint[i] = new Point(originalPiece[i]);
		
		
		//3) Rotate the piece (rotate each subsquare about the pivot)
		//See if the rotation causes a piece to go out of bounds and/or causes a collision with another piece.
		//CollisionType collisionType = CollisionType.COL_NONE;
		//Point mostOutstandingSubsquare = new Point(0,0); //Keep track of the subsquare that's the farthest out of bounds.
		SubsquareCollisionList subsquareCollisionList = new SubsquareCollisionList(); //Keep track of piece collisions
		SubsquareCollisionList subsquareOOBList = new SubsquareCollisionList(); //Keep track of out of bounds collisions
		
		boolean outOfBounds = false;
		boolean pieceCollision = false;
		boolean rotationSuccessful = true;
		
		//Fit everything into one loop to decrease loop iterations and 
		//decrease readability. =)
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
			if (theBoard.outOfBoundsLeft(rotatedPoint[i])) {//the y component corresponds to the column (confusing!)
				System.out.println("Subsquare is to the left of the board");
				//collisionType = CollisionType.COL_LEFT;
				subsquareOOBList.add(new SubsquareCollision(rotatedPoint[i], CollisionType.COL_LEFT));
				//if (rotatedPoint[i].y  < mostOutstandingSubsquare.y)
					//mostOutstandingSubsquare.set(rotatedPoint[i]);
			}
			
			//If the subsquare is to the right of the board...
			else if (theBoard.outOfBoundsRight(rotatedPoint[i])) {
				System.out.println("Subsquare is to the right of the board");
				//collisionType = CollisionType.COL_RIGHT;
				subsquareOOBList.add(new SubsquareCollision(rotatedPoint[i], CollisionType.COL_RIGHT));
				//if (rotatedPoint[i].y > mostOutstandingSubsquare.y)
					//mostOutstandingSubsquare.set(rotatedPoint[i]);
			}
			
			//If the subsquare is under the board
			else if (theBoard.outOfBoundsBottom(rotatedPoint[i])) {
				System.out.println("Subsquare is under the board!");
				subsquareOOBList.add(new SubsquareCollision(rotatedPoint[i], CollisionType.COL_BOTTOM));
				//collisionType = CollisionType.COL_BOTTOM;
				//if (rotatedPoint[i].x > mostOutstandingSubsquare.x)
					//mostOutstandingSubsquare.set(rotatedPoint[i]);
			}
			
			//If the subsquare is colliding with another subsquare...
			//determine on which side of the original piece the collision occurs
			else if (theBoard.isSet(rotatedPoint[i])) { //Redundant out of bounds check; cleaner code.
				
				if (isLeftOfPiece(rotatedPoint[i], originalPiece))
					subsquareCollisionList.add(new SubsquareCollision(rotatedPoint[i], CollisionType.COL_LEFT));
				
				else if (isRightOfPiece(rotatedPoint[i], originalPiece))
					subsquareCollisionList.add(new SubsquareCollision(rotatedPoint[i], CollisionType.COL_RIGHT));
				
				else if (isUnderPiece(rotatedPoint[i], originalPiece))
					subsquareCollisionList.add(new SubsquareCollision(rotatedPoint[i], CollisionType.COL_BOTTOM));
				
				else
					subsquareCollisionList.add(new SubsquareCollision(rotatedPoint[i], CollisionType.COL_TOP));
				
			}
		}
		
		
		outOfBounds = !subsquareOOBList.isEmpty();
		pieceCollision = !subsquareCollisionList.isEmpty();
		
		
		
		//If the piece is only out of bounds, try kicking it back.
		if (outOfBounds && !pieceCollision) {
			
			
			//int kickAmount = 0;
			boolean kickable = false;
			Vec2D offset = getOOBKickVector(subsquareOOBList);
			for (int i = 0; i < 4; ++i)
				rotatedPoint[i].add(offset);
			kickable = !theBoard.checkPieceCollision(rotatedPoint);
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
			if (!subsquareCollisionList.hasSameCollisionType()) {
				rotationSuccessful = false;
			}
			
			else {
				boolean kickable = false;  //Worst case: piece doesn't rotate.
				Vec2D offset = new Vec2D(getCollisionKickVector(subsquareCollisionList));
				for (int i = 0; i < 4; ++i)
					rotatedPoint[i].add(offset);
				kickable = !theBoard.checkPieceCollision(rotatedPoint) && theBoard.isPieceInBounds(rotatedPoint);
				
				rotationSuccessful = kickable;
			}
			
		}
		
		
		if (rotationSuccessful)
			setPiece(rotatedPoint, this.type);
		else
			setPiece(originalPiece, this.type);
		
		return rotationSuccessful;
	}
	
	
	
	
}
