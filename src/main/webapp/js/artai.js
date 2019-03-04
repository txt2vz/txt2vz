function artai() {

    var json =

    '{"name":"ai","cooc":5.509765625,"children":[{"name":"art","cooc":2.419921875,"children":[{"name":"klingemann","cooc":1.890625,"children":[{"name":"creativity","cooc":1.626953125,"children":[{"name":"limited"},{"name":"artist"}]}]},{"name":"moments","cooc":1.5078125,"children":[{"name":"machines","cooc":1.5,"children":[{"name":"imagination"}]}]},{"name":"auction"}]},{"name":"generated"}]}'

    var jsonTree = JSON.parse(json);
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

        tree.draw(jsonTree);
    }
}