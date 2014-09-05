package animation;
import timer.Timer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.ArrayList;

import org.newdawn.slick.Graphics;

import pieces.GameBoardSquare;
import point.Point2f;
import logic.GameBoard;

public class RowDeleteAnimation {
	private List<Chunk> chunkList;
	private Deque<Chunk> chunkStack;
	private int currentCollisionY;
	private Timer animationTimer;
	private boolean isFirstFrame;
	private GameBoard theBoard;
	
	
	
	public RowDeleteAnimation(GameBoard theBoard) {
		chunkStack        = new ArrayDeque<Chunk>(5);
		chunkList         = new ArrayList<Chunk>(5); //max number of possible chunks
		animationTimer    = new Timer();
		isFirstFrame      = true;
		currentCollisionY = GameBoardSquare.boardToScreen(theBoard.getRows(), 0).y; //Bottom of the board
		this.theBoard     = theBoard;
	}
	
	
	
	
	private List<Integer> getDeletedRows() {
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
		int rows = theBoard.getRows();
		int cols = theBoard.getCols();
		int topChunkIndex    = -1;
		int bottomChunkIndex = -1;
		List<Integer> deletedRows = getDeletedRows();
		Chunk currentChunk = new Chunk(theBoard);
		
		for (int i = 0; i < rows; ++i) {
			if (deletedRows.contains(i))
				continue;
			
			for (int j = 0; j < cols; ++j) {
				if (theBoard.isSet(i,j)) {
					//do stuff
				}
			}
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
			
			//Clear the board; log deleted row indices.
			//Also, snatch the row index of the top of the top-most chunk.
			List<Integer> deletedRows = getDeletedRows();
			
			
			//Get all the chunks
			int topHighestChunk = 0; //placeholder to make red X go away
			int lastRowIndex = topHighestChunk;
			for (Integer rowIndex : deletedRows) {
				int numRows = rowIndex.intValue() - lastRowIndex;
				Chunk chunk = new Chunk(theBoard, numRows * theBoard.getCols());
				chunk.createChunk(lastRowIndex, rowIndex.intValue()-1);
				chunkStack.addFirst(chunk);
				chunkList.add(chunk);
				lastRowIndex = rowIndex.intValue() + 1;
			}
			
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
			Point2f bottomChunkRowRep = chunk.getBottomRowSubsquareRepresentative();
			Point2f copy = new Point2f(bottomChunkRowRep);
			
			//s(t) is approximately equal to the sum of all v(t)*dt, where dt is the time
			//between animation frames (basic calculus). This is convenient, since we don't need
			//to keep track of the initial heights of each and every subsquare, which would also
			//require us to recalculate height for every subsquare, each frame. Instead, we just
			//calculate the change in height each frame once, and apply it to all subsquares.
			double t           = Timer.nanoToSeconds(animationTimer.getElapsedTime());
			double dt          = Timer.nanoToSeconds(animationTimer.getDeltaTime());
			double velocity    = 285d * t;
			double deltaHeight = velocity * dt;
			
			//If the chunk goes under the collision line...
			copy.add(0, (float)deltaHeight);
			if (copy.y > currentCollisionY) {
				chunk = chunkStack.removeFirst();
				chunk.align();
				currentCollisionY = (int)chunk.getTopChunkScreenSpace();
			}
			
			else {
				for (Chunk c : chunkStack)
					c.moveChunk((float)deltaHeight);
			}
			
			return !chunkStack.isEmpty();
		}
		
		
		
		return true;
	}
	
	
	
	
}
