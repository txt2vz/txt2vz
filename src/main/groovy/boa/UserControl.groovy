package boa

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import processText.LinkBoost
import processText.WordPairsExtractor
import processText.WordPairsToJSON
import groovy.json.*

class UserControl {


    final Date startRun = new Date()
    final float powerValue = 0.5f
    final int maxLinks = 200
    final int maxWords = 20
    final static String outDir = 'boaData/json/'

    //      /C:\Users\aceslh\IdeaProjects\txt2vz\boaData\j2\ /

    def static textLocation =

            //          /D:\boa\TestData\rec.sport.hockey/
            //       /D:\boa\TestData\sci.crypt/

            //     /D:\boa\War Crimes Text Files_Combined/
            //           /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\sci.crypt/

            //       /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\crude/

            //          /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\coffee/
            //        /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\sugar/
            //        /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\rec.sport.hockey/
            //          /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\sci.space/
            //        /C:\Users\aceslh\Dataset\space100/
            ///C:\Users\aceslh\Dataset\space100/
            //       /C:\Users\aceslh\Dataset\boa\hockey/
            //    /C:\Users\aceslh\Dataset\boa\christian/
            //        /C:\Users\aceslh\Dataset\spaceHockeyChristianBOA/
            //          /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\War Crimes Text Files_Combined/
            //       /C:\Users\aceslh\lngit\txt2vz\src\main\groovy\boa\secrecy\599/

            //            /boaData\text\secrecy\593/
            //   /boaData\text\secrecy\598\ev598doc11098.txt/

//    /boaData\text\secrecy\590\ev590doc10903.txt/

            //           /C:\Users\aceslh\IdeaProjects\txt2vz\boaData\text\secrecy\599\ev599doc11102.txt/
            //    /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\B/
            //     /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\G/
            //       /C:\Users\aceslh\Dataset\space100/
            // //    /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\testDir/
//
            /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOAexamples\rawText\Japan11037.txt/

    // /D:\boa\TestData\Japan11037.txt/
    //       /D:\boa\C/
    //      /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOAexamples\rawText\QuarterlyIntel8338.txt/

    //    /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOA\rawText\4363.txt/


    static void main(String[] args) {
        File textFile_s = new File(textLocation)
        def ctdtj = new UserControl()
        ctdtj.combineDir(textFile_s, outDir)
    }

    void combineDir(File textFile_s, String outDir) {

        WordPairsExtractor wpe
        Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wpData

        if (textFile_s.isDirectory()) {
            println "DIR found"
            wpe = new WordPairsExtractor(powerValue, 200, 20)
            wpData = wpe.processDirectory(textFile_s)

        } else if (textFile_s.isFile()) {
            println "FIle found"
            wpe = new WordPairsExtractor(powerValue, 200, 80)
            wpData = wpe.processText(textFile_s)
        }

        //def wordPairAndCooc = wpData.first
        def json = JsonOutput.toJson(wpData)

        File f = new File('wpData.json')
        f.write(json)
        //    new File('wpData.json').write(json)

        def jsonSlurper = new JsonSlurper()
       // Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wpData2 = jsonSlurper.parse(f)
        Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wpData2 = wpData

        Map<Tuple2<String, String>, Double> t2Cooc = wpData2.first

        def stemInfo = wpData2.second
        def wordPairAndCoocFreqBoost = LinkBoost.linkBoost(t2Cooc
        )
                  //      ',japanes')
              //  ',british')

        println "Freq boost based: "
        def wptj = new WordPairsToJSON(stemInfo)

        String jsonTreeF = wptj.getJSONtree(wordPairAndCoocFreqBoost)
        String jsonGraphF = wptj.getJSONgraph(wordPairAndCoocFreqBoost)

        println ""
        println "Final:"
        println "jsonGraph $jsonGraphF  "
        println "jsonTree $jsonTreeF  "

        def fnameGraphWithDirF = outDir + textFile_s.getName() + 'graphF.json'
        def fnameTreeWithDirF = outDir + textFile_s.getName() + 'treeF.json'

        def outFileGraphF = new File(fnameGraphWithDirF)
        def outFileTreeF = new File(fnameTreeWithDirF)

        outFileGraphF.write(jsonGraphF)
        outFileTreeF.write(jsonTreeF)

        final Date endRun = new Date()
        TimeDuration duration = TimeCategory.minus(endRun, startRun)

        println "Duration: $duration"
    }
}
