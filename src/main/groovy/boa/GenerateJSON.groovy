package boa

import groovy.io.FileType
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import processText.WordPairsExtractor
import processText.WordPairsToJSON

class GenerateJSON {

    final Date startRun = new Date()
    final float powerValue = 0.5f
    final int maxLinks = 200
    final int maxWords = 20
    static String outDirPath = 'boaData/json/'

    Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wordPairData

    static String textLocation =
   //         /boaData\text\secrecy\598\ev598doc11098.txt/
   // /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\coffee14/

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
        //  /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOAexamples\rawText\Japan11037.txt/
    /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\secrecy10/

    // /D:\boa\TestData\Japan11037.txt/
    //       /D:\boa\C/
    //      /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOAexamples\rawText\QuarterlyIntel8338.txt/


    static void main(String[] args) {

        println "textLocation $textLocation"
        def genJ =   new GenerateJSON(new File(textLocation), outDirPath)
      //  genJ.generateSingle()
        genJ.generateSingle(false)
        //   genJ.generateSingle(false)
     //   genJ.generateMulti()
    }

    GenerateJSON(File textLocationF, String outD) {
        textLocation = textLocationF
        outDirPath= outD
    }

    void generateSingle(boolean loadFromExistingJSONfile=false){


        File wpTreeData = new File(outDirPath + 'wpTreeData.json')

        if (loadFromExistingJSONfile) {
            def jsonSlurper = new JsonSlurper()
            wordPairData = jsonSlurper.parse(wpTreeData)

        } else {
            wordPairData = getWordPairDataFromText(new File(textLocation))
            def json = JsonOutput.toJson(wordPairData)

            //store the file containing steminfo and cooc data
            wpTreeData.write(json)
        }

        Map<Tuple2<String, String>, Double> t2Cooc = wordPairData.first
        Map<String, Map<String, Integer>> stemInfo = wordPairData.second

        outputJSONfiles(t2Cooc, stemInfo, outDirPath, new File(textLocation))

        final Date endRun = new Date()
        TimeDuration duration = TimeCategory.minus(endRun, startRun)
        println "Duration: $duration"
    }

    void generateMulti(){

        File f = new File(textLocation)
        assert f.isDirectory()
        int numberOfFiles=0

        f.eachFileRecurse (FileType.FILES) { textFile ->
            numberOfFiles++
            wordPairData = getWordPairDataFromText(textFile)
            Map<Tuple2<String, String>, Double> t2Cooc = wordPairData.first
            Map<String, Map<String, Integer>> stemInfo = wordPairData.second

            outputJSONfiles(t2Cooc, stemInfo, outDirPath, textFile)
        }

        final Date endRun = new Date()
        TimeDuration duration = TimeCategory.minus(endRun, startRun)
        println "Duration: $duration"
    }

    private void outputJSONfiles(Map<Tuple2<String, String>, Double> t2Cooc, Map<String, Map<String, Integer>> stemInfo, String outDir, File textFile) {

        WordPairsToJSON wptj = new WordPairsToJSON()

        String jsonTree = wptj.getJSONtree(t2Cooc, stemInfo)
        String jsonGraph = wptj.getJSONgraph(t2Cooc, stemInfo)

        println ""
        println "Final:"
        println "jsonGraph: $jsonGraph  "
        println "jsonTree: $jsonTree  "
        println "outDir: $outDir  "
      //  if (outDir == null){
       //     outDir = new String()
       // }

        File outFileGraph = new File(outDir + textFile.getName() + '_graph.json')
        File outFileTree = new File(outDir + textFile.getName() + '_tree.json')

        outFileGraph.write(jsonGraph)
        outFileTree.write(jsonTree)
    }

    private Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> getWordPairDataFromText(File textLocationFile) {
        WordPairsExtractor wpe
        Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wpData

        if (textLocationFile.isDirectory()) {
            println "DIR found"
            wpe = new WordPairsExtractor(powerValue, 200, 20)
            wpData = wpe.processAndMergeDirectory(textLocationFile)
        } else
        if (textLocationFile.isFile()) {
            println "File found"
            wpe = new WordPairsExtractor(powerValue, 200, 80)
            wpData = wpe.processText(textLocationFile)
        }
        return wpData
    }
}
