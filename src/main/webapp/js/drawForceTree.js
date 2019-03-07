function drawForceTree(json) {

	//var width = 960, height = 500, root;

	d3.select("svg").remove();

	var force = d3.layout.force().linkDistance(120).charge(-200).gravity(.05)
			.size([ width, height ]).on("tick", tick);	


	var svg = d3.select("#vis").append("svg")//.attr("width", width).attr(
		//	"height", height);

	var link = svg.selectAll(".link"), node = svg.selectAll(".node");

	root = json;//JSON.parse(json);
	console.log ("root in drawForceTree" + root.name);

    resize();
    d3.select(window).on("resize", resize);

	update();

	function update() {
		var nodes = flatten(root), links = d3.layout.tree().links(nodes);
		
		//console.log ("nodes " + nodes);

		// Restart the force layout.
		force.nodes(nodes).links(links).start();

		// Update links.
		link = link.data(links, function(d) {
			return d.target.id;
		});

		link.exit().remove();

		link.enter().insert("line", ".node").attr("class", "link");

		// Update nodes.
		node = node.data(nodes, function(d) {
			return d.id;
		});

		node.exit().remove();

		var nodeEnter = node.enter().append("g").attr("class", "node").on(
				"click", click).call(force.drag);

		nodeEnter.append("circle").attr("r", function(d) {
			return Math.sqrt(70);   //d.size) / 10 || 4.5;
		});

		nodeEnter.append("text").attr("dy", ".35em").text(function(d) {
			return d.name;
		});

		node.select("circle").style("fill", color);
		//node.select("circle").style("opacity", opacity);
	}


	function tick() {
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
	}

	function color(d) {
        return  d.name == root.name ? colorbrewer.BuPu[8][6]
            : d._children ?  colorbrewer.BuPu[8][5]
            : d.children ?  colorbrewer.BuPu[8][4]
            : colorbrewer.BuPu[8][1];


		// return  d.name == root.name ? "green"
		// : d._children ? "blueviolet" //"#3182bd" // collapsed package
		// : d.children ? "aqua"//"#c6dbef" // expanded package
		// : "magenta";//"#fd8d3c"; // leaf node
	}

   // colorbrewer.BuPu[8]
	
	function opacity(d) { 
		return  d.name == root.name ? 0.9
		: d._children ? 0.8 //"#3182bd" // collapsed package
		: d.children ? 0.5//"#c6dbef" // expanded package
		: 0.2;//"#fd8d3c"; // leaf node 
	} 

	// Toggle children on click.
	function click(d) {
		if (d3.event.defaultPrevented)
			return; // ignore drag
		if (d.children) {
			d._children = d.children;
			d.children = null;
		} else {
			d.children = d._children;
			d._children = null;
		}
		update();
	}

	// Returns a list of all nodes under the root.
	function flatten(root) {
		var nodes = [], i = 0;

		function recurse(node) {
			if (node.children)
				node.children.forEach(recurse);
			if (!node.id)
				node.id = ++i;
			nodes.push(node);
		}

		recurse(root);
		return nodes;
	}

    function resize() {
        width = window.innerWidth, height = window.innerHeight;
        svg.attr("width", width).attr("height", height);
        force.size([width, height]).resume();
    }
};
