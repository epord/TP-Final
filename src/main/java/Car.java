public class Car {
    private Integer velocity;
    private Direction drivingDirection;

    public Car(Integer velocity, Direction drivingDirection) {
        this.velocity = velocity;
        this.drivingDirection = drivingDirection;
    }

    public Integer getVelocity() {
        return velocity;
    }

    public void setVelocity(Integer velocity) {
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
}
