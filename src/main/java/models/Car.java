package models;

public class Car {
	private Integer velocity;
	private Direction drivingDirection;
	private Integer laneToChange;

	public Car(Integer velocity, Direction drivingDirection) {
		this.velocity = velocity;
		this.drivingDirection = drivingDirection;
	}

	public Integer getVelocity() {
		return velocity;
	}

	public void setVelocity(Integer velocity) {
		if (velocity < 0) throw new IllegalStateException("Velocity must be positive.");
		this.velocity = velocity;
	}

	public Direction getDrivingDirection() {
		return drivingDirection;
	}

	public void setDrivingDirection(Direction drivingDirection) {
		this.drivingDirection = drivingDirection;
	}

	public boolean isMovingHorizontally() {
		return drivingDirection.equals(Direction.HORIZONTAL);
	}

	public boolean isMovingVertically() {
		return !isMovingHorizontally();
	}

	public void changeLane(Integer laneIndex) {
		laneToChange = laneIndex;
	}

	public void resetLaneToChange() {
		laneToChange = null;
	}

	public Integer getLaneToChange() {
		return laneToChange;
	}
}
