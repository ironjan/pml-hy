var moveChart = dc.lineChart('#monthly-move-chart');

var margin = {top: 30, right: 20, bottom: 30, left: 50},
    width = 1200 - margin.left - margin.right,
    height = 600 - margin.top - margin.bottom;
// Set the ranges
var x = d3.time.scale().range([0, width]);
var y = d3.scale.linear().range([height, 0]);

// Define the axes
var xAxis = d3.svg.axis().scale(x)
    .orient("bottom").ticks(5);

var yAxis = d3.svg.axis().scale(y)
    .orient("left").ticks(5);

// Define the line
var valueline = d3.svg.line()
    .x(function(d) { return x(d.crawlingTime); })
    .y(function(d) { return y(d.free); });

// Adds the svg canvas
var svg = d3.select("body")
    .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
    .append("g")
        .attr("transform",
              "translate(" + margin.left + "," + margin.top + ")");

// Get the data
d3.json("/working_data_crawled", function(error, data) {
    // Add first and last with free = 0 to fix graph
    var last = data[data.length-1];
    last.free = +0;
    data.push(last);
   
    var first = data[0];
    first.free = +0;
    data.push(first);
    console.log(data[0]);    data.forEach(function(d) {
        console.log("d: " + d + ", " + d.crawlingTime + ", " + d.free);
        d.crawlingTime = d3.time.format.iso.parse(d.crawlingTime);
        d.free = +d.free;
    });

    // Scale the range of the data
    x.domain(d3.extent(data, function(d) { return d.crawlingTime; }));
    y.domain([0, d3.max(data, function(d) { return d.free; })]);

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
        .attr("d", valueline(data));

});
