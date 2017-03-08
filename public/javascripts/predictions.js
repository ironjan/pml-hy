function drawPredictions(){
var moveChart = dc.lineChart('#predictions-chart');

var margin = {top: 30, right: 20, bottom: 30, left: 50},
    width = 1200 - margin.left - margin.right,
    height = 600 - margin.top - margin.bottom;
// Set the ranges
var x = d3.time.scale().range([0, width]);
var y = d3.scale.linear().range([height, 0]);

// Define the axes
var xAxis = d3.svg.axis().scale(x)
    .orient("bottom").ticks(10);

var yAxis = d3.svg.axis().scale(y)
    .orient("left").ticks(10);

// Define the line
var valueline = d3.svg.line()
    .x(function(d) { return x(d.predictedTime); })
    .y(function(d) { return y(d.prediction); })
    .interpolate("linear");

// Adds the svg canvas
var svg = d3.select("#predictions-chart")
    .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
    .append("g")
        .attr("transform",
              "translate(" + margin.left + "," + margin.top + ")");

// Get the data
d3.json("/api/predictions/latest", function(error, data) {
    data.forEach(function(d) {
        d.predictedTime = d3.time.format.iso.parse(d.predictedTime);
        d.prediction = +d.prediction;
    });

    // Scale the range of the data
    x.domain(d3.extent(data, function(d) { return d.predictedTime; }));
    y.domain([0, d3.max(data, function(d) { return d.prediction; })]);

    // Add the X Axis
    svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis);

    // Add the Y Axis
    svg.append("g")
        .attr("class", "y axis")
        .call(yAxis);

    // Add the valueline path.
    svg.append("path")
        .attr("class", "line")
        .attr("d", valueline(data))
        .attr("stroke", "black")
        .attr("stroke-width", 1)
        .attr("fill", "none");

    svg.selectAll("dot")
        .data(data)
        .enter().append("circle")
            .attr("r", 2)
            .attr("cx", function(d) { return x(d.predictedTime); })
            .attr("cy", function(d) { return y(d.prediction); });
});
}

drawPredictions();