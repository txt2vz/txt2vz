package processText

import spock.lang.Specification

//in plain text wordparandcooc [[star, host]:7.2578125, [kepler, 1658]:6.03515625, [01, koi]:5.3125, [exoplanet, candidate]:5.0, [kepler, 1658b]:4.31640625, [kepler, space]:4.017578125, [telescope, space]:4.0, [years, 10]:4.0, [kepler, observations]:3.515625, [star, orbiting]:3.40625, [kepler, system]:3.130859375, [kepler, data]:3.0, [years, ago]:3.0, [earth, days]:3.0, [kepler, exoplanet]:2.619140625, [kepler, telescope]:2.537109375, [kepler, launch]:2.53515625, [kepler, illustration]:2.265625, [kepler, nasa]:2.2578125, [telescope, 10]:2.25]
//tree [name:star, cooc0:7.2578125, children:[[name:host], [name:orbiting, children:[[name:planet], [name:1658b]], coocYY:2.033203125], [name:earth]]]
//json: {"name":"star","cooc0":7.2578125,"children":[{"name":"host"},{"name":"orbiting","children":[{"name":"planet"},{"name":"1658b"}],"coocYY":2.033203125},{"name":"earth"}]}


class WordPairsToJSONTest extends Specification {
}
