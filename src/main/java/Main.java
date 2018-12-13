import helpers.AnimationBuilder;
import helpers.FileManager;
import models.City;
import models.CityStats;
import models.TrafficLightModifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

	private static Integer frameCount = 5000;

	public static void main(String[] args) throws  IOException {
//		normalSimulation();
//		generateTimeTravelGraphic();
//		generateFlowAndTimesGraphincsIntersections();
		getLifeTimesIntersections();
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
		System.out.println(cityStats.getAverageFlow());
		System.out.println(cityStats.getAverageArrivalTime());
		FileManager fm = new FileManager();
		fm.writeString("p5/frontend/animation.out", ab.getString());
	}


	private static void getLifeTimesIntersections() throws  IOException {

		Integer periodStep = 10;
		for (int pv = periodStep; pv < 120; pv += periodStep) {
			List<Double> lifetimes = new ArrayList<>();
			for (int ph = periodStep; ph < 120; ph += periodStep) {
				City city = City.readCityFromFile("bigCity.ns");

				for (int i = 0; i < 8; i += 2) {
					TrafficLightModifier.replaceTrafficLightClass(city, i, pv, ph, 8 * i, true);
				}
				for (int i = 1; i < 8; i += 2) {
					TrafficLightModifier.replaceTrafficLightClass(city, i, ph, pv, 8 + 8 * i, true);
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

				lifetimes.add(cityStats.getAverageArrivalTime());
			}
			System.out.println(lifetimes + ";");
		}
	}


	private static void generateFlowAndTimesGraphincsIntersections() throws  IOException {
		
		Integer idx = 0;
		System.out.println("meansFlow = [];");
		System.out.println("meansDuration = [];");
		System.out.println("stdFlow = [];");
		System.out.println("stdDuration = [];");
		for (Double initialFlow = 0.5; initialFlow <= 5 ; initialFlow += 0.5) {

			List<Double> flows = new ArrayList<>();
			List<Double> durations = new ArrayList<>();
			for (int j = 0; j < 5; j++) {

				City city = City.readCityFromFile("bigCityNoLights.ns");
				city.setHorizontalSpawnRate(initialFlow);
				city.setVerticalSpawnRate(initialFlow);
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
				flows.add(cityStats.getAverageFlow());
				durations.add(cityStats.getAverageArrivalTime());
			}
			System.out.println("flow" + idx + " = " + flows + ";");
			System.out.println("durations" + idx + " = " + durations + ";");
			System.out.println("meansFlow = [meansFlow mean(flow" + idx + ")]" + ";");
			System.out.println("stdFlow = [stdFlow std(flow" + idx + ")]" + ";");
			System.out.println("meansDuration = [meansDuration mean(durations" + idx + ")]" + ";");
			System.out.println("stdDuration = [stdDuration std(durations" + idx++ + ")]" + ";");
		}
	}

	private static void generateTimeTravelGraphic() throws IOException {

		Integer periodStep = 5;
		Integer phaseStep = 2;


		for (int ph = 0; ph <= 40; ph += phaseStep) {
			List<Double> lifetimes = new ArrayList<>();
			for (int pe = periodStep; pe < 120; pe += periodStep) {
				City city = City.readCityFromFile("avenue.ns");
//				for (int i = 3; i <= 8; i++) {
//					TrafficLightModifier.addTrafficLight(city, 10, i, 114);
//					TrafficLightModifier.addTrafficLight(city, 11, i, 124);
//					TrafficLightModifier.addTrafficLight(city, 12, i, 135);
//					TrafficLightModifier.addTrafficLight(city, 13, i, 144);
//				}
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
