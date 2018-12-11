# Traffic Simulator: Automaton based on Nagel-Schreckenberg model


## Input file format
```
int(width) int(heigth) int(trafficLightsCount)
int(lightPeriod1_1) int(lightPeriod2_1) int(lightPhase_1) bool(startEnabled_1)
int(lightPeriod1_2) int(lightPeriod2_2) int(lightPhase_2) bool(startEnabled_2)
...
int(lightPeriod1_N) int(lightPeriod2_N) int(lightPhase_N) bool(startEnabled_N)
=== CITY MAP ==
```

The city map is composed by *int(height)* lines of *int(width)*:
- `|` for roads specifying vertical direction (for initial traffic generation)
- `_` for roads specifying horizontal direction (for initial traffic generation)
- `+` for intersections
- `digit` to represent a traffic light type (settings of each traffic light class on top)
- `H` horizontal car spawner
- `V` vertical car spawner
- `other character` represents a non drivable cell

Example:
```
12 7 2
20 10 0 true
10 20 0 false
oooooVVooooo
ooooo..ooooo
ooooo00ooooo
H...1++.....
H...1++.....
ooooo..ooooo
ooooo..ooooo
```