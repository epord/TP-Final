package models;

import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class City {

	private List<List<Cell>> cells;
	private Map<Pair<Integer, Integer>, Car> cars = new HashMap<>();
	private List<TrafficLightClass> trafficLightClasses = new ArrayList<>();
	private List<Cell> horizontalSpawners = new ArrayList<>();
	private List<Cell> verticalSpawners = new ArrayList<>();
	private Integer cityWidth;
	private Integer cityHeight;
	private Integer currentIteration = 0;

	private final Integer maxVelocity = 3;
	private final Double decelerationProbability = 0.8;
	private final Double horizontalSpawnRate = 0.2;
    private final Double verticalSpawnRate = 0.1;
    private final Double laneChangeRate = 0.5;

    // Stats
	List<Integer> time = new ArrayList<>();
	List<Integer> carCount = new ArrayList<>();
	List<Integer> drivableCellsCount = new ArrayList<>();
	List<Double> density = new ArrayList<>();
	List<Double> meanVelocity = new ArrayList<>();
	List<Integer> deadCars = new ArrayList<>(); // amount of cars to exit the scene

	public City(Integer cityWidth, Integer cityHeight) {
		this.cityWidth = cityWidth;
		this.cityHeight = cityHeight;
		this.cells = getEmptyCells();
	}

	private List<List<Cell>> getEmptyCells() {
		List<List<Cell>> newCells = new ArrayList<>();
		for (int i = 0; i < cityHeight; i++) {
			newCells.add(new ArrayList<>());
			for (int j = 0; j < cityWidth; j++) {
				newCells.get(i).add(new Cell(i, j));
			}
		}
		return newCells;
	}

	public void initializeTraffic(Double density) {
		Random r = new Random();
		for (int i = 0; i < cityHeight; i++) {
			for (int j = 0; j < cityWidth; j++) {
				Cell cell = cells.get(i).get(j);
				if (r.nextDouble() < density && cell.getTrafficDirection() != null) {
					Car car = new Car(r.nextInt(maxVelocity), cell.getTrafficDirection());
					cell.setCar(car);
					cars.put(new Pair(i, j), car);
				}
			}
		}
	}

	public void evolve() {

		Random r = new Random();


		// NaSh Rule #1: Accelerate
		cars.keySet().parallelStream().forEach(position -> {
			Car car = cars.get(position);
			car.setVelocity(Math.min(car.getVelocity() + 1, maxVelocity));
		});


		// Nash Rule #2: Decelerate
		cars.keySet().parallelStream().forEach(position -> {
			Car car = cars.get(position);
			Cell cell = cells.get(position.getKey()).get(position.getValue());
			Integer distanceToNextObstacle = getDistanceToObstacle(cell.getI(), cell.getJ(), car);
			Integer velocityInSameLane = Math.min(car.getVelocity(), distanceToNextObstacle - 1);


			Integer distanceToNextIntersection = getDistanceToNextIntersection(position.getKey(), position.getValue(), car);

			// Should change lane?
			Double laneChangeValue = r.nextDouble();
			if (laneChangeValue < laneChangeRate && !cell.isIntersection() && distanceToNextIntersection > maxVelocity) {
				Integer laneDirection = laneChangeValue < laneChangeRate / 2 ? 1 : -1;
				Cell neighbor =  car.isMovingHorizontally() ?
						getCellAt(position.getKey() + laneDirection, position.getValue())
						: getCellAt(position.getKey(), position.getValue() + laneDirection);
				if (neighbor != null && !neighbor.isBlocked()) {
					Cell distantNeighbor =  car.isMovingHorizontally() ?
							getCellAt(position.getKey() + 2 * laneDirection, position.getValue())
							: getCellAt(position.getKey(), position.getValue() + 2 * laneDirection);
					Integer backwardDistance = car.isMovingHorizontally() ?
							getDistanceToPreviousCar(neighbor.getI(), neighbor.getJ(), car)
							: getDistanceToPreviousCar(neighbor.getI(), neighbor.getJ(), car);
					Integer distanceToNextCar = car.isMovingHorizontally() ?
							getDistanceToObstacle(neighbor.getI(), neighbor.getJ(), car)
							: getDistanceToObstacle(neighbor.getI(), neighbor.getJ(), car);
					Integer velocityInOtherLane = Math.min(car.getVelocity(), distanceToNextCar - 1);
					if ((distantNeighbor == null || !distantNeighbor.containsCar())
							&& velocityInOtherLane > velocityInSameLane && backwardDistance > maxVelocity) {
						Integer laneToChange = car.isMovingHorizontally() ? neighbor.getI() : neighbor.getJ();
						car.changeLane(laneToChange);
						car.setVelocity(velocityInOtherLane);
						return;
					}
				}
			}

			car.setVelocity(velocityInSameLane);

		});


		// NaSh Rule #3: Randomization
		cars.keySet().parallelStream().forEach(position -> {
			Car car = cars.get(position);
			if (r.nextDouble() < decelerationProbability) {
				car.setVelocity(Math.max(car.getVelocity() - 1, 0));
			}
		});


		// NaSch Rule #4: Movement
		moveCars();


		// Toggle traffic lights
		trafficLightClasses.parallelStream().forEach(trafficLightClass -> {
			if (currentIteration >= trafficLightClass.getPhase()) {
				if (currentIteration.equals(trafficLightClass.getPhase())) {
					trafficLightClass.setStatus(trafficLightClass.isInitiallyEnabled());
					trafficLightClass.setLastToggleIteration(currentIteration);
				} else {
					// If traffic light is on
					if (trafficLightClass.getStatus()) {
						if (currentIteration - trafficLightClass.getLastToggleIteration() == trafficLightClass.getOnDuration()) {
							trafficLightClass.setStatus(false);
							trafficLightClass.setLastToggleIteration(currentIteration);
						}
					} else {
						if (currentIteration - trafficLightClass.getLastToggleIteration() == trafficLightClass.getOffDuration()) {
							trafficLightClass.setStatus(true);
							trafficLightClass.setLastToggleIteration(currentIteration);
						}
					}
				}
				trafficLightClass.getPositions().forEach(position -> {
					cells.get(position.getKey()).get(position.getValue()).setTrafficLight(trafficLightClass.getStatus());
				});
			}
		});


		// Next iteration
		currentIteration++;


		// Stats
		saveStats();

	}

    private Integer getDistanceToObstacle(Integer i, Integer j, Car car) {
        Integer distanceToObstacle = null;
        Boolean isMovingHorizontally = car.isMovingHorizontally();

        for (int k = 1; distanceToObstacle == null && k < car.getVelocity() + 1; k++) {
			Cell cell = isMovingHorizontally ?
					cells.get(i).get(Math.floorMod(j + k, cityWidth))
					: cells.get(Math.floorMod(i + k, cityHeight)).get(j);
			distanceToObstacle = cell.isBlocked() ? k : null;
        }
        return distanceToObstacle != null ? distanceToObstacle : Integer.MAX_VALUE;
    }

    private Integer getDistanceToPreviousCar(Integer i, Integer j, Car car) {
        Integer distanceToObstacle = null;
        Boolean isMovingHorizontally = car.isMovingHorizontally();

        for (int k = 1; distanceToObstacle == null && k < maxVelocity; k++) {
			Cell cell = isMovingHorizontally ?
					cells.get(i).get(Math.floorMod(j - k, cityWidth))
					: cells.get(Math.floorMod(i - k, cityHeight)).get(j);
			distanceToObstacle = cell.containsCar() ? k : null;
        }
		return distanceToObstacle == null ? Integer.MAX_VALUE : distanceToObstacle;
    }

    private Integer getDistanceToNextIntersection(Integer i, Integer j, Car car) {
        Integer distanceToIntersection = null;
        Boolean isMovingHorizontally = car.isMovingHorizontally();

        for (int k = 1; distanceToIntersection == null && k < car.getVelocity(); k++) {
			Cell cell = isMovingHorizontally ?
					cells.get(i).get(Math.floorMod(j + k, cityWidth))
					: cells.get(Math.floorMod(i + k, cityHeight)).get(j);
			distanceToIntersection = cell.isIntersection() ? k : null;
        }
        return distanceToIntersection == null ? Integer.MAX_VALUE : distanceToIntersection;
    }

	private void moveCars() {
		clearCarsFromCells();

		deadCars.add(0);
		cars.keySet().stream().forEach(position -> {
			Integer i = position.getKey();
			Integer j = position.getValue();
			Car car = cars.get(position);
			Boolean isMovingHorizontally = car.isMovingHorizontally();
			Cell destinationCell;
			Integer laneToChange = car.getLaneToChange();
			if (laneToChange != null) {
				destinationCell = isMovingHorizontally ?
						cells.get(laneToChange).get(Math.floorMod(j + car.getVelocity(), cityWidth))
						: cells.get(Math.floorMod(i + car.getVelocity(), cityHeight)).get(laneToChange);
			} else {
				destinationCell = isMovingHorizontally ?
						cells.get(i).get(Math.floorMod(j + car.getVelocity(), cityWidth))
						: cells.get(Math.floorMod(i + car.getVelocity(), cityHeight)).get(j);
			}
			while (destinationCell.containsCar() && car.getVelocity() > 0) {
				car.setVelocity(car.getVelocity() - 1);
				destinationCell = isMovingHorizontally ?
						cells.get(destinationCell.getI()).get(Math.floorMod(j + car.getVelocity(), cityWidth))
						: cells.get(Math.floorMod(i + car.getVelocity(), cityHeight)).get(destinationCell.getJ());
			}
			if (destinationCell.isBlocked()) {
				destinationCell = cells.get(i).get(j);
			}
			destinationCell.setCar(car);
			car.resetLaneToChange();

			// Save dead cars for stats
			boolean isOut = isMovingHorizontally ?
					j + car.getVelocity() >= cityWidth : i + car.getVelocity() >= cityHeight;
			if (isOut) {
				deadCars.set(currentIteration, deadCars.get(currentIteration) + 1);
			}
		});

		cars = getCars();
	}

	private void clearCarsFromCells() {
		for (int i = 0; i < cityHeight; i++) {
			for (int j = 0; j < cityWidth; j++) {
				cells.get(i).get(j).removeCar();
			}
		}
	}

	private Map<Pair<Integer, Integer>, Car> getCars() {
		Map<Pair<Integer, Integer>, Car> cars = new HashMap<>();
		for (int i = 0; i < cityHeight; i++) {
			for (int j = 0; j < cityWidth; j++) {
				Cell cell = cells.get(i).get(j);
				if (cell.containsCar()) {
					cars.put(new Pair<>(i, j), cell.getCar());
				}
			}
		}
		return cars;
	}

	public String rasterize() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cityHeight; i++) {
			for (int j = 0; j < cityWidth; j++) {
				Cell cell = cells.get(i).get(j);
				if (cell.isTrafficLightOn()) {
					sb.append("*");
				} else if (cell.containsCar()) {
					sb.append(cell.getCar().getVelocity());
				} else if (cell.isAvailable())
					sb.append("_");
				else
					sb.append(".");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public static City readCityFromFile(String path) throws IOException {
		Scanner scanner = new Scanner(new File(path));
		Integer cityWidth = scanner.nextInt();
		Integer cityHeight = scanner.nextInt();
		Integer amountOfTrafficLights = scanner.nextInt();

		City city = new City(cityWidth, cityHeight);

		for (int i = 0; i < amountOfTrafficLights; i++) {
			city.trafficLightClasses.add(new TrafficLightClass(scanner.nextInt(), scanner.nextInt(), scanner.nextInt(), scanner.nextBoolean()));
		}

		String osName = System.getProperty("os.name").toLowerCase();
		boolean isWindows = osName.startsWith("windows");
		if (isWindows) scanner.useDelimiter("\r\n");
		else scanner.useDelimiter("\n");
		Integer i = 0;
		Integer j;
		while (i < cityHeight) {
			j = 0;
			String line = scanner.next();
			if (line.length() != cityWidth)
				throw new IllegalStateException("City must be " + cityWidth + " width.");
			for (Character c : line.toCharArray()) {
				if(c == '|') {
					city.cells.get(i).get(j).setAvailability(true);
					city.cells.get(i).get(j).setTrafficDirection(Direction.VERTICAL);
				} else if(c == '_') {
					city.cells.get(i).get(j).setAvailability(true);
					city.cells.get(i).get(j).setTrafficDirection(Direction.HORIZONTAL);
				} else if(c == '.') {
					city.cells.get(i).get(j).setAvailability(true);
				} else if (c == '+') {
					city.cells.get(i).get(j).setAvailability(true);
					city.cells.get(i).get(j).setIntersection(true);
				} else if (c == 'V') {
					city.cells.get(i).get(j).setAvailability(true);
					city.verticalSpawners.add(city.cells.get(i).get(j));
				} else if (c == 'H') {
					city.cells.get(i).get(j).setAvailability(true);
					city.horizontalSpawners.add(city.cells.get(i).get(j));
				} else if (c >= '0' && c <= '9') {
					city.cells.get(i).get(j).setAvailability(true);
					TrafficLightClass trafficLightClass = city.trafficLightClasses.get(c - '0');
					trafficLightClass.addTrafficLight(i, j);
					city.cells.get(i).get(j).setTrafficLight(trafficLightClass.getStatus());
				}
				j++;
			}
			i++;
		}

		return city;
	}

	public void saveStats() {
		Integer cityWidth = getCityWidth();
		Integer cityHeight = getCityHeight();

		this.time.add(getCurrentIteration());

		Double cumulatedVelocities = 0.0;
		Integer carCount = 0;
		Integer drivableCellsCount = 0;
		for (int i = 0; i < cityHeight; i++) {
			for (int j = 0; j < cityWidth; j++) {
				Cell cell = getCellAt(i, j);
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

	public void printStats() {
		System.out.println("time = " + time + ';');
		System.out.println("carCount = " + carCount + ';');
		System.out.println("drivableCellsCount = " + drivableCellsCount + ';');
		System.out.println("density = " + density + ';');
		System.out.println("meanVelocity = " + meanVelocity + ';');
		System.out.println("deadCars = " + deadCars + ';');
	}

	public void printFlowByDensity(Integer idx) {
		Integer totalDeadCars = deadCars.parallelStream().mapToInt(i -> i.intValue()).sum();
		System.out.println("density" + idx + " = " + density.get(0) + ";");
		System.out.println("flow" + idx + " = " + totalDeadCars.doubleValue() / deadCars.size() + ";");
	}

	Cell getCellAt(Integer i, Integer j) {
		if (i < 0 || i >= cityHeight || j < 0 || j >= cityWidth) {
			return null;
		}
		return cells.get(i).get(j);
	}

	public Integer getCityWidth() {
		return cityWidth;
	}

	public Integer getCityHeight() {
		return cityHeight;
	}

	Integer getCurrentIteration() {
		return currentIteration;
	}

}
