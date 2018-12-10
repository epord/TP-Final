package models;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class TrafficLightClass {

	private Integer onDuration;
	private Integer offDuration;
    private Integer phase;
    private Boolean initiallyEnabled;
    private List<Pair<Integer, Integer>> positions = new ArrayList<>();

    private Integer lastToggleIteration;
    private Boolean status;

    public TrafficLightClass(Integer onDuration, Integer offDuration, Integer phase, Boolean initiallyEnabled) {
        this.onDuration = onDuration;
        this.offDuration = offDuration;
        this.phase = phase % (onDuration + offDuration);
        this.initiallyEnabled = initiallyEnabled;
        this.lastToggleIteration = 0;
		// Traffic light start at the opposite value defined in city file. Because it will switch to the proper value when currentIteration == phase.
		this.status = !initiallyEnabled;
    }

    public Boolean isInitiallyEnabled() {
        return initiallyEnabled;
    }

	public Integer getOnDuration() {
		return onDuration;
	}

	public Integer getOffDuration() {
		return offDuration;
	}

	public Integer getPeriod() {
    	return onDuration + offDuration;
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

	public Integer getLastToggleIteration() {
		return lastToggleIteration;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setLastToggleIteration(Integer lastToggleIteration) {
		this.lastToggleIteration = lastToggleIteration;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
}
