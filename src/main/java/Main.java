import helpers.AnimationBuilder;
import helpers.FileManager;
import models.City;
import models.CityStats;

import java.io.IOException;

public class Main {

	private static Integer frameCount = 1000;

	public static void main(String[] args) throws IOException {
//		City city = City.readCityFromFile("avenue.ns");
		City city = City.readCityFromFile("city.ns");
//        City city = City.readCityFromFile("city_no_traffic_lights.ns");
//		city.initializeTraffic(0.2);

		CityStats cityStats = new CityStats();
		cityStats.saveStats(city);

		AnimationBuilder ab = new AnimationBuilder(city);
		ab.addCurrentFrame(city);

		for (int i = 0; i < frameCount; i++) {
			System.out.println("===== " + i + " =====");
			System.out.println(city.rasterize());
			city.evolve();
			ab.addCurrentFrame(city);
			cityStats.saveStats(city);
		}

		FileManager fm = new FileManager();
		fm.writeString("p5/frontend/animation.out", ab.getString());
	}

}
