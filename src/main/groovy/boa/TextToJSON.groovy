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

  //  File textDirFile
  //  File outDirJSONFile

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

        println "textDirPathString $textDirPathString"
        def genJ = new TextToJSON(new File(textDirPathString), new File (outDirPathString))
        genJ.generateMulti()
    }

   // TextToJSON(File textLocationF, File outD) {
        //textDirPathString = textLocationF
        //outDirPathString = outD
  //  }

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

        final Date endRun = new Date()
        TimeDuration duration = TimeCategory.minus(endRun, startRun)
        println "Duration: $duration"
    }

    void recurseMulti(File textFileRoot, File outPathJSON) {

        textFileRoot.eachFile { File f ->

            if (f.isDirectory()) {

                String outP = outPathJSON.toString() + File.separator + f.name

                File fd = new File(outP)
                if (!fd.exists()) {
                    fd.mkdir()
                    recurseMulti(f, fd)
                } else {
                    println "File Already Exists"
                }
            }
            else if (f.isFile()){
                wordPairData = getWordPairDataFromText(f)
                Map<Tuple2<String, String>, Double> t2Cooc = wordPairData.first
                Map<String, Map<String, Integer>> stemInfo = wordPairData.second
                outputJSONfiles(t2Cooc, stemInfo, outPathJSON.toString(), f)
            }
        }
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

      //  File outFileNet = new File(outDir + textFile.getName() + '_network.json')
        File outFileTree = new File(outDir + File.separator + textFile.getName() + '_tree.json')

     //   outFileNet.write(jsonNet)
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
