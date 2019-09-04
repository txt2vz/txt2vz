package boa

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import processText.WordPairsExtractor
import processText.WordPairsToJSON
import groovy.json.*

class GenerateJSONfromFileOrDir {

    final Date startRun = new Date()
    final float powerValue = 0.5f
    final int maxLinks = 200
    final int maxWords = 20
    final static String outDir = 'boaData/json/'


    def static textLocation =
             /boaData\text\secrecy\598\ev598doc11098.txt/

    //    /boaData\text\secrecy\590\ev590doc10903.txt/

    //         /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\sci.crypt/
            //       /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\crude/
          //           /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\coffee/
            //        /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\sugar/
            //        /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\rec.sport.hockey/
            //          /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\sci.space/
            //        /C:\Users\aceslh\Dataset\space100/
             //      /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\rec.sport.hockey/
            //        /C:\Users\aceslh\Dataset\spaceHockeyChristianBOA/
            //          /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\War Crimes Text Files_Combined/
            //       /C:\Users\aceslh\lngit\txt2vz\src\main\groovy\boa\secrecy\599/
       //                 /boaData\text\secrecy\593/
    //     /D:\boa\War Crimes Text Files_Combined/

            //           /C:\Users\aceslh\IdeaProjects\txt2vz\boaData\text\secrecy\599\ev599doc11102.txt/
            //    /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\B/
            //     /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\G/
            //       /C:\Users\aceslh\Dataset\space100/
            // //    /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\testDir/
//
      //      /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOAexamples\rawText\Japan11037.txt/

    // /D:\boa\TestData\Japan11037.txt/
    //       /D:\boa\C/
    //      /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOAexamples\rawText\QuarterlyIntel8338.txt/



    static void main(String[] args) {

        new GenerateJSONfromFileOrDir().combineDir(new File(textLocation), outDir)
    }

    void combineDir(File textLocationFile, String outDir) {

        println "outDir $outDir"
        boolean loadFromExistingJSONFile = false
        Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wordPairData

       // File f = new File(/C:\Users\aceslh\IdeaProjects\txt2vz\boaData\json/)
         File f = new File(outDir + 'wpTreeData.json')

        if (loadFromExistingJSONFile){
            def jsonSlurper = new JsonSlurper()
            wordPairData =  jsonSlurper.parse(f)

        } else
        {
            wordPairData = getWordPairDataFromText(textLocationFile)
            def json = JsonOutput.toJson(wordPairData)

            //store the file containing steminfo and cooc data
            f.write(json)
        }

        Map<Tuple2<String, String>, Double> t2Cooc = wordPairData.first

        def stemInfo = wordPairData.second
     //   def wordPairAndCoocFreqBoost = LinkBoost.linkBoost(t2Cooc,'japanes')

        def wptj = new WordPairsToJSON(stemInfo)

        String jsonTreeF = wptj.getJSONtree(t2Cooc)
        String jsonGraphF = wptj.getJSONgraph(t2Cooc)

        println ""
        println "Final:"
        println "jsonGraph $jsonGraphF  "
        println "jsonTree $jsonTreeF  "

        def fnameGraphWithDirF = outDir + textLocationFile.getName() + 'graphF.json'
        def fnameTreeWithDirF = outDir + textLocationFile.getName() + 'treeF.json'

        def outFileGraphF = new File(fnameGraphWithDirF)
        def outFileTreeF = new File(fnameTreeWithDirF)

        outFileGraphF.write(jsonGraphF)
        outFileTreeF.write(jsonTreeF)

        final Date endRun = new Date()
        TimeDuration duration = TimeCategory.minus(endRun, startRun)

        println "Duration: $duration"
    }

    private Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> getWordPairDataFromText(File textLocationFile) {
        WordPairsExtractor wpe
        Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wpData

        if (textLocationFile.isDirectory()) {
            println "DIR found"
            wpe = new WordPairsExtractor(powerValue, 200, 20)
            wpData = wpe.processDirectory(textLocationFile)

        } else if (textLocationFile.isFile()) {
            println "File found"
            wpe = new WordPairsExtractor(powerValue, 200, 80)
            wpData = wpe.processText(textLocationFile)
        }
        return wpData
    }
}
