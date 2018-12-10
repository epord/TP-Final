package models;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class TrafficLightClass {

    private Integer period;
    private Integer phase;
    private Boolean initiallyEnabled;
    private List<Pair<Integer, Integer>> positions = new ArrayList<>();

    public TrafficLightClass(Integer period, Integer phase, Boolean initiallyEnabled) {
        this.period = period;
        this.phase = phase;
        this.initiallyEnabled = initiallyEnabled;
    }

    public Boolean isInitiallyEnabled() {
        return initiallyEnabled;
    }

    public Integer getPeriod() {
        return period;
    }

    public Integer getPhase() {
        return phase;
    }

    public List<Pair<Integer, Integer>> getPositions() {
        return positions;
    }

    public void addTrafficLight(Integer i, Integer j) {
        positions.add(new Pair<>(i, j));
    }
}
