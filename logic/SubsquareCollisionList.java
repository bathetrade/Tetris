package logic;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

import pieces.ActivePiece;
import point.Point;

public class SubsquareCollisionList {

	private List<SubsquareCollision> collisionList;
	private boolean sameType = true;
	private boolean firstAddition = true;
	private ActivePiece.CollisionType firstCollisionType = ActivePiece.CollisionType.COL_NONE;
	
	
	
	
	public SubsquareCollisionList() {
		collisionList = new ArrayList<SubsquareCollision>(4);
	}
	
	
	
	
	public void add(SubsquareCollision s) {
		if (firstAddition) {
			firstCollisionType = s.getCollision();
			firstAddition = false;
		}
		
		if (s.getCollision() != firstCollisionType)
			sameType = false;
			
		
		collisionList.add(s);
	}
	
	
	
	
	/**
	 * This method only makes sense if all the subsquares have the same collision type. Make sure to call
	 * hasSameCollisionType() before calling this method.
	 * @return Returns the collision type of all the elements, assuming they're the same. If they're not
	 * the same, the method returns COL_NONE.
	 */
	public ActivePiece.CollisionType getCollisionType() {
		return firstCollisionType;
	}
	
	
	
	
	public List<SubsquareCollision> getList() {
		return collisionList;
	}
	
	
	
	/**
	 * Get the subsquare in the collision list which is the "farthest" out in the direction of collision.
	 * For example, if the active piece collides with other pieces after a left rotate (counter-clockwise), then
	 * the collision type is COL_LEFT and the most outstanding subsquare would be the farthest one to the left.
	 * This method is used to determine how much to "kick" a piece back in after a collision.
	 * @return
	 */
	public Point getMostOutstandingSubsquare() {
		if (!sameType)
			return new Point(-1,-1);
		
		
		if (firstCollisionType == ActivePiece.CollisionType.COL_LEFT) {
			Comparator<SubsquareCollision> leftCollisionSorter = new Comparator<SubsquareCollision>() {
				public int compare(SubsquareCollision s1, SubsquareCollision s2) {
					return s1.getSubsquare().y - s2.getSubsquare().y;
				}
			};
			Collections.min(collisionList, leftCollisionSorter);
		}
		
		
		else if (firstCollisionType == ActivePiece.CollisionType.COL_RIGHT){
			Comparator<SubsquareCollision> rightCollisionSorter = new Comparator<SubsquareCollision>() {
				public int compare(SubsquareCollision s1, SubsquareCollision s2) {
					return s1.getSubsquare().y - s2.getSubsquare().y;
				}
			};
			Collections.max(collisionList, rightCollisionSorter);
		}
		
		
		else if (firstCollisionType == ActivePiece.CollisionType.COL_BOTTOM) {
			Comparator<SubsquareCollision> bottomCollisionSorter = new Comparator<SubsquareCollision>() {
				public int compare(SubsquareCollision s1, SubsquareCollision s2) {
					return s1.getSubsquare().x - s2.getSubsquare().x;
				}
			};
			Collections.max(collisionList, bottomCollisionSorter);
		}
		
		else {
			Comparator<SubsquareCollision> topCollisionSorter = new Comparator<SubsquareCollision>() {
				public int compare(SubsquareCollision s1, SubsquareCollision s2) {
					return s1.getSubsquare().x - s2.getSubsquare().x;
				}
			};
			Collections.min(collisionList, topCollisionSorter);
		}
		
		return collisionList.get(0).getSubsquare();
	}
	
	
	
	
	public boolean isEmpty() {
		return collisionList.isEmpty();
	}
	
	
	
	
	public boolean hasSameCollisionType() {
		return sameType;
	}

	
	
	
}
