package boa


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
    static String outDirPathString = /C:\Users\aceslh\IdeaProjects\txt2vz\boaData\json/
    static String textDirPathString = /C:\Users\aceslh\IdeaProjects\txt2vz\boaData\text\recurseTest/


    Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wordPairData

//  /boaData\text\coffee10/
    //  /boaData\text\secrecy10/
    //         /boaData\text\secrecy\598\ev598doc11098.txt/
    // /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\coffee14/
    //         /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\sci.crypt/
    //    /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\B/
    //     /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\G/
    //  /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOAexamples\rawText\Japan11037.txt/
    //  /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\secrecy10/
    //      /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOAexamples\rawText\QuarterlyIntel8338.txt/


    static void main(String[] args) {

        final Date startRun = new Date()
        new TextToJSON().recurseMulti(new File(textDirPathString), new File (outDirPathString), true, true)
      //  new TextToJSON().summariseDir(new File(textDirPathString), new File (outDirPathString))

        final Date endRun = new Date()
        TimeDuration duration = TimeCategory.minus(endRun, startRun)
        println "Duration: $duration"
    }

    void recurseMulti(File textFileRoot, File outFileForJSON, boolean summarise = true, boolean recurse = true) {

        textFileRoot.eachFile { File f ->

            if (f.isDirectory()) {

               if (summarise){
                   wordPairData = getWordPairDataFromText(f)
                   outputJSONfiles(wordPairData.first, wordPairData.second, outFileForJSON.toString(), f)
               }

                if (recurse) {
                    String outSubDirPath = outFileForJSON.toString() + File.separator + f.name

                    File subDir = new File(outSubDirPath)
                    if (!subDir.exists()) {
                        subDir.mkdir()
                        recurseMulti(f, subDir, summarise, recurse)
                    } else {
                        println "File Already Exists"
                    }
                }
            }
            else if (f.isFile()){
                wordPairData = getWordPairDataFromText(f)
                outputJSONfiles(wordPairData.first, wordPairData.second, outFileForJSON.toString(), f)
            }
        }
    }

    void generateSingle(boolean loadFromExistingJSONfile = false) {

        File wpTreeData = new File(outDirPathString + 'wpTreeData.json')

        if (loadFromExistingJSONfile) {
            def jsonSlurper = new JsonSlurper()
            wordPairData = jsonSlurper.parse(wpTreeData)

        } else {
            wordPairData = getWordPairDataFromText(new File(textDirPathString))
            def json = JsonOutput.toJson(wordPairData)

            //store the file containing steminfo and cooc data
            wpTreeData.write(json)
        }

        Map<Tuple2<String, String>, Double> t2Cooc = wordPairData.first
        Map<String, Map<String, Integer>> stemInfo = wordPairData.second

        outputJSONfiles(t2Cooc, stemInfo, outDirPathString, new File(textDirPathString))
    }


    private void outputJSONfiles(Map<Tuple2<String, String>, Double> t2Cooc, Map<String, Map<String, Integer>> stemInfo, String outFolder, File sourceFile) {

        WordPairsToJSON wptj = new WordPairsToJSON()

        String jsonTree = wptj.getJSONtree(t2Cooc, stemInfo)
        String jsonNet = wptj.getJSONnet(t2Cooc.take(maxNetworkLinks), stemInfo)

        println ""
        println "Final:"
        println "jsonNet: $jsonNet  "
        println "jsonTree: $jsonTree  "
        println "outFolder: $outFolder  "

        String folder = (sourceFile.isDirectory())? "_Folder_" : ""

        File outFileTree = new File(outFolder + File.separator + sourceFile.getName() + folder  + '_tree.json')
        File outFileNet =  new File(outFolder + File.separator + sourceFile.getName() + folder  + '_network.json')

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
        } else if (textLocationFile.isFile()) {
            println "File found"
            wpe = new WordPairsExtractor(powerValue, 200, 80)
            wpData = wpe.processText(textLocationFile)
        }
        return wpData
    }
}
