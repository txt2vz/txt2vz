package boa


import groovy.time.TimeCategory
import groovy.time.TimeDuration
import groovy.transform.CompileStatic
import processText.WordPairsExtractor
import processText.WordPairsToJSON

@CompileStatic
class TextToJSON {

    float powerValue = 0.9f
    private int maxLinks = 100
    private int highFrequencyWordsSingleFile = 20
    private int highFrequencyWordsDir = 80
    private int maxNetworkLinks = 40
    final static String outDirPathString = /C:\Users\aceslh\IdeaProjects\txt2vz\boaData\json/
    boolean useNER = true
    //final static String textDirPathString = /C:\Users\aceslh\IdeaProjects\txt2vz\boaData\text\recurseTest/


    final static String textDirPathString =
            //     /D:\boa\TestData\test/
            /C:\Users\aceslh\lngit\txt2vz\boaData\text\single/

    //  /C:\Users\aceslh\lngit\txt2vz\boaData\text\exp/
    //  final static String textDirPathString = /C:\Users\aceslh\IdeaProjects\txt2vz\boaData\text\recurseTest\coffee10/
    // /C:\Users\aceslh\IdeaProjects\txt2vz\boaData\text\exp/


    Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wordPairData

    static void main(String[] args) {

        final Date startRun = new Date()
        TextToJSON ttj = new TextToJSON(270, false)
        int fc = ttj.getJSONfromSourceText(new File(textDirPathString), new File(outDirPathString), true, true)
        //  new TextToJSON().recurseMulti(new File(textDirPathString), new File (outDirPathString), true, true)
        //  new TextToJSON().summariseDir(new File(textDirPathString), new File (outDirPathString))

        final Date endRun = new Date()
        TimeDuration duration = TimeCategory.minus(endRun, startRun)
        println "Duration: $duration file count $fc"
    }

    TextToJSON(int maxL, boolean useNER) {
        this.useNER = useNER

        switch (maxL) {
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
    }

    int getJSONfromSourceText(File file, File outputDirectoryForJSON, boolean summarise = true, boolean recurse = true) {

        int fileCount = 0
        WordPairsExtractor wpe

        assert outputDirectoryForJSON.isDirectory()

        file.eachFile { File f ->

            if (f.isDirectory()) {

                if (summarise) {
                    wpe = new WordPairsExtractor(powerValue, maxLinks, highFrequencyWordsDir, useNER)
                    wordPairData = wpe.processAndMergeDirectory(f)
                    writeJSONfiles(wordPairData.first, wordPairData.second, outputDirectoryForJSON.toString(), f)
                }

                if (recurse) {
                    String outSubDirPath = outputDirectoryForJSON.toString() + File.separator + f.name

                    File subDir = new File(outSubDirPath)
                    if (!subDir.exists()) {
                        subDir.mkdir()
                        fileCount += getJSONfromSourceText(f, subDir, summarise, recurse)
                    } else {
                        println "File Already Exists "
                    }
                }
            } else if (f.isFile()) {
                fileCount++

                wpe = new WordPairsExtractor(powerValue, maxLinks, highFrequencyWordsSingleFile, useNER)
                wordPairData = wpe.processText(f.text)

                writeJSONfiles(wordPairData.first, wordPairData.second, outputDirectoryForJSON.toString(), f)
            }
        }
        return fileCount
    }

    private void writeJSONfiles(Map<Tuple2<String, String>, Double> t2Cooc, Map<String, Map<String, Integer>> stemInfo, String outFolderPath, File sourceFile) {

        WordPairsToJSON wptj = new WordPairsToJSON()

        String jsonTree = wptj.getJSONtree(t2Cooc, stemInfo)
        String jsonNet = wptj.getJSONnet(t2Cooc.take(maxNetworkLinks), stemInfo)

        println ""
        println "File: $sourceFile"
        println "jsonNet: $jsonNet  "
        println "jsonTree: $jsonTree  "
        println "outFolderPath: $outFolderPath  "

        String folder = (sourceFile.isDirectory()) ? "_Folder_" : ""

        File outFileTree = new File(outFolderPath + File.separator + sourceFile.getName() + folder + '_tree.json')
        File outFileNet = new File(outFolderPath + File.separator + sourceFile.getName() + folder + '_network.json')

        outFileNet.write(jsonNet)
        outFileTree.write(jsonTree)
    }
}
