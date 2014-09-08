package animation;
import point.Point2f;
import org.newdawn.slick.Image;

public class ScreenSpaceSubsquare {
	private Point2f subsquare;
	private Image color;
	
	public ScreenSpaceSubsquare(Point2f subsquare, Image color) {
		this.subsquare = subsquare;
		this.color     = color;
	}
	
	public void set(Point2f subsquare, Image color) {
		this.subsquare = subsquare;
		this.color = color;
	}
	
	public Point2f getPoint() {
		return subsquare;
	}
	
	public Image getColor() {
		return color;
	}
	
}
