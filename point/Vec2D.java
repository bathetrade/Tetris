package point;

public class Vec2D extends Point {
	
	public Vec2D() {
		super();
	}
	
	public Vec2D(int x, int y) {
		super(x,y);
	}
	
	public Vec2D(Vec2D v) {
		x = v.x;
		y = v.y;
	}
}
