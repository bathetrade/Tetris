package animation;

import java.util.Deque;
import java.util.LinkedList;

import org.newdawn.slick.Graphics;

import point.Point;
import point.Point2f;
import logic.GameBoard;
import pieces.GameBoardSquare;
import tetrisgame.TetrisGame;

public class Chunk {
	private Deque<ScreenSpaceSubsquare> subsquareList;
	
	public Chunk() {
		subsquareList = new LinkedList<ScreenSpaceSubsquare>();
	}
	
	
	/**
	 * Checks whether a chunk exists on the interval [upperBoundRowIndex, lowerBoundRowIndex).
	 * @param upperBoundRowIndex - The index of the row containing the top of the chunk in (row,col)
	 * space (i.e., logic space).
	 * @param lowerBoundRowIndex - The index of the row of the bottom chunk + 1 in logic space.
	 * @return Returns a boolean indicating whether the chunk exists.
	 */
	public static boolean isEmptyChunk(int lowerBoundRowIndex, int upperBoundRowIndex) {
		return !(lowerBoundRowIndex > upperBoundRowIndex);
	}
	
	
	
	
	public void addSubsquare(ScreenSpaceSubsquare s) {
		subsquareList.addFirst(s);
	}
	
	
	
	/**
	 * Calculates the line (i.e., the y value) of the top of the chunk in screen space. Runs in O(1).
	 * @return Returns -1.0 if the chunk is empty, otherwise, returns the y value of the top of the
	 * chunk in screen space.
	 */
	public float getTopBoundScreenSpace() {
		if (subsquareList.isEmpty())
			return -1;
		
		return subsquareList.peekLast().getPoint().y;
	}
	
	/**
	 * Calculates the line (i.e., the y value) of the bottom of the chunk in screen space. Runs in 
	 * O(1).
	 * @return Returns -1.0 if the chunk is empty, otherwise, returns the y value of the bottom of
	 * the chunk in screen space.
	 */
	public float getBottomBoundScreenSpace() {
		if (subsquareList.isEmpty())
			return -1;
		
		return subsquareList.peekFirst().getPoint().y + TetrisGame.pieceSize;
	}
	
	
	
	
	public Deque<ScreenSpaceSubsquare> getSubsquareList() {
		return subsquareList;
	}
	
	
	
	/**
	 * Sets the chunk on the game board.
	 * @param theBoard - the board.
	 */
	public void setOnBoard(GameBoard theBoard) {
		for (ScreenSpaceSubsquare s : subsquareList) {
			Point2f screenSpace = s.getPoint();
			Point logicSpace = GameBoardSquare.screenToBoard(screenSpace.x, screenSpace.y);
			theBoard.setSquare(logicSpace.x, logicSpace.y, s.getColor());
		}
	}
	
	
	
	
//	/**
//	 * Creates a chunk on the interval [upperBound, lowerBound). This chunk is guaranteed
//	 * to be sorted, such that the first subsquare is on the highest row of the chunk, and the last 
//	 * subsquare is on the lowest row. This method also clears the chunk from the board.
//	 * @param theBoard - The GameBoard containing the chunks.
//	 * @param lowerBound - The bottom-most row index plus 1 of the chunk in logic space.
//	 * @param upperBound - The top-most row index of the chunk in logic space.
//	 * @param clearChunk - We can optionally clear this chunk from the game board while we're
//	 * acquiring it.
//	 * @return If the chunk is empty, returns an empty chunk. Returns a non-empty chunk otherwise.
//	 */
//	public static Chunk createChunkFromBounds(GameBoard theBoard, int lowerBound, int upperBound, boolean clearChunk) {
//		if (isEmptyChunk(lowerBound, upperBound))
//			return new Chunk();
//		
//		Chunk chunk = new Chunk();
//		int numCols = theBoard.getCols();
//		
//		//Two versions of the loop for maximum efficiency. This is better than checking the clearChunk
//		//value for each (i,j) index.
//		if (clearChunk) {
//			for (int i = upperBound; i < lowerBound; ++i) {
//				for (int j = 0; j < numCols; ++j) {
//					if (theBoard.isSet(i,j)) {
//						Point p = GameBoardSquare.boardToScreen(i, j);
//						chunk.addSubsquare(new ScreenSpaceSubsquare(new Point2f(p), theBoard.getSquare(i,j).getColor()));
//						theBoard.clearSquare(i,j);
//					}
//				}
//			}
//		}
//		
//		else {
//			for (int i = upperBound; i < lowerBound; ++i) {
//				for (int j = 0; j < numCols; ++j) {
//					if (theBoard.isSet(i,j)) {
//						Point p = GameBoardSquare.boardToScreen(i, j);
//						chunk.addSubsquare(new ScreenSpaceSubsquare(new Point2f(p), theBoard.getSquare(i,j).getColor()));
//					}
//				}
//			}
//		}
//		
//		return chunk;
//	}
	
	
	
	
	public void createChunkFromBounds(GameBoard theBoard, int lowerBound, int upperBound, boolean clearChunk) {
		if (isEmptyChunk(lowerBound, upperBound))
			return;
		
		int numCols = theBoard.getCols();
		
		//Two versions of the loop for maximum efficiency. This is better than checking the clearChunk
		//value for each (i,j) index.
		if (clearChunk) {
			for (int i = upperBound; i < lowerBound; ++i) {
				for (int j = 0; j < numCols; ++j) {
					if (theBoard.isSet(i,j)) {
						Point p = GameBoardSquare.boardToScreen(i, j);
						addSubsquare(new ScreenSpaceSubsquare(new Point2f(p), theBoard.getSquare(i,j).getColor()));
						theBoard.clearSquare(i,j);
					}
				}
			}
		}
		
		else {
			for (int i = upperBound; i < lowerBound; ++i) {
				for (int j = 0; j < numCols; ++j) {
					if (theBoard.isSet(i,j)) {
						Point p = GameBoardSquare.boardToScreen(i, j);
						addSubsquare(new ScreenSpaceSubsquare(new Point2f(p), theBoard.getSquare(i,j).getColor()));
					}
				}
			}
		}
				
	}
	
	
	
	
	public void moveChunk(float amount) {
		for (ScreenSpaceSubsquare s : subsquareList) {
			s.getPoint().add(0, amount);
		}
	}
	
	
	
	/**
	 * Note: This method is potentially buggy in its current, simplistic state.
	 * This method removes floating point imprecisions after a chunk is done falling, using the current
	 * collision line in screen space. The bottom-most row of the chunk will be aligned with 
	 * collisionLine + pieceSize, the one above that will be aligned with collisionLine + 2*pieceSize,
	 * and so on. This method guarantees that all the chunks will convert back to logic space 
	 * without problems, and that any hiccups with the timer will be smoothed out.
	 * 
	 * @return Returns true if the method successfully aligns the chunks, and false otherwise (it
	 * should never return false; this is just here for debugging).
	 */
	//Update: not using this method. Instead, if updating the chunk would put it below the collision line, then
	//we simply update by the exact amount needed to align it with the line. This is cleaner.
	
	public boolean align() {
		
		//Really simplistic rounding. Might cause a problem if the computer stutters enough to
		//allow the timer to update the chunk beyond 1 integer below the collision line.
		for (ScreenSpaceSubsquare s : subsquareList) {
			Point2f p = s.getPoint();
			p.set((float)Math.round(p.x), (float)Math.round(p.y));
		}
		
		//topChunkScreenSpaceY = (float)Math.round(topChunkScreenSpaceY);
		//bottomChunkScreenSpaceY = (float)Math.round(bottomChunkScreenSpaceY);
		
		return true;
	}
	
	
	
	
	public boolean isEmpty() {
		return subsquareList.isEmpty();
	}
	
	
	
	
	public void render(Graphics graphics) {
		for (ScreenSpaceSubsquare s : subsquareList) {
			//graphics.setColor(s.getColor());
			//graphics.fillRect(s.getPoint().x, s.getPoint().y, TetrisGame.pieceSize, TetrisGame.pieceSize);
			Point2f p = s.getPoint();
			graphics.drawImage(s.getColor(), p.x, p.y);
		}
	}
}
