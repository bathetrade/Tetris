package manager;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import logic.GameBoard.PieceType;

import java.util.Map;
import java.util.HashMap;

public class ImageManager {

	private static Map<PieceType, Image> images;

	private ImageManager() {
	}

	static {
		images = new HashMap<PieceType, Image>();
		try {
			images.put(PieceType.PIECE_J,      new Image("images//GreenSquare.png"));
			images.put(PieceType.PIECE_L,      new Image("images//CyanSquare.png"));
			images.put(PieceType.PIECE_LINE,   new Image("images//YellowSquare.png"));
			images.put(PieceType.PIECE_S,      new Image("images//BlueSquare.png"));
			images.put(PieceType.PIECE_SQUARE, new Image("images//OrangeSquare.png"));
			images.put(PieceType.PIECE_T,      new Image("images//PinkSquare.png"));
			images.put(PieceType.PIECE_Z,      new Image("images//RedSquare.png"));
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public static Image getImage(PieceType type) {
		return images.get(type);
	}

}