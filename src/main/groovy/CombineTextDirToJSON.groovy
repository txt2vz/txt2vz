import groovy.time.TimeCategory
import groovy.time.TimeDuration
import processText.WordPairsExtractor
import processText.WordPairsToJSON

class CombineTextDirToJSON {

    static void main(String[] args) {

        final Date startRun = new Date()
        final float powerValue = 0.5f
        final int maxLinks = 200
        final int maxWords = 20

        def testDir =

                //          /D:\boa\TestData\rec.sport.hockey/
                //       /D:\boa\TestData\sci.crypt/
              //      /D:\boa\TestData\sci.space/
                //     /D:\boa\War Crimes Text Files_Combined/
              //           /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\sci.crypt/

               //       /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\crude/

               //          /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\coffee/
                //        /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\sugar/
              //        /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\rec.sport.hockey/
             //          /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\sci.space/
                //        /C:\Users\aceslh\Dataset\space100/
                ///C:\Users\aceslh\Dataset\space100/
                //      /C:\Users\aceslh\Dataset\space100\59871/
                //       /C:\Users\aceslh\Dataset\boa\hockey/
                //    /C:\Users\aceslh\Dataset\boa\christian/
                //        /C:\Users\aceslh\Dataset\spaceHockeyChristianBOA/
          //          /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\War Crimes Text Files_Combined/

         //       /C:\Users\aceslh\lngit\txt2vz\src\main\groovy\boa\testMulti2.txt/
              //  /C:\Users\aceslh\lngit\txt2vz\src\main\groovy\boa\eb21.txt/

                /C:\Users\aceslh\lngit\txt2vz\src\main\groovy\boa\secrecy\600/

          //    /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\B/
//
    //        /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\A/

        //     /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\G/
        //       /C:\Users\aceslh\Dataset\space100/
        // //    /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\testDir/

        //     /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOA\rawText\Japan11037.txt/

        //   /D:\boa\TestData\QuarterlyIntel8338.txt/
        // /D:\boa\TestData\Japan11037.txt/
        //       /D:\boa\C/
      //   /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOA\rawText\QuarterlyIntel8338.txt/

     //    /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOA\rawText\4363.txt/


        File file = new File(testDir)
        WordPairsExtractor wpe
        Tuple3<Map<Tuple2<String, String>, Double>, Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wpData


        if (file.isDirectory()) {
            wpe = new WordPairsExtractor(powerValue, 200, 20)
            wpData = wpe.processDirectory(file)

        } else if (file.isFile()) {
            wpe = new WordPairsExtractor(powerValue, 200, 40)
            wpData = wpe.processText(file)
        }

        def wordPairAndCooc = wpData.first
        def wordPairAndCoocFreqBoost = wpData.second
        def stemInfo = wpData.third

        WordPairsToJSON wptj = new WordPairsToJSON(stemInfo)
        println " "
        println "Cooc based:"
        String jsonTree = wptj.getJSONtree(wordPairAndCooc)
        String jsonGraph = wptj.getJSONgraph(wordPairAndCooc.take(20))

        println ""
        println "Freq boost based: "
        wptj = new WordPairsToJSON(stemInfo)

        String jsonTreeF = wptj.getJSONtree(wordPairAndCoocFreqBoost)
        String jsonGraphF = wptj.getJSONgraph(wordPairAndCoocFreqBoost)

        String jsonTreeFs = wptj.getJSONtree(wordPairAndCoocFreqBoost.take(80))
        String jsonGraphFs = wptj.getJSONgraph(wordPairAndCoocFreqBoost.take(20))

        println ""
        println "Final:"
        println "jsonGraph $jsonGraph  "
        println "jsonTree $jsonTree  "

        def fnameGraphWithDir = 'jsonOut3/' + file.getName() + 'graphDIR.json'
        def fnameTreeWithDir = 'jsonOut3/' + file.getName() + 'treeDIR.json'
        def fnameGraphWithDirF = 'jsonOut3/' + file.getName() + 'graphDIRF.json'
        def fnameTreeWithDirF = 'jsonOut3/' + file.getName() + 'treeDIRF.json'

        def fnameGraphWithDirFs = 'jsonOut3/' + file.getName() + 'graphDIRFs.json'
        def fnameTreeWithDirFs = 'jsonOut3/' + file.getName() + 'treeDIRFs.json'


        def outFileGraph = new File(fnameGraphWithDir)
        def outFileTree = new File(fnameTreeWithDir)
        def outFileGraphF = new File(fnameGraphWithDirF)
        def outFileTreeF = new File(fnameTreeWithDirF)

        def outFileGraphFs = new File(fnameGraphWithDirFs)
        def outFileTreeFs = new File(fnameTreeWithDirFs)

        outFileGraph.write(jsonGraph)
        outFileTree.write(jsonTree)
        outFileGraphF.write(jsonGraphF)
        outFileTreeF.write(jsonTreeF)

        outFileGraphFs.write(jsonGraphFs)
        outFileTreeFs.write(jsonTreeFs)


        final Date endRun = new Date()
        TimeDuration duration = TimeCategory.minus(endRun, startRun)

        println "Duration: $duration"
    }
}
