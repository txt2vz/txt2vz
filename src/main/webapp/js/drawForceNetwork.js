function drawForceNetwork(jsonlinks) {

	d3.select("svg").remove();
	var linksobj;

	if (jsonlinks == null || typeof jsonlinks == 'object') {
		linksobj = jsonlinks;
	} else
		linksobj = JSON.parse(jsonlinks);

	// var linksobj = jsonlinks; // JSON.parse(jsonlinks);
	var links = linksobj.links;
	var nodes = {};

	var w = 960,
		h = 800;
	var svg = d3.select("#vis").append("svg")//.attr("width", w).attr("height",
		//h);

	// Compute the distinct nodes from the links.
	links.forEach(function(link) {
		var sc = link.source;
		var tg = link.target;
		link.source = nodes[sc] || (nodes[sc] = {
				name: sc,
				numberLinks: 0,
				totalCooc: 0
			});
		link.target = nodes[tg] || (nodes[tg] = {
				name: tg,
				numberLinks: 0,
				totalCooc: 0
			});

		// count number of links for each node
		nodes[sc].numberLinks++;
		nodes[tg].numberLinks++;
		nodes[sc].totalCooc += link.cooc;
		nodes[tg].totalCooc += link.cooc;
	});

	var force = d3.layout.force().gravity(.05).charge(-200)//.size([w, h]);

	var linkCoocExtent = d3.extent(links, function(d) {
		return d.cooc
	});

	force.nodes(d3.values(nodes)).links(links).linkDistance(
		function(d) {

			var distanceScale = d3.scale.linear().domain(linkCoocExtent)
				.range([180, 80]);
			return distanceScale(d.cooc);

		}).start();

	// console.log(" links cooc extent " + linkCoocExtent);
	var link = svg.selectAll(".link").data(links).enter().append("line").attr(
		"stroke-width",
		function(d) {

			var linkWidthScale = d3.scale.linear().domain(linkCoocExtent)
				.range([0.1, 3]);
			return linkWidthScale(d.cooc);

		}).attr("class", "link");

	var totalCoocArray = [];
	var nodeTotalCoocExtent = d3.extent(d3.values(nodes), function(d) {
		totalCoocArray.push(d.totalCooc);
		return d.totalCooc
	});

	var node = svg.selectAll(".node").data(d3.values(nodes)).enter()
		.append("g").attr("class", "node").call(force.drag);

	var fontScale = d3.scale.linear().domain(nodeTotalCoocExtent).range(
		[8, 20]);

	node.append("rect").attr("width", function(d) {

		return d.name.length * (fontScale(d.totalCooc) / 2) + 20;
	}).attr("height", function(d) {
		return 20;
	}).attr("ry", 8).attr("rx", 8).attr("y", -15).attr("x", -5)

		.attr(
		"opacity",
		function(d) {
			var opacityScale = d3.scale.linear()
				.domain(nodeTotalCoocExtent).range([0.4, 0.7]);
			var opacityValue = opacityScale(d.totalCooc);
			return opacityValue;
		}).attr(
		"fill",
		function(d) {

			if (true){//document.getElementById('bupu').checked) {
				colorScale = d3.scale.quantile().domain(totalCoocArray)
					.range(colorbrewer.BuPu[8]);
			}; 
		/*	else
			if (document.getElementById('purd').checked) {
				colorScale = d3.scale.quantile().domain(totalCoocArray)
					.range(colorbrewer.PuRd[8]);
			} else
			if (document.getElementById('reds').checked) {
				colorScale = d3.scale.quantile().domain(totalCoocArray)
					.range(colorbrewer.Reds[8]);
			} else {
				colorScale = d3.scale.quantile().domain(totalCoocArray)
					.range(["magenta", "blueviolet", "cyan"]);
			};
*/
			var colorValue = colorScale(d.totalCooc);
			return colorValue;
		});

	node.append("text").attr("font-size", function(d) {

		var fontValue = fontScale(d.totalCooc);
		// console.log("TotalCooc" + d.totalCooc
		// + " fontValue " + fontValue);
		return fontValue;

	}).text(function(d) {
		return d.name; // + " cooc: " + d.totalCooc;
	});

    resize();
    d3.select(window).on("resize", resize);

	force.on("tick", function() {
		link.attr("x1", function(d) {
			return d.source.x;
		}).attr("y1", function(d) {
			return d.source.y;
		}).attr("x2", function(d) {
			return d.target.x;
		}).attr("y2", function(d) {
			return d.target.y;
		});

		node.attr("transform", function(d) {
			return "translate(" + d.x + "," + d.y + ")";
		});
	});

    function resize() {
        width = window.innerWidth, height = window.innerHeight;
        svg.attr("width", width).attr("height", height);
        force.size([width, height]).resume();
    }
}