import helpers.AnimationBuilder;
import helpers.FileManager;
import models.City;

import java.io.IOException;

public class Main {

	private static Integer frameCount = 10800;

	public static void main(String[] args) throws IOException {

		Integer idx = 0;

		for (int j = 0; j < 3; j++) {
			for (double d = 0.07; d < 0.65; d += 0.02) {
				City city = City.readCityFromFile("city.ns");
				city.initializeTraffic(d);

				AnimationBuilder ab = new AnimationBuilder(city);
				ab.addCurrentFrame(city);

				for (int i = 0; i < frameCount; i++) {
					city.evolve();
					ab.addCurrentFrame(city);
				}

				city.printFlowByDensity(idx++);
			}
		}
	}

}
