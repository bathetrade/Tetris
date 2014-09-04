package tetrisgame;

import logic.GameBoard;
import timer.Timer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Input;
import org.newdawn.slick.Color;
import org.newdawn.slick.Music;
import pieces.GameBoardSquare.MoveType;
import point.Point;
import point.Vec2D;

public class TetrisGame extends BasicGame {

	private Input input						= null;
	private Music tetrisTheme               = null;
	private boolean isKeyDown 				= false;
	private GameBoard theBoard				= null;
	private Timer timer                     = null;
	private  boolean gameOver               = false;
	private long baseTime                   = 0;
	public static final int windowWidth     = 800;
	public static final int windowHeight    = 600;
	public static final int pieceSize       = 24;   //Size of a Tetris piece's "sub square"
	public static final int blockWidth      = 10;   //Width of playing area in blocks
	public static final int blockHeight     = 20;   //Height of playing area in blocks
	public static final int numInvisRows    = 1;    //Invisible rows at top for extra space
	public static final Vec2D boardToScreenOffsetVector;
	
	//Initialize the vector used to change from "logic space" to screen space 
	static {
		Point screenCenter = new Point(windowWidth/2, windowHeight/2);
		int screenCenterX  = windowWidth / 2;
		int screenCenterY  = windowHeight / 2;
		int hbw            = (pieceSize*blockWidth) / 2;
		int hbh            = (pieceSize*blockHeight) / 2;
		int topLeftBoardX  = screenCenterX - hbw;
		int topLeftBoardY  = screenCenterY - hbh;
		
		//Get the vector that translates the origin of the visible game area to the origin of the
		//	on-screen playing area.
		//For instance, if there are two invisible rows, and we want the game area to have a blockHeight of
		//	10 (a total of 12 rows), then the 0th row and the 1st row are invisible. The rows 2 through 11 are visible. So, we
		//	want to map (2,0) (2nd row, 0th column) to the on-screen playing area's origin (i.e., topLeftBoard).
		boardToScreenOffsetVector = new Vec2D(topLeftBoardX, topLeftBoardY - numInvisRows * pieceSize);
	}
	
	
	
	
	public TetrisGame(String title) {
		super(title);
		theBoard = new GameBoard(blockHeight + numInvisRows, blockWidth);
		timer    = new Timer();
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
		
		//Initialize sound
		try {
			tetrisTheme = new Music(new String("sounds//SMB-X.XM"));
			//tetrisTheme.loop();
			tetrisTheme.setVolume(0.2f);
		}
		catch(SlickException e) {
			e.printStackTrace();
		}
		
		
		//Disable FPS counter
		container.setShowFPS(true);
		
		//Initialize primitive timer
		timer.start();
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
		
		boolean moveNow = false; //Override default piece timer
		int moveAmount = 1;
		
		//Check input
		if (!gameOver) {
			
			if (input.isKeyDown(Input.KEY_ESCAPE))
				gameOver = true;
			
			if (input.isKeyDown(Input.KEY_LEFT)) {
				if (!isKeyDown) {
					theBoard.getActivePiece().move(MoveType.MOVE_LEFT, moveAmount);
					isKeyDown = true;
				}
			}
			
			else if (input.isKeyDown(Input.KEY_RIGHT)){
				if (!isKeyDown) {
					theBoard.getActivePiece().move(MoveType.MOVE_RIGHT, moveAmount);
					isKeyDown = true;
				}
			}
			
			else if (input.isKeyDown(Input.KEY_DOWN)) {
				if (!isKeyDown) {
					if (!theBoard.getActivePiece().move(MoveType.MOVE_DOWN, moveAmount)) {
						if (!theBoard.spawnPiece())
							gameOver = true;
					}
					timer.reset();
					isKeyDown = true;
				}
			}
			
			else if (input.isKeyDown(Input.KEY_SPACE)) {
				if (!isKeyDown) {
					theBoard.getActivePiece().dropPiece();
					isKeyDown = true;
					moveNow = true;
				}
			}
			
			
			//Dvorak configuration; for Qwerty, change to z and x respectively.  
			else if (input.isKeyDown(Input.KEY_SEMICOLON)) {
				if (!isKeyDown) {
					theBoard.getActivePiece().rotate(true);
					isKeyDown = true;
				}
			}
			
			else if (input.isKeyDown(Input.KEY_Q)) {
				if (!isKeyDown) {
					theBoard.getActivePiece().rotate(false);
					isKeyDown = true;
				}
			}
			
			else isKeyDown = false;
		
			//Check timer to see if it's time to move the active piece down
			if (Timer.nanoToSeconds(timer.getElapsedTime()) > 1 || moveNow == true) {
				if (!theBoard.getActivePiece().move(MoveType.MOVE_DOWN, moveAmount)){
					theBoard.setClearRowsFlag(true);
					if (!theBoard.spawnPiece())
						gameOver = true;
				}
				timer.reset();
			}
		}
		
		else {
			System.out.println("Displaying game over screen");
			container.exit();
		}
		
	}
	
	
	
	
}
