import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Grid {

    private List<List<Cell>> cells;
    private List<Cell> cellsWithCar;
    private Integer carsCount = 0;
    private Integer roadLength;
    private List<Integer> trafficLightPositions = new ArrayList<>();
    private Integer currentIteration = 0;

    private final Integer roadWidth = 3;
    private final Integer maxVelocity = 5;
    private final Double decelerationProbability = 0.5;
    private final Integer trafficLightPeriod = 30;

    public Grid(Integer roadLength, Double density) {
        if (density > 1.0 || density < 0) throw new IllegalArgumentException("density must be >= 0.0 and <= 1.0");
        this.roadLength = roadLength;
        this.initialize(density);
    }

    private void initialize(Double density) {
        cells = getEmptyCells();

        cellsWithCar = new ArrayList<>();
        Random r = new Random();
        while ((1.0 * carsCount) / (roadWidth * roadLength) < density) {
            // TODO: more efficient generation
            Integer i = Math.abs( r.nextInt() % roadWidth);
            Integer j = Math.abs(r.nextInt() % roadLength);
            if (!cells.get(i).get(j).containsCar()) {
                Cell c = cells.get(i).get(j);
                c.setCar(new Car(Math.abs(r.nextInt() % maxVelocity)));
                cellsWithCar.add(c);
                carsCount++;
            }
        }
    }

    public void evolve() {

        // Toggle traffic lights
        if (currentIteration % trafficLightPeriod == 0) {
            toggleTrafficLights();
        }


        // NaSh Rule #1: Accelerate
        cellsWithCar.stream().forEach(cell -> {
            Car car = cell.getCar();
            car.setVelocity(Math.min(car.getVelocity() + 1, maxVelocity));
        });


        // Nash Rule #2: Decelerate
        cellsWithCar.stream().forEach(cell -> {
            Car car = cell.getCar();
            Integer distanceToNextObstacle = Math.floorMod(getNextBlockingPosition(cell.getI(), cell.getJ()) - cell.getJ(), roadLength);
            car.setVelocity(Math.min(car.getVelocity(), Math.floorMod(distanceToNextObstacle - 1, roadLength)));
        });


        // NaSh Rule #3: Randomization
        Random r = new Random();
        cellsWithCar.stream().forEach(cell -> {
            Car car = cell.getCar();
            if (r.nextDouble() < decelerationProbability) {
                car.setVelocity(Math.max(car.getVelocity() - 1, 0));
            }
        });


        // NaSh Rule #4: Movement
        List<List<Cell>> newCells = getEmptyCells();
        List<Cell> newCellsWithCar = new ArrayList<>();
        moveCars(cellsWithCar, newCells, newCellsWithCar);
        copyTrafficLights(cells, newCells);
        cells = newCells;
        cellsWithCar = newCellsWithCar;


        // Next iteration
        currentIteration++;

    }

    public Integer getCarsCount() {
        return carsCount;
    }

    public Integer getRoadLength() {
        return roadLength;
    }

    public Integer getRoadWidth() {
        return roadWidth;
    }

    public void addTrafficLight(Integer position) {
        if (position < roadLength) {
            trafficLightPositions.add(position);
        }
    }

    private List<List<Cell>> getEmptyCells() {
        List<List<Cell>> newCells = new ArrayList<List<Cell>>();
        for (int i = 0; i < roadWidth; i++) {
            newCells.add(new ArrayList<Cell>());
            for (int j = 0; j < roadLength; j++) {
                newCells.get(i).add(new Cell(i, j));
            }
        }
        return newCells;
    }

    /**
     * Returns the next j position in i lane where the car will be blocked (car or traffic light).
     * @param i lane
     * @param j position in the lane
     * @return next blocking posiition in i lane
     */
    private Integer getNextBlockingPosition(Integer i, Integer j) {
        Integer nextBlockingPosition = null;
        for (int k = 1; k <= roadLength && nextBlockingPosition == null; k++) {
            Cell cell = cells.get(i).get((j + k) % roadLength);
            nextBlockingPosition = cell.containsCar() || cell.isIstrafficLightOn() ? (j + k) % roadLength : null;
        }
        return nextBlockingPosition;
    }

    private void toggleTrafficLights() {
        for (int i = 0; i < roadWidth; i++) {
            for (Integer trafficLightPosition: trafficLightPositions) {
                cells.get(i).get(trafficLightPosition).toggleTrafficLight();
            }
        }
    }

    private void moveCars(List<Cell> oldCellsWithCar, List<List<Cell>> newCells, List<Cell> newCellsWithCar){
        oldCellsWithCar.stream().forEach(cell -> {
            Car car = cell.getCar();
            Cell cellWithCar = newCells.get(cell.getI()).get(Math.floorMod(cell.getJ() + car.getVelocity(), roadLength));
            cellWithCar.setCar(car);
            newCellsWithCar.add(cellWithCar);
        });
    }

    private void copyTrafficLights(List<List<Cell>> oldCells, List<List<Cell>> newCells) {
        for (int i = 0; i < roadWidth; i++) {
            for (int j = 0; j < roadLength; j++) {
                newCells.get(i).get(j).setTrafficLight(oldCells.get(i).get(j).isIstrafficLightOn());
            }
        }
    }

    public String rasterize() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < roadWidth; i++) {
            for (int j = 0; j < roadLength; j++) {
                Cell cell = cells.get(i).get(j);
                if (cell.containsCar())
                    sb.append(cell.getCar().getVelocity());
                else if (cell.isIstrafficLightOn())
                    sb.append("*");
                else
                    sb.append("-");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static Grid readGridFromFile(String path) throws FileNotFoundException, IOException {
        Scanner scanner = new Scanner(new File(path));
        Integer roadLength = scanner.nextInt();

        Grid grid = new Grid(roadLength, 0.0);

        while(scanner.hasNextInt()) {
            grid.addTrafficLight(scanner.nextInt());
        }

        scanner.useDelimiter("\n");
        Integer i = 0;
        Integer j;
        while(scanner.hasNext() && i < 3) {
            j = 0;
            String line = scanner.next();
            if (line.length() != roadLength) throw new IllegalStateException("Road must be " + roadLength + " long.");
            for (Character c: line.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    Cell cell = grid.cells.get(i).get(j);
                    cell.setCar(new Car(c - '0'));
                    grid.cellsWithCar.add(cell);
                    grid.carsCount++;
                }
                j++;
            }
            i++;
        }
        return grid;
    }
}
