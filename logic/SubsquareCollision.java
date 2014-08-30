package logic;
import point.Point;
import pieces.ActivePiece;

public class SubsquareCollision {
	private Point subsquare;
	private ActivePiece.CollisionType colType;
	
	public SubsquareCollision(Point p) {
		subsquare = new Point(p);
		colType = ActivePiece.CollisionType.COL_NONE;
	}
	
	public SubsquareCollision(Point p, ActivePiece.CollisionType type) {
		subsquare = new Point(p);
		colType = type;
	}
	
	public void setCollision(ActivePiece.CollisionType type) {
		colType = type;
	}
	
	public ActivePiece.CollisionType getCollision() {
		return colType;
	}
	
	public Point getSubsquare() {
		return subsquare;
	}
}
