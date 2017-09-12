package lucene

class Test1 {

    static main (args){

        def t0 = new Tuple2("AA", "bb")
        def t1 = new Tuple2("AA", "b2")

        println "t1 0 " + t1[0]
        def m = [:]
        m << [(t0) :5]
        m << [(t1) :7]
        m << [(t1) :7]
        println "m $m"
        println "m get t1 " + m.get(t1)
        def t4 = new Tuple2("AA", "b2")
        println "m get t4 " + m.get(t4)

        def s0 = "s0"
        def s1 = "s1"
        def s2 = "s2"

        List l = []
        l << s0
        l << s1
        l<< s2

        println "l is $l"
        println "l combs " + [l,l].combinations()

//        [l,l].combinations{
//            println "c $it"
//            println "*************"
//            if (it.)

       // }


     }
}
