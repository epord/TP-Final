var file;
var frames = [];
var frames_count;
var roadWidth;
var roadLength;
var carsCount;

var carWidth;
var canvas_size = 1600;

function preload() {
    file = loadStrings('./animation.out');
}

function setup() {
    readAnimationInfo();
    frames_count = Math.floor((file.length - 3) / roadWidth);
    readFrames(file);

    frameRate(10);
    createCanvas(canvas_size, (roadWidth + 2) * carWidth);
}

function readAnimationInfo() {
    var splitted = file[0].trim().split(" ");
    roadWidth = parseInt(splitted[0]);
    roadLength = parseInt(splitted[1]);
    carsCount = parseInt(splitted[2]);
    carWidth = canvas_size / roadLength;
}

function readFrames() {
    for (var k = 0; k < frames_count; k++) {
        var lanes = [];
        for (var i = 0; i < roadWidth; i++) {
            var serialized = file[k * roadWidth + i + 1];
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
    rect(carWidth * j, (i + 1) * carWidth, carWidth, carWidth);
}

function drawRedLight(i, j) {
    fill('#ff000088');
    rect(carWidth * j, (i + 1) * carWidth, carWidth, carWidth);
}