var file;
var frames = [];
var frames_count;
var cityWidth;
var cityHeight;

var carWidth;
var canvas_size = 800;

function preload() {
    file = loadStrings('./animation.out');
}

function setup() {
    readAnimationInfo();
    frames_count = Math.floor((file.length - 3) / cityHeight);

    readFrames(file);
    frameRate(10);

    createCanvas(cityWidth * carWidth, cityHeight * carWidth);
}

function readAnimationInfo() {
    var splitted = file[0].trim().split(" ");
    cityWidth = parseInt(splitted[0]);
    cityHeight = parseInt(splitted[1]);
    carWidth = canvas_size / cityWidth;
}

function readFrames() {
    for (var k = 0; k < frames_count; k++) {
        var lanes = [];
        for (var i = 0; i < cityHeight; i++) {
            var serialized = file[k * cityHeight + i + 1];
            lanes.push(serialized.split(''));
        }
        frames.push(lanes);
    }
}

var current_frame = 0;
function draw() {
    background(120);

    frames[current_frame].forEach((lane, i) => {
        lane.forEach((cell, j) => {
            if (cell >= 0) {
                drawCar(i, j, cell);
            } else if (cell == '.') {
                drawNotDrivable(i, j);
            }
        });
        lane.forEach((cell, j) => {
            if (cell == '*') {
                drawRedLight(i, j);
            }
        });
    });


    current_frame = (++current_frame) % frames_count;
}

function mousePressed() {
    saveFrames('out', 'png', 1, 25, function(data) {
        print(data);
    });
}

colorsBySpeed = {
    0: '#eeeeee',
    1: '#daffaa',
    2: '#fcf185',
    3: '#ffc87c',
    4: '#f28c60',
    5: '#ff6b5b',
}

function drawCar(i, j, speed) {
    fill(colorsBySpeed[speed]);
    rect(carWidth * j, i * carWidth, carWidth, carWidth);
}

function drawRedLight(i, j) {
    fill('#ff000088');
    rect(carWidth * j, i * carWidth, carWidth, carWidth);
}

function drawNotDrivable(i, j) {
    fill('#555555');
    rect(carWidth * j, i * carWidth, carWidth, carWidth);
}