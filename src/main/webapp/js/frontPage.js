function frontPage() {
	//console.log("in front page");
	var jl = {};
	jl.links = [ {
		'source' : 'txt2vz',
		'target' : 'by',
		'cooc' : 4,
		'rank' : 7

	}, {
		'source' : 'laurie',
		'target' : 'by',
		'cooc' : 4,
		'rank' : 8
	}, {
		'source' : 'txt2vz',
		'target' : 'document',
		'cooc' : 4,
		'rank' : 0
	}, {
		'source' : 'analysis',
		'target' : 'visualization',
		'cooc' : 4,
		'rank' : 2
	}, {
		'source' : 'summary',
		'target' : 'visualization',
		'cooc' : 4,
		'rank' : 3
	}, {
		'source' : 'concept',
		'target' : 'visualization',
		'cooc' : 4,
		'rank' : 4
	}, {
		'source' : 'document',
		'target' : 'mind',
		'cooc' : 4,
		'rank' : 7
	}, {
		'source' : 'mind',
		'target' : 'map',
		'cooc' : 4,
		'rank' : 7
	}, {
		'source' : 'document',
		'target' : 'visualization',
		'cooc' : 4,
		'rank' : 1
	}, {
		'source' : 'laurie',
		'target' : 'hirsch',
		'cooc' : 4,
		'rank' : 2
	} ];
	drawLinks(jl);
};