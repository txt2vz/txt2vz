function frontPage() {
	//console.log("in front page");
	var jl = {};
	jl.links = [ {
		'source' : 'british',
		'target' : 'online',
		'cooc' : 4,
		'rank' : 7

	}, {
		'source' : 'british',
		'target' : 'archives',
		'cooc' : 4,
		'rank' : 8
	}, {
		'source' : 'archives',
		'target' : 'online',
		'cooc' : 4,
		'rank' : 0
	}, {
		'source' : 'archives',
		'target' : 'collections',
		'cooc' : 4,
		'rank' : 2
	}, {
		'source' : 'archives',
		'target' : 'academic',
		'cooc' : 4,
		'rank' : 3
	}, {
		'source' : 'academic',
		'target' : 'publishers',
		'cooc' : 4,
		'rank' : 4
	}, {
		'source' : 'online',
		'target' : 'website',
		'cooc' : 4,
		'rank' : 7
	}, {
		'source' : 'website',
		'target' : '3 million',
		'cooc' : 4,
		'rank' : 7
	}, {
		'source' : '3 million',
		'target' : 'records',
		'cooc' : 4,
		'rank' : 1
	}, {
		'source' : 'archives',
		'target' : 'history',
		'cooc' : 4,
		'rank' : 2
	} ];
	drawForceNetwork(jl);
};