public class Cell {
    private Car car;
    private Integer i;
    private Integer j;
    private boolean isAvailable = false;
    private boolean isIntersection;

    public Cell(Integer i, Integer j, boolean isAvailable, boolean isIntersection) {
        this.i = i;
        this.j = j;
        this.isAvailable = isAvailable;
        this.isIntersection = isIntersection;
    }

    public Cell(Integer i, Integer j) {
        this(i, j, false, false);
    }

    public Integer getI() {
        return i;
    }

    public Integer getJ() {
        return j;
    }

    public Car getCar() {
        return this.car;
    }

    public void setCar(Car c) {
        if (this.car != null) throw new IllegalStateException("A car is already present in his cell.");
        this.car = c;
    }

    public boolean isIntersection() {
        return isIntersection;
    }

    public void setIntersection(boolean intersection) {
        isIntersection = intersection;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void toggleAvailability() {
        isAvailable = !isAvailable;
    }

    public void setAvailability(boolean availability) {
        this.isAvailable = availability;
    }

    public boolean containsCar() {
        return car != null;
    }

}