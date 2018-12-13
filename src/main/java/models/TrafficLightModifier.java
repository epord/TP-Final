package models;

import javafx.util.Pair;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class TrafficLightModifier{

	//Add a new Traffic light Class
	public static Integer addTrafficLightClass(City city, Integer onDuration, Integer offDuration, Integer phase, Boolean initiallyEnabled){
		TrafficLightClass trafficLightClass = new TrafficLightClass(onDuration,offDuration,phase,initiallyEnabled);
		city.getTrafficLightClasses().add(trafficLightClass);
		return city.getTrafficLightClasses().size()-1;
	}

	public static void replaceTrafficLightClass(City city, int index, Integer onDuration, Integer offDuration, Integer phase, Boolean initiallyEnabled){
		TrafficLightClass trafficLightClass = new TrafficLightClass(onDuration,offDuration,phase,initiallyEnabled);
		List<TrafficLightClass> classes = city.getTrafficLightClasses();
		if(index < classes.size()){
			//Add the old positions to the new class
			classes.get(index).getPositions()
					.stream().forEach(pair -> trafficLightClass.addTrafficLight(pair.getKey(),pair.getValue()));

			//Replace old class
			classes.remove(index);
			classes.add(index, trafficLightClass);
		} else {
			throw new IllegalStateException("no such traffic light");
		}
	}

	//Add a trafic light from the class to the xy position
	public static void addTrafficLight(City city, Integer classIndex, Integer i, Integer j){
		List<TrafficLightClass> classes = city.getTrafficLightClasses();
		if(classIndex < classes.size()){
			classes.get(classIndex).getPositions().add(new Pair<>(i, j));
		} else {
			throw new IllegalStateException("no such traffic light");
		}
	}

	public static void removeTrafficLights(City city){
		city.getTrafficLightClasses().clear();
		refreshCityCellsTrafficLightStatus(city);
	}

	public static void refreshCityCellsTrafficLightStatus(City city){
		//Blank the traffic light statuses
		for (int i = 0; i < city.getCityHeight(); i++) {
			for (int j = 0; j < city.getCityWidth(); j++) {
				city.getCellAt(i,j).setTrafficLight(false);
			}
		}

		city.getTrafficLightClasses()
				.stream()
				.forEach(trafficLightClass -> trafficLightClass.getPositions()
						.stream()
						//set the cells to the correct traffic light status
						.forEach(pair -> city.getCellAt(pair.getKey(),pair.getValue())
								.setTrafficLight(trafficLightClass.getStatus())));
	}
}
