package animation;
import point.Point2f;
import org.newdawn.slick.Color;
public class ScreenSpaceSubsquare {
	private Point2f subsquare;
	private Color color;
	
	public ScreenSpaceSubsquare(Point2f subsquare, Color color) {
		this.subsquare = subsquare;
		this.color     = color;
	}
	
	public void set(Point2f subsquare, Color color) {
		this.subsquare = subsquare;
		this.color = color;
	}
	
	public Point2f getPoint() {
		return subsquare;
	}
	
	public Color getColor() {
		return color;
	}
	
}
