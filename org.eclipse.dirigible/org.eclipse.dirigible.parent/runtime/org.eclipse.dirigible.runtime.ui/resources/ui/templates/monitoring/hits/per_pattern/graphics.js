var graphicsPerPatternMargin = {top: 20, right: 400, bottom: 30, left: 50},
    graphicsPerPatternWidth = 1200 - graphicsPerPatternMargin.left - graphicsPerPatternMargin.right,
    graphicsPerPatternHeight = 300 - graphicsPerPatternMargin.top - graphicsPerPatternMargin.bottom;

var graphicsPerPatternParseDate = d3.time.format("%Y%m%d%H").parse;

var graphicsPerPatternX = d3.time.scale()
    .range([0, graphicsPerPatternWidth]);

var graphicsPerPatternY = d3.scale.linear()
    .range([graphicsPerPatternHeight, 0]);

var graphicsPerPatternColor = d3.scale.category10();

var graphicsPerPatternXAxis = d3.svg.axis()
    .scale(graphicsPerPatternX)
    .orient("bottom");

var graphicsPerPatternYAxis = d3.svg.axis()
    .scale(graphicsPerPatternY)
    .orient("left");

var graphicsPerPatternLine = d3.svg.line()
    .interpolate("basis")
    .x(function(d) { return graphicsPerPatternX(d.date); })
    .y(function(d) { return graphicsPerPatternY(d.temperature); });

var graphicsPerPatternSvg = d3.select("#hits-per-pattern").append("svg")
    .attr("width", graphicsPerPatternWidth + graphicsPerPatternMargin.left + graphicsPerPatternMargin.right)
    .attr("height", graphicsPerPatternHeight + graphicsPerPatternMargin.top + graphicsPerPatternMargin.bottom)
  .append("g")
    .attr("transform", "translate(" + graphicsPerPatternMargin.left + "," + graphicsPerPatternMargin.top + ")");

d3.tsv("../acclog?hitsPerPattern", function(error, data) {
  graphicsPerPatternColor.domain(d3.keys(data[0]).filter(function(key) { return key !== "date"; }));

  data.forEach(function(d) {
    d.date = graphicsPerPatternParseDate(d.date);
  });

  var graphicsPerPatterns = graphicsPerPatternColor.domain().map(function(name) {
    return {
      name: name,
      values: data.map(function(d) {
        return {date: d.date, temperature: +d[name]};
      })
    };
  });

  graphicsPerPatternX.domain(d3.extent(data, function(d) { return d.date; }));

  graphicsPerPatternY.domain([
    d3.min(graphicsPerPatterns, function(c) { return d3.min(c.values, function(v) { return v.temperature; }); }),
    d3.max(graphicsPerPatterns, function(c) { return d3.max(c.values, function(v) { return v.temperature; }); })
  ]);

  // Legend
  var graphicsPerPatternLegend = graphicsPerPatternSvg.selectAll('g')
  	.data(graphicsPerPatterns)
  	.enter()
  	.append('g')
  	.attr('class', 'legend');

  graphicsPerPatternLegend.append('rect')
  	.attr('x', graphicsPerPatternWidth + 40)
  	.attr('y', function(d, i){ return i *  20;})
  	.attr('width', 10)
  	.attr('height', 10)
  	.style('fill', function(d) { 
  		return graphicsPerPatternColor(d.name);
  	});

  graphicsPerPatternLegend.append('text')
  	.attr('x', graphicsPerPatternWidth + 56)
  	.attr('y', function(d, i){ return (i *  20) + 9;})
  	.text(function(d){ return d.name; });
  // Legend 
  
  graphicsPerPatternSvg.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + graphicsPerPatternHeight + ")")
      .call(graphicsPerPatternXAxis);
	  
  graphicsPerPatternSvg.append("g")
      .attr("class", "y axis")
      .call(graphicsPerPatternYAxis)
      .append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", ".71em")
      .style("text-anchor", "end");

  var graphicsPerPattern = graphicsPerPatternSvg.selectAll(".graphicsPerPattern")
      .data(graphicsPerPatterns)
      .enter().append("g")
      .attr("class", "graphicsPerPattern");

  graphicsPerPattern.append("path")
      .attr("class", "line")
      .attr("d", function(d) { return graphicsPerPatternLine(d.values); })
      .style("stroke", function(d) { return graphicsPerPatternColor(d.name); });
});