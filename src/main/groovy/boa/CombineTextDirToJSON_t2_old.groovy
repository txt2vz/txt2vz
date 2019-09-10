package boa

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import processText.WordPairsExtractor
import processText.WordPairsToJSON

class CombineTextDirToJSON_t2_old {

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

                /boaData\text\secrecy\593/
                //   /boaData\text\secrecy\598\ev598doc11098.txt/

//    /boaData\text\secrecy\590\ev590doc10903.txt/

     //           /C:\Users\aceslh\IdeaProjects\txt2vz\boaData\text\secrecy\599\ev599doc11102.txt/


        //    /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\B/
//


        //     /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\G/
        //       /C:\Users\aceslh\Dataset\space100/
        // //    /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\testDir/
//
    //         /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOAexamples\rawText\Japan11037.txt/


        // /D:\boa\TestData\Japan11037.txt/
        //       /D:\boa\C/
     //      /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOAexamples\rawText\QuarterlyIntel8338.txt/

        //    /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\BOA\rawText\4363.txt/



    static void main(String[] args) {
        File textFile_s = new File(textLocation)
        def ctdtj =  new CombineTextDirToJSON_t2_old()
             ctdtj.combineDir(textFile_s, outDir)
    }

    void combineDir(File textFile_s, String outDir){

        WordPairsExtractor wpe
        Tuple3<Map<Tuple2<String, String>, Double>, Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wpData

        if (textFile_s.isDirectory()) {
            println "DIR found"
            wpe = new WordPairsExtractor(powerValue, 200, 20)
            wpData = wpe.processAndMergeDirectory(textFile_s)

        } else if (textFile_s.isFile()) {
            println "FIle found"
            wpe = new WordPairsExtractor(powerValue, 900, 80)
            wpData = wpe.processText(textFile_s)
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
    //    String outDirPath =// 'boaData/json/'

  //      /C:\Users\aceslh\IdeaProjects\txt2vz\boaData\j2\ /

        def fnameGraphWithDir = outDir + textFile_s.getName() + 'graph.json'
        def fnameTreeWithDir = outDir + textFile_s.getName() + 'tree.json'
        def fnameGraphWithDirF = outDir + textFile_s.getName() + 'graphF.json'
        def fnameTreeWithDirF = outDir + textFile_s.getName() + 'treeF.json'

        def fnameGraphWithDirFs = outDir + textFile_s.getName() + 'graphFs.json'
        def fnameTreeWithDirFs = outDir + textFile_s.getName() + 'treeFs.json'

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
