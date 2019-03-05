function partitionD(jsonText){

    var json = JSON.parse(jsonText);

    console.log("json test part " + jsonText );

    d3.select("svg").remove();
  //  var tree = d3.select("#vis").append("svg")
    var partition = d3.select("#vis").append("svg")

        .chart("partition.rectangle")

        .value("cooc")
        //.zoomable([1, 5])
        .collapsible()
        //.duration(200)
        .sortable("_DESC_")
    ;

    partition.draw(json);

}

