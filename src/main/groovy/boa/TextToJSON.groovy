package boa

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import processText.WordPairsExtractor
import processText.WordPairsToJSON

class TextToJSON {

    final static float powerValue = 0.5f
    private int maxLinks = 200
    private int highFrequencyWordsSingleFile = 20
    private int highFrequencyWordsDir = 80
    private int maxNetworkLinks = 40
    final static String outDirPathString = /C:\Users\aceslh\IdeaProjects\txt2vz\boaData\json/
   //final static String textDirPathString = /C:\Users\aceslh\IdeaProjects\txt2vz\boaData\text\recurseTest/


    final static String textDirPathString = /C:\Users\aceslh\lngit\txt2vz\boaData\text\single/
    //  final static String textDirPathString = /C:\Users\aceslh\IdeaProjects\txt2vz\boaData\text\recurseTest\coffee10/


    Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wordPairData

    static void main(String[] args) {

        final Date startRun = new Date()
        TextToJSON ttj = new TextToJSON()
        int fc = ttj.recurseMulti(new File(textDirPathString),  new File(outDirPathString), false, false, 252)
        //  new TextToJSON().recurseMulti(new File(textDirPathString), new File (outDirPathString), true, true)
        //  new TextToJSON().summariseDir(new File(textDirPathString), new File (outDirPathString))

        final Date endRun = new Date()
        TimeDuration duration = TimeCategory.minus(endRun, startRun)
        println "Duration: $duration file count $fc"
    }

    int recurseMulti(File textFileRoot, File outFileForJSON, boolean summarise = true, boolean recurse = true, int maxL = 0) {
       if (maxL >0) {
           maxLinks = maxL
       }

       switch (maxL){
           case 0..150:
               highFrequencyWordsSingleFile = 10
               highFrequencyWordsDir = 40
               maxNetworkLinks = 20
               break

           case 150..250:
               highFrequencyWordsSingleFile = 20
               highFrequencyWordsDir = 80
               maxNetworkLinks = 40
               break

           case 250..450:
               highFrequencyWordsSingleFile = 40
               highFrequencyWordsDir = 160
               maxNetworkLinks = 80
               break

           default:
               highFrequencyWordsSingleFile = 20
               highFrequencyWordsDir = 80
               maxNetworkLinks = 40
               break
       }

        int fileCount = 0
        textFileRoot.eachFile { File f ->

            if (f.isDirectory()) {

                if (summarise) {
                    wordPairData = getWordPairDataFromText(f)
                    writeJSONfiles(wordPairData.first, wordPairData.second, outFileForJSON.toString(), f)
                }

                if (recurse) {
                    String outSubDirPath = outFileForJSON.toString() + File.separator + f.name

                    File subDir = new File(outSubDirPath)
                    if (!subDir.exists()) {
                        subDir.mkdir()
                        fileCount += recurseMulti(f, subDir, summarise, recurse)
                    } else {
                        println "File Already Exists "
                    }
                }
            } else if (f.isFile()) {
                fileCount++
                wordPairData = getWordPairDataFromText(f)
                writeJSONfiles(wordPairData.first, wordPairData.second, outFileForJSON.toString(), f)
            }
        }
        return fileCount
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

        writeJSONfiles(t2Cooc, stemInfo, outDirPathString, new File(textDirPathString))
    }

    private void writeJSONfiles(Map<Tuple2<String, String>, Double> t2Cooc, Map<String, Map<String, Integer>> stemInfo, String outFolderPath, File sourceFile) {

        WordPairsToJSON wptj = new WordPairsToJSON()

        String jsonTree = wptj.getJSONtree(t2Cooc, stemInfo)
        String jsonNet = wptj.getJSONnet(t2Cooc.take(maxNetworkLinks), stemInfo)

        println ""
        println "Final:"
        println "jsonNet: $jsonNet  "
        println "jsonTree: $jsonTree  "
        println "outFolderPath: $outFolderPath  "

        String folder = (sourceFile.isDirectory()) ? "_Folder_" : ""

        File outFileTree = new File(outFolderPath + File.separator + sourceFile.getName() + folder + '_tree.json')
        File outFileNet = new File(outFolderPath + File.separator + sourceFile.getName() + folder + '_network.json')

        outFileNet.write(jsonNet)
        outFileTree.write(jsonTree)
    }

    private Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> getWordPairDataFromText(File sourceTextFile) {
        WordPairsExtractor wpe
        Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wpData

        if (sourceTextFile.isDirectory()) {
            wpe = new WordPairsExtractor(powerValue, maxLinks, highFrequencyWordsDir)
            wpData = wpe.processAndMergeDirectory(sourceTextFile)
        } else if (sourceTextFile.isFile()) {
            wpe = new WordPairsExtractor(powerValue, maxLinks, highFrequencyWordsSingleFile)
            wpData = wpe.processText(sourceTextFile)
        }
        return wpData
    }
}
