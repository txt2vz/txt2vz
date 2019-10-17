package boa

import groovy.io.FileType
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import processText.WordPairsExtractor
import processText.WordPairsToJSON

class TextToJSON {

    final Date startRun = new Date()
    final float powerValue = 0.5f
    final int maxLinks = 200
    final int maxWords = 20
    final int maxNetworkLinks = 40
    static String outDirPath = 'boaData/json/'

    Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wordPairData

    static String textLocation =
//  /boaData\text\coffee10/
    /boaData\text\secrecy10/
   //         /boaData\text\secrecy\598\ev598doc11098.txt/
   // /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\coffee14/
    //         /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\sci.crypt/
    //    /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\B/
    //     /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\G/
        //  /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOAexamples\rawText\Japan11037.txt/
  //  /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\secrecy10/
    //      /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOAexamples\rawText\QuarterlyIntel8338.txt/


    static void main(String[] args) {

        println "textLocation $textLocation"
        def genJ =   new TextToJSON(new File(textLocation), outDirPath)

        genJ.generateSingle(false)
        //   genJ.generateSingle(false)
     //   genJ.generateMulti()
    }

    TextToJSON(File textLocationF, String outD) {
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
        String jsonNet = wptj.getJSONnet(t2Cooc.take(maxNetworkLinks), stemInfo)

        println ""
        println "Final:"
        println "jsonNet: $jsonNet  "
        println "jsonTree: $jsonTree  "
        println "outDir: $outDir  "

        File outFileNet = new File(outDir + textFile.getName() + '_network.json')
        File outFileTree = new File(outDir + textFile.getName() + '_tree.json')

        outFileNet.write(jsonNet)
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
