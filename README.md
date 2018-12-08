# Traffic Simulator: Automaton based on Nagel-Schreckenberg model


## Input file format
```
int(width) int(heigth) int(trafficLightsCount)
int(lightPeriod1) int(lightPhase1) bool(startEnable1)
int(lightPeriod2) int(lightPhase2) bool(startEnable2)
...
int(lightPeriodN) int(lightPhaseN) bool(startEnableN)
=== CITY MAP ==
```

The city map is composed by *int(height)* lines of *int(width)*:
- `.` for roads
- `*` for intersections
- `digit` to represent a traffic light type (settings of each traffic light class on top)
- `H` horizontal car spawner
- `V` vertical car spawner
- `other character` represents a non drivable cell

Example:
```
12 7 2
20 0 true
20 0 false
oooooVVooooo
ooooo..ooooo
ooooo00ooooo
H...1++.....
H...1++.....
ooooo..ooooo
ooooo..ooooo
```