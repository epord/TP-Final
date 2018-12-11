package models;

import java.util.ArrayList;
import java.util.List;

public class CityStats {

    List<Integer> time = new ArrayList<>();
    List<Integer> carCount = new ArrayList<>();
    List<Integer> drivableCellsCount = new ArrayList<>();
    List<Double> density = new ArrayList<>();
    List<Double> meanVelocity = new ArrayList<>();

    public void saveStats(City city) {
        Integer cityWidth = city.getCityWidth();
        Integer cityHeight = city.getCityHeight();

        this.time.add(city.getCurrentIteration());

        Double cumulatedVelocities = 0.0;
        Integer carCount = 0;
        Integer drivableCellsCount = 0;
        for (int i = 0; i < cityHeight; i++) {
            for (int j = 0; j < cityWidth; j++) {
                Cell cell = city.getCellAt(i, j);
                if (cell.containsCar()) {
                    carCount++;
                    cumulatedVelocities += cell.getCar().getVelocity();
                }
                if (cell.isAvailable()) {
                    drivableCellsCount++;
                }
            }
        }
        this.carCount.add(carCount);
        this.drivableCellsCount.add(drivableCellsCount);
        this.density.add(carCount.doubleValue() / drivableCellsCount);
        this.meanVelocity.add(cumulatedVelocities / carCount);

    }

}
