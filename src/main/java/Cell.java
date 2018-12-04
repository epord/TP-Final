public class Cell {
    private Car car;
    private Integer i;
    private Integer j;
    private boolean isTrafficLightOn = false;

    public Cell(Integer i, Integer j) {
        this.i = i;
        this.j = j;
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
        this.car = c;
    }

    public boolean isTrafficLightOn() {
        return isTrafficLightOn;
    }

    public void toggleTrafficLight() {
        isTrafficLightOn = !isTrafficLightOn;
    }

    public void setTrafficLight(boolean trafficLight) {
        this.isTrafficLightOn = trafficLight;
    }

    public boolean containsCar() {
        return car != null;
    }

}