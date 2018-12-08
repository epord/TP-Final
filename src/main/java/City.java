import com.sun.org.apache.xpath.internal.operations.Bool;
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
    private Integer carsCount = 0;
    private Integer cityWidth;
    private Integer cityHeight;
    private Integer currentIteration = 0;

    private final Integer maxVelocity = 3;
    private final Double decelerationProbability = 0.3;
    private final Double horizontalSpawnRate = 0.2;
    private final Double verticalSpawnRate = 1.0;

    public City(Integer cityWidth, Integer cityHeight, Double density) {
        if (density > 1.0 || density < 0) throw new IllegalArgumentException("density must be >= 0.0 and <= 1.0");
        this.cityWidth = cityWidth;
        this.cityHeight = cityHeight;
        this.cells = getEmptyCells();
//        this.initialize(density);
    }

    public void evolve() {

        Random r = new Random();


        // Spawn cars
        horizontalSpawners.stream().forEach(cell -> {
            if (!cell.containsCar() && r.nextDouble() < horizontalSpawnRate) {
                Car newCar = new Car(r.nextInt(maxVelocity) + 1, Direction.HORIZONTAL);
                cell.setCar(newCar);
                cars.put(new Pair<>(cell.getI(), cell.getJ()), newCar);
            }
        });
        verticalSpawners.stream().forEach(cell -> {
            if (!cell.containsCar() && r.nextDouble() < verticalSpawnRate) {
                Car newCar = new Car(r.nextInt(maxVelocity), Direction.VERTICAL);
                cell.setCar(newCar);
                cars.put(new Pair<>(cell.getI(), cell.getJ()), newCar);
            }
        });


        // NaSh Rule #1: Accelerate
        cars.keySet().stream().forEach(position -> {
            Car car = cars.get(position);
            car.setVelocity(Math.min(car.getVelocity() + 1, maxVelocity));
        });


        // Nash Rule #2: Decelerate
        cars.keySet().stream().forEach(position -> {
            Car car = cars.get(position);
            Cell cell = cells.get(position.getKey()).get(position.getValue());
            Integer distanceToNextObstacle = getDistanceToObstacle(cell.getI(), cell.getJ(), car);
            car.setVelocity(Math.min(car.getVelocity(), distanceToNextObstacle - 1));
        });


        // NaSh Rule #3: Randomization
        cars.keySet().stream().forEach(position -> {
            Car car = cars.get(position);
            if (r.nextDouble() < decelerationProbability) {
                car.setVelocity(Math.max(car.getVelocity() - 1, 0));
            }
        });


        // NaSch Rule #4: Movement
        moveCars();


        // Toggle traffic lights
        trafficLightClasses.stream().forEach(trafficLightClass -> {
            if ((currentIteration + trafficLightClass.getPhase()) % trafficLightClass.getPeriod() == 0) {
                trafficLightClass.getPositions().forEach(position -> {
                    cells.get(position.getKey()).get(position.getValue()).toggleTrafficLight();
                });
            }
        });


        // Next iteration
        currentIteration++;

    }

    public Integer getCityWidth() {
        return cityWidth;
    }

    public Integer getCityHeight() {
        return cityHeight;
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

    private Integer getDistanceToObstacle(Integer i, Integer j, Car car) {
        Integer distanceToObstacle = null;
        Boolean isMovingHorizontally = car.isMovingHorizontally();

        for (int k = 1; distanceToObstacle == null && k < car.getVelocity() + 1; k++) {
            Boolean outOfMap = isMovingHorizontally ? j + k >= cityWidth : i + k >= cityHeight;
            if (outOfMap) {
                distanceToObstacle = Integer.MAX_VALUE;
            } else {
                Cell cell = isMovingHorizontally ? cells.get(i).get(j + k) : cells.get(i + k).get(j);
                distanceToObstacle = cell.isBlocked() ? k : null;
            }
        }
        return distanceToObstacle != null ? distanceToObstacle : Integer.MAX_VALUE;
    }

    private void moveCars() {
        clearCarsFromCells();

        cars.keySet().stream().forEach(position -> {
            Integer i = position.getKey();
            Integer j = position.getValue();
            Car car = cars.get(position);
            Boolean isMovingHorizontally = car.isMovingHorizontally();
            Boolean outOfMap = isMovingHorizontally ?
                    j + car.getVelocity() >= cityWidth : i + car.getVelocity() >= cityHeight;
            if (!outOfMap) {
                Cell destinationCell = isMovingHorizontally ?
                        cells.get(i).get(j + car.getVelocity())
                        : cells.get(i + car.getVelocity()).get(j);
                while (destinationCell.containsCar()) {
                    car.setVelocity(car.getVelocity() - 1);
                    destinationCell = isMovingHorizontally ?
                            cells.get(i).get(j + car.getVelocity())
                            : cells.get(i + car.getVelocity()).get(j);
                }
                destinationCell.setCar(car);
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
                if (cell.containsCar())
                    sb.append(cell.getCar().getVelocity());
                else if (cell.isTrafficLightOn()) {
                    sb.append("*");
                }
                else if (cell.isAvailable())
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

        City city = new City(cityWidth, cityHeight, 0.0);

        for (int i = 0; i < amountOfTrafficLights; i++) {
            city.trafficLightClasses.add(new TrafficLightClass(scanner.nextInt(), scanner.nextInt(), scanner.nextBoolean()));
        }

        scanner.useDelimiter("\n");
        Integer i = 0;
        Integer j;
        while(i < cityHeight) {
            j = 0;
            String line = scanner.next();
            if (line.length() != cityWidth) throw new IllegalStateException("City must be " + cityWidth + " width.");
            for (Character c: line.toCharArray()) {
                if (c == '.') {
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
                    if (trafficLightClass.isInitiallyEnabled()) {
                        city.cells.get(i).get(j).setTrafficLightEnabled(true);
                    }
                }
                j++;
            }
            i++;
        }
        return city;
    }


//    TRAFFIC LIGHTS NOT IMPLEMENTED YET

    private void initialize(Double density) {

//        cars = new ArrayList<>();
        Random r = new Random();
        while ((1.0 * carsCount) / (cityHeight * cityWidth) < density) {
            // TODO: more efficient generation
            Integer i = Math.abs( r.nextInt() % cityHeight);
            Integer j = Math.abs(r.nextInt() % cityWidth);
            if (!cells.get(i).get(j).containsCar()) {
                Cell c = cells.get(i).get(j);
//                c.setCar(new Car(Math.abs(r.nextInt() % maxVelocity)));
//                cars.add(c);
                carsCount++;
            }
        }
    }

    private void toggleTrafficLight(Integer pos) {
        for (int i = 0; i < cityHeight; i++) {
            cells.get(i).get(pos).toggleAvailability();
        }
    }

    private void copyTrafficLights(List<List<Cell>> oldCells, List<List<Cell>> newCells) {
        for (int i = 0; i < cityHeight; i++) {
            for (int j = 0; j < cityWidth; j++) {
                newCells.get(i).get(j).setAvailability(oldCells.get(i).get(j).isAvailable());
            }
        }
    }
}
