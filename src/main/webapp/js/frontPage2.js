function frontPage2() {

    var json =
        '{"name": "british", "cooc": 1.0, "children": [{"name": "online", "cooc": 1.0, "children": [{"name": "archives"}]}]}';

    var jsonTree = JSON.parse(json2);
    {
        d3.select("svg").remove();
        var tree = d3.select("#vis").append("svg")

            .chart("tree.radial")

            //.diameter(500)
            .radius(function (d) {
                //	if (d.size)
                //	return Math.log(d.size);
                //	else
                return 5;
            }).levelGap(100).zoomable([0.1, 3]).collapsible(3);
        //.duration(200)
        //.sortable("_ASC_")

        tree.draw(json3);
    }
}