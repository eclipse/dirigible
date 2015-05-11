
var graphicsPerURIMargin = {top: 20, right: 250, bottom: 30, left: 150},
    graphicsPerURIWidth = 1100 - graphicsPerURIMargin.left - graphicsPerURIMargin.right,
    graphicsPerURIHeight = 600 - graphicsPerURIMargin.top - graphicsPerURIMargin.bottom;

var graphicsPerURIParseDate = d3.time.format("%Y%m%d%H%M").parse;

var graphicsPerURIX = d3.time.scale()
    .range([0, graphicsPerURIWidth]);

var graphicsPerURIY = d3.scale.linear()
    .range([graphicsPerURIHeight, 0]);

var graphicsPerURIColor = d3.scale.category10();

var graphicsPerURIXAxis = d3.svg.axis()
    .scale(graphicsPerURIX)
    .orient("bottom");

var graphicsPerURIYAxis = d3.svg.axis()
    .scale(graphicsPerURIY)
    .orient("left");

var graphicsPerURILine = d3.svg.line()
    .interpolate("basis")
    .x(function(d) { return graphicsPerURIX(d.date); })
    .y(function(d) { return graphicsPerURIY(d.temperature); });

var graphicsPerURISvg = d3.select("#memory-log").append("svg")
    .attr("width", graphicsPerURIWidth + graphicsPerURIMargin.left + graphicsPerURIMargin.right)
    .attr("height", graphicsPerURIHeight + graphicsPerURIMargin.top + graphicsPerURIMargin.bottom)
  .append("g")
    .attr("transform", "translate(" + graphicsPerURIMargin.left + "," + graphicsPerURIMargin.top + ")");

d3.tsv("../memory?log", function(error, data) {
  graphicsPerURIColor.domain(d3.keys(data[0]).filter(function(key) { return key !== "date"; }));

  data.forEach(function(d) {
    d.date = graphicsPerURIParseDate(d.date);
  });

  var graphicsPerURIs = graphicsPerURIColor.domain().map(function(name) {
    return {
      name: name,
      values: data.map(function(d) {
        return {date: d.date, temperature: +d[name]};
      })
    };
  });

  graphicsPerURIX.domain(d3.extent(data, function(d) { return d.date; }));

  graphicsPerURIY.domain([
    d3.min(graphicsPerURIs, function(c) { return d3.min(c.values, function(v) { return v.temperature; }); }),
    d3.max(graphicsPerURIs, function(c) { return d3.max(c.values, function(v) { return v.temperature; }); })
  ]);
  
  // Legend
  var legend = graphicsPerURISvg.selectAll('g')
  	.data(graphicsPerURIs)
  	.enter()
  	.append('g')
  	.attr('class', 'legend');

  legend.append('rect')
  	.attr('x', graphicsPerURIWidth + 40)
  	.attr('y', function(d, i){ return i *  20;})
  	.attr('width', 10)
  	.attr('height', 10)
  	.style('fill', function(d) { 
  		return graphicsPerURIColor(d.name);
  	});

  legend.append('text')
  	.attr('x', graphicsPerURIWidth + 56)
  	.attr('y', function(d, i){ return (i *  20) + 9;})
  	.text(function(d){ return d.name; });
  // Legend  
  
  graphicsPerURISvg.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + graphicsPerURIHeight + ")")
      .call(graphicsPerURIXAxis);
	  
  graphicsPerURISvg.append("g")
      .attr("class", "y axis")
      .call(graphicsPerURIYAxis)
      .append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", ".71em")
      .style("text-anchor", "end");

  var graphicsPerURI = graphicsPerURISvg.selectAll(".graphicsPerURI")
      .data(graphicsPerURIs)
      .enter().append("g")
      .attr("class", "graphicsPerURI");

  graphicsPerURI.append("path")
      .attr("class", "line")
      .attr("d", function(d) { return graphicsPerURILine(d.values); })
      .style("stroke", function(d) { return graphicsPerURIColor(d.name); });

  graphicsPerURI.append("text")
      .datum(function(d) { return {name: d.name, value: d.values[d.values.length - 1]}; })
      .attr("transform", function(d) { return "translate(" + graphicsPerURIX(d.value.date) + "," + graphicsPerURIY(d.value.temperature) + ")"; })
      .attr("x", 3)
      .attr("dy", ".35em")
      .text(function(d) { return d.name; });
});