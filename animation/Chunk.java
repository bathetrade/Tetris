package animation;

import java.util.List;
import java.util.ArrayList;

import org.newdawn.slick.Graphics;

import point.Point2f;
import logic.GameBoard;
import pieces.GameBoardSquare;
import tetrisgame.TetrisGame;

public class Chunk {
	private List<ScreenSpaceSubsquare> subsquareList;
	private float topChunkScreenSpaceY;    //The y value, in screen space, of the top of the chunk
	private float bottomChunkScreenSpaceY; 
	private GameBoard theBoard;
	
	public Chunk(GameBoard theBoard, int size) {
		subsquareList = new ArrayList<ScreenSpaceSubsquare>(size);
		this.theBoard = theBoard;
		topChunkScreenSpaceY = -1;
		bottomChunkScreenSpaceY = -1;
	}
	
	public float getTopChunkScreenSpace() {
		return topChunkScreenSpaceY;
	}
	
	public float getBottomChunkScreenSpace() {
		return bottomChunkScreenSpaceY;
	}
	
	
	
	public Point2f getBottomRowSubsquareRepresentative() {
		int lastIndex = subsquareList.size() - 1;
		if (lastIndex < 0)
			return new Point2f(-1,-1);
		return subsquareList.get(lastIndex).getPoint();
	}
	
	
	public List<ScreenSpaceSubsquare> getSubsquareList() {
		return subsquareList;
	}
	
	
	
	
	/**
	 * Creates a chunk between 
	 * @param topRowIndex
	 * @param bottomRowIndex
	 */
	public void createChunk(int topRowIndex, int bottomRowIndex) {
		if (!subsquareList.isEmpty())
			subsquareList.clear();
		
		topChunkScreenSpaceY = GameBoardSquare.boardToScreen(topRowIndex, 0).y;
		bottomChunkScreenSpaceY = GameBoardSquare.boardToScreen(bottomRowIndex, 0).y;
		
		int cols = theBoard.getCols();
		for (int i = topRowIndex; i < bottomRowIndex; ++i) {
			for (int j = 0; j < cols; ++j) {
				if (theBoard.isSet(i,j))
					subsquareList.add(new ScreenSpaceSubsquare(new Point2f(i,j), theBoard.getSquare(i,j).getColor()));
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
	 * This method removes floating point imprecisions after a chunk is done falling, using the current
	 * collision line in screen space. The bottom-most row of the chunk will be aligned with 
	 * collisionLine + pieceSize, the one above that will be aligned with collisionLine + 2*pieceSize,
	 * and so on. This method guarantees that all the chunks will convert back to logic space 
	 * without problems, and that any hiccups with the timer will be smoothed out.
	 * 
	 * @param collisionLine - The current collision line, in screen space.
	 * @return Returns true if the method successfully aligns the chunks, and false otherwise (it
	 * should never return false; this is just here for debugging).
	 */
	public boolean alignSubsquares(int collisionLine) {
		for (ScreenSpaceSubsquare s : subsquareList) {
			Point2f p = s.getPoint();
			p.x = (float)Math.round(p.x);
			p.y = (float)Math.round(p.y);	
		}
		return true;
	}
	
	
	
	
	public void render(Graphics graphics) {
		for (ScreenSpaceSubsquare s : subsquareList) {
			graphics.setColor(s.getColor());
			graphics.fillRect(s.getPoint().x, s.getPoint().y, TetrisGame.pieceSize, TetrisGame.pieceSize);
		}
	}
}
