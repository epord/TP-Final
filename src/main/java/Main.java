import java.io.IOException;

public class Main {

    private static Integer frameCount = 1000;

    public static void main(String[] args) throws IOException {
        Grid grid = new Grid(180, 0.2);
        grid.addTrafficLight(20, 30);
        grid.addTrafficLight(100, 15);

//        Grid grid = Grid.readGridFromFile("input.ns");

        FileManager fm = new FileManager();
        AnimationBuilder ab = new AnimationBuilder(grid);

        ab.addCurrentFrame(grid);
        System.out.println(grid.rasterize());

        for (int i = 0; i < frameCount; i++) {
            grid.evolve();
            ab.addCurrentFrame(grid);
            System.out.println(grid.rasterize());
        }

        fm.writeString("p5/frontend/animation.out", ab.getString());
    }

}
