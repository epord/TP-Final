package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityStats {

    List<Integer> time = new ArrayList<>();
    List<Integer> carCount = new ArrayList<>();
    List<Integer> drivableCellsCount = new ArrayList<>();
    List<Double> density = new ArrayList<>();
    List<Double> meanVelocity = new ArrayList<>();
    Map<Car, Integer> spawnTimes = new HashMap<>();
    Map<Integer, Double> avgHorizontalArrivalTime = new HashMap<>();
    Map<Integer, Double> avgVerticalArrivalTime = new HashMap<>();
    List<Integer> horizontalCarFlow = new ArrayList<>();
    List<Integer> verticalCarFlow = new ArrayList<>();

    public void saveStats(City city) {
    	Integer currentIteration = city.getCurrentIteration();
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


        // Average arrival times
        city.getJustSpawnedCars().forEach(car -> {
        	spawnTimes.put(car, currentIteration);
		});

        double horizontalSum = 0.0;
        int horizontalCount = 0;
        double verticalSum = 0.0;
        int verticalCount = 0;

        for(Car car: city.getJustRemovedCars()) {
        	if (car.getDrivingDirection().equals(Direction.HORIZONTAL)) {
        		horizontalSum += currentIteration - spawnTimes.get(car);
        		horizontalCount++;
        		spawnTimes.remove(car);
			} else if (car.getDrivingDirection().equals(Direction.VERTICAL)) {
        		verticalSum += currentIteration - spawnTimes.get(car);
        		verticalCount++;
        		spawnTimes.remove(car);
			}
		}

        if (horizontalCount > 0) {
			avgHorizontalArrivalTime.put(currentIteration, horizontalSum / horizontalCount);
		}
		if (verticalCount > 0) {
			avgVerticalArrivalTime.put(currentIteration, verticalSum / verticalCount);
		}

		// Car Flow
		horizontalCarFlow.add((int)city.getJustRemovedCars().stream().filter(car -> car.getDrivingDirection().equals(Direction.HORIZONTAL)).count());
		verticalCarFlow.add((int)city.getJustRemovedCars().stream().filter(car -> car.getDrivingDirection().equals(Direction.VERTICAL)).count());

    }

    public List<Integer> getTime() {
        return time;
    }

    public List<Integer> getCarCount() {
        return carCount;
    }

    public List<Integer> getDrivableCellsCount() {
        return drivableCellsCount;
    }

    public List<Double> getDensity() {
        return density;
    }

    public List<Double> getMeanVelocity() {
        return meanVelocity;
    }

    public Map<Car, Integer> getSpawnTimes() {
        return spawnTimes;
    }

    public Map<Integer, Double> getAvgHorizontalArrivalTime() {
        return avgHorizontalArrivalTime;
    }

    public Map<Integer, Double> getAvgVerticalArrivalTime() {
        return avgVerticalArrivalTime;
    }

    public Double getAverageArrivalTime() {
        Double horizontalCumulatedArrivalTimes = avgHorizontalArrivalTime.keySet().stream()
                .map(key -> avgHorizontalArrivalTime.get(key))
                .mapToDouble(Double::doubleValue)
                .sum();
        Double verticalCumulatedArrivalTimes = avgVerticalArrivalTime.keySet().stream()
                .map(key -> avgVerticalArrivalTime.get(key))
                .mapToDouble(Double::doubleValue)
                .sum();
        Double averageArrivalTime = (horizontalCumulatedArrivalTimes + verticalCumulatedArrivalTimes)
                / (avgHorizontalArrivalTime.size() + avgVerticalArrivalTime.size());
        return averageArrivalTime;
    }
}
