var graphicsPerProjectMargin = {top: 20, right: 400, bottom: 30, left: 50},
    graphicsPerProjectWidth = 1200 - graphicsPerProjectMargin.left - graphicsPerProjectMargin.right,
    graphicsPerProjectHeight = 300 - graphicsPerProjectMargin.top - graphicsPerProjectMargin.bottom;

var graphicsPerProjectParseDate = d3.time.format("%Y%m%d%H").parse;

var graphicsPerProjectX = d3.time.scale()
    .range([0, graphicsPerProjectWidth]);

var graphicsPerProjectY = d3.scale.linear()
    .range([graphicsPerProjectHeight, 0]);

var graphicsPerProjectColor = d3.scale.category10();

var graphicsPerProjectXAxis = d3.svg.axis()
    .scale(graphicsPerProjectX)
    .orient("bottom");

var graphicsPerProjectYAxis = d3.svg.axis()
    .scale(graphicsPerProjectY)
    .orient("left");

var graphicsPerProjectLine = d3.svg.line()
    .interpolate("basis")
    .x(function(d) { return graphicsPerProjectX(d.date); })
    .y(function(d) { return graphicsPerProjectY(d.temperature); });

var graphicsPerProjectSvg = d3.select("#hits-per-project").append("svg")
    .attr("width", graphicsPerProjectWidth + graphicsPerProjectMargin.left + graphicsPerProjectMargin.right)
    .attr("height", graphicsPerProjectHeight + graphicsPerProjectMargin.top + graphicsPerProjectMargin.bottom)
  .append("g")
    .attr("transform", "translate(" + graphicsPerProjectMargin.left + "," + graphicsPerProjectMargin.top + ")");

d3.tsv("../acclog?hitsPerProject", function(error, data) {
  graphicsPerProjectColor.domain(d3.keys(data[0]).filter(function(key) { return key !== "date"; }));

  data.forEach(function(d) {
    d.date = graphicsPerProjectParseDate(d.date);
  });

  var hitsPerProject= graphicsPerProjectColor.domain().map(function(name) {
    return {
      name: name,
      values: data.map(function(d) {
        return {date: d.date, temperature: +d[name]};
      })
    };
  });

  graphicsPerProjectX.domain(d3.extent(data, function(d) { return d.date; }));

  graphicsPerProjectY.domain([
    d3.min(hitsPerProject, function(c) { return d3.min(c.values, function(v) { return v.temperature; }); }),
    d3.max(hitsPerProject, function(c) { return d3.max(c.values, function(v) { return v.temperature; }); })
  ]);

  //Legend
  var graphicsPerProjectLegend = graphicsPerProjectSvg.selectAll('g')
  	.data(hitsPerProject)
  	.enter()
  	.append('g')
  	.attr('class', 'legend');

  graphicsPerProjectLegend.append('rect')
  	.attr('x', graphicsPerProjectWidth + 40)
  	.attr('y', function(d, i){ return i *  20;})
  	.attr('width', 10)
  	.attr('height', 10)
  	.style('fill', function(d) { 
  		return graphicsPerProjectColor(d.name);
  	});

  graphicsPerProjectLegend.append('text')
  	.attr('x', graphicsPerProjectWidth + 56)
  	.attr('y', function(d, i){ return (i *  20) + 9;})
  	.text(function(d){ return d.name; });
  // Legend 
  
  graphicsPerProjectSvg.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + graphicsPerProjectHeight + ")")
      .call(graphicsPerProjectXAxis);
	  
  graphicsPerProjectSvg.append("g")
      .attr("class", "y axis")
      .call(graphicsPerProjectYAxis)
      .append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", ".71em")
      .style("text-anchor", "end");

  var hitPerProject = graphicsPerProjectSvg.selectAll(".hitPerProject")
      .data(hitsPerProject)
      .enter().append("g")
      .attr("class", "hitPerProject");

  hitPerProject.append("path")
      .attr("class", "line")
      .attr("d", function(d) { return graphicsPerProjectLine(d.values); })
      .style("stroke", function(d) { return graphicsPerProjectColor(d.name); });
});