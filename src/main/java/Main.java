import java.io.IOException;

public class Main {

    private static Integer frameCount = 1000;

    public static void main(String[] args) throws IOException {
        City city = City.readCityFromFile("city.ns");
//        City city = City.readCityFromFile("city_no_traffic_lights.ns");
        AnimationBuilder ab = new AnimationBuilder(city);
        ab.addCurrentFrame(city);

        for (int i = 0; i < frameCount; i++) {
            System.out.println("===== " + i + " =====");
            System.out.println(city.rasterize());
            city.evolve();
            ab.addCurrentFrame(city);
        }

        FileManager fm = new FileManager();
        fm.writeString("p5/frontend/animation.out", ab.getString());
    }

}
