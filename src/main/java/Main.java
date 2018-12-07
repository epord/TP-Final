import java.io.IOException;

public class Main {

    private static Integer frameCount = 1000;

    public static void main(String[] args) throws IOException {
        Avenue avenue = new Avenue(180, 0.2);
        avenue.addTrafficLight(20, 30);
        avenue.addTrafficLight(100, 15);

//        Avenue avenue = Avenue.readAvenueFromFile("input.ns");

        FileManager fm = new FileManager();
        AnimationBuilder ab = new AnimationBuilder(avenue);

        ab.addCurrentFrame(avenue);
        System.out.println(avenue.rasterize());

        for (int i = 0; i < frameCount; i++) {
            avenue.evolve();
            ab.addCurrentFrame(avenue);
            System.out.println(avenue.rasterize());
        }

        fm.writeString("p5/frontend/animation.out", ab.getString());
    }

}
