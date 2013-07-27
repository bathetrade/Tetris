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
		setPoint(point.x, point.y);
	}
	
	public void setPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

}