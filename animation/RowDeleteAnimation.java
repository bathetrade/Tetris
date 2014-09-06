package animation;
import timer.Timer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.ArrayList;

import org.newdawn.slick.Graphics;

import pieces.GameBoardSquare;
import point.Point2f;
import point.Point;
import logic.GameBoard;

public class RowDeleteAnimation {
	private List<Chunk> chunkRenderList;
	private Deque<Chunk> chunkStack;
	private int currentCollisionY;
	private Timer animationTimer;
	private boolean isFirstFrame;
	private GameBoard theBoard;
	
	
	
	public RowDeleteAnimation(GameBoard theBoard) {
		chunkStack        = new ArrayDeque<Chunk>(5);
		chunkRenderList   = new ArrayList<Chunk>(5); //max number of possible chunks
		animationTimer    = new Timer();
		isFirstFrame      = true;
		currentCollisionY = GameBoardSquare.boardToScreen(theBoard.getRows(), 0).y; //Bottom of the board
		this.theBoard     = theBoard;
	}
	
	
	
	
	private List<Integer> getFullRowsAndClearThem() {
		List<Integer> deletedRows = new ArrayList<Integer>(4);
		int rows = theBoard.getRows();
		int cols = theBoard.getCols();
		boolean fullRow;
		
		for (int i = 0; i < rows; ++i) {
			fullRow = true;
			for (int j = 0; i < cols; ++j) {
				if (!theBoard.isSet(i,j)) {
					fullRow = false;
					break;
				}
			}
			if (fullRow) {
				deletedRows.add(i);
				theBoard.clearRow(i);
			}
		}
		return deletedRows;
	}
	
	

	//working on this method
	private void getChunksAndClearBoard() {
		List<Integer> deletedRows = getFullRowsAndClearThem();
		Chunk currentChunk;
		
		//Better way of doing the loop?
		//Check each "chunk interval": between the top of the board and the top-most deleted row,
		//between the deleted rows, and between the bottom-most deleted row and the bottom of the
		//board. Each of these intervals optionally contains a chunk.
		int upperBound = 0; //Initialize to the top of the board
		for (Integer lowerBound : deletedRows) {
			
			//Iterate over each subsquare on the interval and add the set subsquares to the
			//current chunk.
			currentChunk = Chunk.createChunkFromBounds(theBoard, lowerBound, upperBound);
			
			if (!currentChunk.isEmpty()) {
				chunkStack.add(currentChunk);
				chunkRenderList.add(currentChunk);
			}
			upperBound = lowerBound.intValue() + 1;
		} //end main loop
		
		//Handle last potential "chunk interval," namely, the chunk between the bottom-most deleted
		//row and the bottom of the board.
		if (upperBound < theBoard.getCols()) {
			currentChunk = Chunk.createChunkFromBounds(theBoard, upperBound, theBoard.getCols());
			
			//We don't add this chunk to the stack because it doesn't need to move.
			//However, we need to initialize the collision line to the top of this stationary 
			//bottom-most stack.
			if (!currentChunk.isEmpty()) {
				chunkRenderList.add(currentChunk);
				currentCollisionY = (int)currentChunk.getTopBoundScreenSpace();
			}
		}
		
	}
	
	
	
		
	public void setChunksOnBoard() {
		for (Chunk c : chunkRenderList) {
			c.setOnBoard(theBoard);
		}
	}
	
	
	
	
	public boolean play(Graphics graphics) {
		//If it's the first frame of animation, we want to get the algorithm set up.
		//We begin the animation timer, which is used to calculate the position of the chunks
		//based on the total elapsed time.
		//We get all the chunks and add them to the chunk list for rendering, and the chunk stack
		//for the meat of the algorithm.
		//We also clear all the chunks off the board. This way, we can reset them when the animation
		//is complete (by converting the screen space coordinates of the chunks' subsquares back to
		//logic space (AKA, board space, the matrix of booleans in (row,col) form).
		if (isFirstFrame) {
			animationTimer.start();
			
			getChunksAndClearBoard();
			
			isFirstFrame = false;
		}
		
		//If it's not the first frame of animation....
		//This is the part where we move the chunks down.
		//The basic gist is that we want to try moving the chunk on the top of the stack (i.e.,
		//the chunk on the bottom of the screen) down. If it goes below the collision line, then
		//we don't do the move, pop the chunk off the stack, and update the collision line (which is
		//now the top of the popped chunk).
		//****Note: We can optimize the "try moving chunk" part of the algorithm by choosing
		//			the last subsquare in the chunk, which is guaranteed to be in the bottom 
		//			row of the chunk. Thus, if this "test subsquare" goes below the collision line, then
		//			we don't want to move the chunk. In that case, we want to eliminate floating-point
		//			imprecision by rounding the subsquares' x and y values to the nearest integer.*****
		//The animation is done when the chunk stack is empty.
		else {
			animationTimer.tick();
			Chunk chunk = chunkStack.peekFirst();
			float testSquareY = chunk.getBottomBoundScreenSpace();
			
			//s(t) is approximately equal to the sum of all v(t)*dt, where dt is the time
			//between animation frames (basic calculus). This is convenient, since we don't need
			//to keep track of the initial heights of each and every subsquare, which would also
			//require us to recalculate height for every subsquare, each frame. Instead, we just
			//calculate the change in height each frame once, and apply it to all subsquares.
			double t           = Timer.nanoToSeconds(animationTimer.getElapsedTime());
			double dt          = Timer.nanoToSeconds(animationTimer.getDeltaTime());
			double velocity    = 285d * t;
			double deltaHeight = velocity * dt;
			
			//If the chunk goes under the collision line, stop the chunk, update the collision line,
			//and pop the chunk off the stack.
			testSquareY += deltaHeight;
			if (testSquareY > currentCollisionY) {
				chunk = chunkStack.removeFirst();
				chunk.align();
				currentCollisionY = (int)chunk.getTopBoundScreenSpace();
			}
			
			//Otherwise, keep letting the chunks fall...
			else {
				for (Chunk c : chunkStack)
					c.moveChunk((float)deltaHeight);
			}
			
			return !chunkStack.isEmpty();
		}
		
		
		
		return true;
	}
	
	
	
	
}
