var graphicsPerURIMargin = {top: 20, right: 400, bottom: 30, left: 50},
    graphicsPerURIWidth = 1200 - graphicsPerURIMargin.left - graphicsPerURIMargin.right,
    graphicsPerURIHeight = 300 - graphicsPerURIMargin.top - graphicsPerURIMargin.bottom;

var graphicsPerURIParseDate = d3.time.format("%Y%m%d%H").parse;

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

var graphicsPerURIsvg = d3.select("#hits-per-uri").append("svg")
    .attr("width", graphicsPerURIWidth + graphicsPerURIMargin.left + graphicsPerURIMargin.right)
    .attr("height", graphicsPerURIHeight + graphicsPerURIMargin.top + graphicsPerURIMargin.bottom)
  .append("g")
    .attr("transform", "translate(" + graphicsPerURIMargin.left + "," + graphicsPerURIMargin.top + ")");

d3.tsv("../acclog?hitsPerURI", function(error, data) {
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

  //Legend
  var graphicsPerURILegend = graphicsPerURIsvg.selectAll('g')
  	.data(graphicsPerURIs)
  	.enter()
  	.append('g')
  	.attr('class', 'legend');

  graphicsPerURILegend.append('rect')
  	.attr('x', graphicsPerURIWidth + 40)
  	.attr('y', function(d, i){ return i *  20;})
  	.attr('width', 10)
  	.attr('height', 10)
  	.style('fill', function(d) { 
  		return graphicsPerURIColor(d.name);
  	});

  graphicsPerURILegend.append('text')
  	.attr('x', graphicsPerURIWidth + 56)
  	.attr('y', function(d, i){ return (i *  20) + 9;})
  	.text(function(d){ return d.name; });
  // Legend 
  
  graphicsPerURIsvg.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + graphicsPerURIHeight + ")")
      .call(graphicsPerURIXAxis);
	  
  graphicsPerURIsvg.append("g")
      .attr("class", "y axis")
      .call(graphicsPerURIYAxis)
      .append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", ".71em")
      .style("text-anchor", "end");

  var graphicsPerURI = graphicsPerURIsvg.selectAll(".graphicsPerURI")
      .data(graphicsPerURIs)
      .enter().append("g")
      .attr("class", "graphicsPerURI");

  graphicsPerURI.append("path")
      .attr("class", "line")
      .attr("d", function(d) { return graphicsPerURILine(d.values); })
      .style("stroke", function(d) { return graphicsPerURIColor(d.name); });
});