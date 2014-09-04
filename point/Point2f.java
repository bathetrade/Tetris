package point;

public class Point2f {
	float x;
	float y;
	
	public Point2f() {
		x = 0f;
		y = 0f;
	}
	
	public Point2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(Point2f p) {
		x = p.x;
		y = p.y;
	}
	
	public void set(Point p) {
		x = (float)p.x;
		y = (float)p.y;
	}
	
	public void add(Vec2D v) {
		x += v.x;
		y += v.y;
	}
	
	public void subtract(Vec2D v) {
		x -= v.x;
		y -= v.y;
	}
}
