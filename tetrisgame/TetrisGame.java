package tetrisgame;

import logic.GameBoard;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Input;
import org.newdawn.slick.Color;

import pieces.ActivePiece.CollisionType;
import pieces.GameBoardSquare.MoveType;

public class TetrisGame extends BasicGame {

	private Input input						= null;
	private boolean isKeyDown 				= false;
	private GameBoard theBoard				= null;
	private  boolean gameOver               = false;
	private long baseTime                   = 0;
	public static final int pieceSize       = 24;   //Size of a Tetris piece's "sub square"
	public static final int blockWidth      = 10;   //Width of playing area in blocks
	public static final int blockHeight     = 20;   //Height of playing area in blocks
	
	public TetrisGame(String title) {
		super(title);
		theBoard = new GameBoard(blockHeight, blockWidth);
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		
		//Draw Tetris playing area
		int centerX       = container.getWidth()/2;
		int centerY       = container.getHeight()/2;
		int playingWidth  = pieceSize * blockWidth;
		int playingHeight = pieceSize * blockHeight;
		g.setColor(Color.white);
		g.drawRect(centerX - playingWidth/2, centerY - playingHeight/2, playingWidth, playingHeight);
		
		//Render all pieces on the board
		theBoard.render(container, g);
		
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		
		//Initialize input
		input = new Input(container.getHeight());
		
		//Disable FPS counter
		container.setShowFPS(true);
		
		//Initialize primitive timer
		baseTime = System.nanoTime();
		
		//Spawn first piece
		if (!theBoard.spawnPiece()) {
			System.out.println("This should never happen");
			container.exit();
		}
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		
		//Check input
		if (!gameOver) {
			if (input.isKeyDown(Input.KEY_ESCAPE))
				gameOver = true;
			
			if (input.isKeyDown(Input.KEY_LEFT)) {
				if (!isKeyDown) {
					theBoard.getActivePiece().move(MoveType.MOVE_LEFT);
					isKeyDown = true;
				}
			}
			
			else if (input.isKeyDown(Input.KEY_RIGHT)){
				if (!isKeyDown) {
					theBoard.getActivePiece().move(MoveType.MOVE_RIGHT);
					isKeyDown = true;
				}
			}
			
			else if (input.isKeyDown(Input.KEY_DOWN)) {
				if (!isKeyDown) {
					if (theBoard.getActivePiece().move(MoveType.MOVE_DOWN) == CollisionType.COL_BOTTOM) {
						if (!theBoard.spawnPiece())
							gameOver = true;
					}
					baseTime  = System.nanoTime(); //Reset timer
					isKeyDown = true;
				}
			}
			
			else if (input.isKeyDown(Input.KEY_Z)) {
				if (!isKeyDown) {
					theBoard.getActivePiece().rotate();
					isKeyDown = true;
				}
			}
			else isKeyDown = false;
		
		//Check timer to see if it's time to move the active piece down
			long currentTime = System.nanoTime();
			if ((currentTime - baseTime)/1000000000 > 3) {
				if (theBoard.getActivePiece().move(MoveType.MOVE_DOWN) == CollisionType.COL_BOTTOM) {
					if (!theBoard.spawnPiece())
						gameOver = true;
				}
				baseTime = currentTime;
				/*theBoard.printBoard();
				System.out.println("..............................");
				System.out.println(".............................."); */
			}
		}
		
		else {
			System.out.println("Displaying game over screen");
			container.exit();
		}
		
	}	

}
