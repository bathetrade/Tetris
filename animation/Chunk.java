package animation;

import java.util.Deque;
import java.util.LinkedList;

import org.newdawn.slick.Graphics;

import point.Point2f;
import logic.GameBoard;
import pieces.GameBoardSquare;
import tetrisgame.TetrisGame;

public class Chunk {
	private Deque<ScreenSpaceSubsquare> subsquareList;
	private float topChunkScreenSpaceY;    //The y value, in screen space, of the top of the chunk
	private float bottomChunkScreenSpaceY; 
	private GameBoard theBoard;
	
	public Chunk(GameBoard theBoard) {
		subsquareList = new LinkedList<ScreenSpaceSubsquare>();
		this.theBoard = theBoard;
		topChunkScreenSpaceY = -1;
		bottomChunkScreenSpaceY = -1;
	}
	
	
	
	
	public void addSubsquare(ScreenSpaceSubsquare s) {
		subsquareList.addFirst(s);
	}
	
	
	
	
	public float getTopChunkScreenSpace() {
		return topChunkScreenSpaceY;
	}
	
	public float getBottomChunkScreenSpace() {
		return bottomChunkScreenSpaceY;
	}
	
	
	
	/**
	 * Picks a subsquare from the bottom-most row of the chunk. This is used for determining when
	 * to stop a falling a chunk (i.e., if the subsquare is below the collision line).
	 * @return Returns the coordinates of the subsquare. Returns (-1,-1) if the chunk is empty.
	 */
	public Point2f getBottomRowSubsquareRepresentative() {
		int lastIndex = subsquareList.size() - 1;
		if (lastIndex < 0)
			return new Point2f(-1,-1);
		return subsquareList.peekFirst().getPoint();
	}
	
	
	public Deque<ScreenSpaceSubsquare> getSubsquareList() {
		return subsquareList;
	}
	
	
	
	public void setTopChunkIndex(int row) {
		topChunkScreenSpaceY = GameBoardSquare.boardToScreen(row, 0).y;
	}
	
	
	
	public void setBottomChunkIndex(int row) {
		bottomChunkScreenSpaceY = GameBoardSquare.boardToScreen(row, 0).y;
	}
	
	
	
	
	/**
	 * Creates a chunk on the interval [topRowIndex, bottomRowIndex] by getting all the subsquares
	 * that are set in the interval and adding them to a list. Stores the top of the chunk and the
	 * bottom of the chunk in screen space.
	 * @param topRowIndex - The integer in the interval [0, GameBoard.rows) signifying the top 
	 * row of the chunk. This is used to store the height of the top of the chunk in screen space,
	 * which is used to tell the falling chunk above the current chunk when to stop.
	 * @param bottomRowIndex - The integer in the interval [topRowIndex, GameBoard.rows) signifying the bottom
	 * row of the chunk. This is used to store the height of the bottom of the chunk in screen space,
	 * which is used to tell the current falling chunk when to stop.
	 */
	public void createChunk(int topRowIndex, int bottomRowIndex) {
		if (!subsquareList.isEmpty())
			subsquareList.clear();
		
		topChunkScreenSpaceY = GameBoardSquare.boardToScreen(topRowIndex, 0).y;
		bottomChunkScreenSpaceY = GameBoardSquare.boardToScreen(bottomRowIndex, 0).y;
		
		int cols = theBoard.getCols();
		for (int i = topRowIndex; i <= bottomRowIndex; ++i) {
			for (int j = 0; j < cols; ++j) {
				if (theBoard.isSet(i,j))
					subsquareList.addFirst(new ScreenSpaceSubsquare(new Point2f(i,j), theBoard.getSquare(i,j).getColor()));
			}
		}
	}
	
	public void moveChunk(float amount) {
		for (ScreenSpaceSubsquare s : subsquareList) {
			s.getPoint().add(0, amount);
			topChunkScreenSpaceY += amount;
			bottomChunkScreenSpaceY += amount;
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
	public boolean align() {
		
		//Really simplistic rounding. Might cause a problem if the computer stutters enough to
		//allow the timer to update the chunk beyond 1 integer below the collision line.
		for (ScreenSpaceSubsquare s : subsquareList) {
			Point2f p = s.getPoint();
			p.set((float)Math.round(p.x), (float)Math.round(p.y));
		}
		
		topChunkScreenSpaceY = (float)Math.round(topChunkScreenSpaceY);
		bottomChunkScreenSpaceY = (float)Math.round(bottomChunkScreenSpaceY);
		
		return true;
	}
	
	
	
	
	public boolean isEmpty() {
		return subsquareList.isEmpty();
	}
	
	
	
	
	public void render(Graphics graphics) {
		for (ScreenSpaceSubsquare s : subsquareList) {
			graphics.setColor(s.getColor());
			graphics.fillRect(s.getPoint().x, s.getPoint().y, TetrisGame.pieceSize, TetrisGame.pieceSize);
		}
	}
}
