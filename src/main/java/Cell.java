public class Cell {
    private Car car;
    private Integer i;
    private Integer j;
    private boolean istrafficLightOn = false;

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

    public boolean isIstrafficLightOn() {
        return istrafficLightOn;
    }

    public void toggleTrafficLight() {
        istrafficLightOn = !istrafficLightOn;
    }

    public void setTrafficLight(boolean trafficLight) {
        this.istrafficLightOn = trafficLight;
    }

    public boolean containsCar() {
        return car != null;
    }

}