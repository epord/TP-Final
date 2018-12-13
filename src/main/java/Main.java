import helpers.AnimationBuilder;
import helpers.FileManager;
import models.City;
import models.CityStats;
import models.TrafficLightModifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

	private static Integer frameCount = 1000;

	public static void main(String[] args) throws  IOException {
		normalSimulation();
//		generateTimeTravelGraphic();
	}

	private static void normalSimulation() throws  IOException {

		City city = City.readCityFromFile("bigCity.ns");

		CityStats cityStats = new CityStats();
		cityStats.saveStats(city);

		AnimationBuilder ab = new AnimationBuilder(city);
		ab.addCurrentFrame(city);

		for (int i = 0; i < frameCount; i++) {
			//			System.out.println("===== " + i + " =====");
			//			System.out.println(city.rasterize());
			city.evolve();
			ab.addCurrentFrame(city);
			cityStats.saveStats(city);
		}

		FileManager fm = new FileManager();
		fm.writeString("p5/frontend/animation.out", ab.getString());
	}

	private static void generateTimeTravelGraphic() throws IOException {

		Integer periodStep = 5;
		Integer phaseStep = 2;


		for (int ph = 0; ph <= 40; ph += phaseStep) {
			List<Double> lifetimes = new ArrayList<>();
			for (int pe = 5; pe < 120; pe += periodStep) {
				City city = City.readCityFromFile("avenue.ns");
				for (int i = 3; i <= 8; i++) {
					TrafficLightModifier.addTrafficLight(city, 10, i, 114);
					TrafficLightModifier.addTrafficLight(city, 11, i, 124);
					TrafficLightModifier.addTrafficLight(city, 12, i, 135);
					TrafficLightModifier.addTrafficLight(city, 13, i, 144);
				}
				for (int i = 0; i < 14; i++) {
					TrafficLightModifier.replaceTrafficLightClass(city, i, pe, pe, ph * i, true);
				}

				CityStats cityStats = new CityStats();
				cityStats.saveStats(city);

				AnimationBuilder ab = new AnimationBuilder(city);
				ab.addCurrentFrame(city);

				for (int i = 0; i < frameCount; i++) {
					//			System.out.println("===== " + i + " =====");
					//			System.out.println(city.rasterize());
					city.evolve();
					ab.addCurrentFrame(city);
					cityStats.saveStats(city);
				}

//				FileManager fm = new FileManager();
//				fm.writeString("p5/frontend/animation.out", ab.getString());

				lifetimes.add(cityStats.getAverageArrivalTime());
			}
			System.out.println(lifetimes + ";");
		}
	}

}
