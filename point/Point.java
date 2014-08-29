package point;

public class Point {
	
	public int x;
	public int y;
	
	public Point() {
		x = 0;
		y = 0;
	}
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(Point point) {
		set(point.x, point.y);
	}
	
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(Point p) {
		if (p != null) {
			this.x = p.x;
			this.y = p.y;
		}
	}
	
	public Point add(Vec2D v) {
		this.x += v.x;
		this.y += v.y;
		return this;
	}
	
	public Point subtract(Vec2D v) {
		this.x -= v.x;
		this.y -= v.y;
		return this;
	}
}