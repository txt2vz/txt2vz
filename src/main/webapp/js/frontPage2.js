function frontPage2() {

    var json2 =
        '{"name": "british", "cooc": 1.0, "children": [{"name": "online", "cooc": 2.0, "children": [{"name": "archives", "cooc": 3.2}]}]}';


    var jsonTree = JSON.parse(json2);


    d3.json(jsonTree, function (data2) {
        console.log("data " + data2.name);
    });

    {

        d3.select("svg").remove();
        var tree = d3.select("#vis").append("svg")

            .chart("tree.radial")

            //.diameter(500)
            .radius(function (d) {
                //	if (d.size)
                //	return Math.log(d.size);
                var e = d3.min(d.cooc);
                console.log("d.cooc is " + d.cooc + " e " + e);
                //	else
                return 5;
            }).levelGap(100).zoomable([0.1, 3]).collapsible(3);
        //.duration(200)
        //.sortable("_ASC_")

        tree.draw(jsonTree);
    }
}