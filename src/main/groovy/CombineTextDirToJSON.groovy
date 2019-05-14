import groovy.time.TimeCategory
import groovy.time.TimeDuration
import processText.WordPairsExtractor
import processText.WordPairsToJSON

class CombineTextDirToJSON {

    static void main(String[] args) {

        final Date startRun = new Date()
        //    def m = ['small': [30, 80], 'medium': [100, 200], 'large': [200, 400], 'huge': [400, 800]]
        //  def m = ['large': [200, 400], 'huge': [400, 800]]
        //   def m = ['huge': [400, 800]]
        def m = ['huge': [30, 100]]
        final float powerValue = 0.5f

        String networkType = 'radial'
        //   def testDir = /D:\boa\C/
        def testDir =

                //        /C:\Users\aceslh\Dataset\space100/
                ///C:\Users\aceslh\Dataset\space100/
                /C:\Users\aceslh\Dataset\space100\59871/
        //        /C:\Users\aceslh\Dataset\boa\hockey/
        //   /C:\Users\aceslh\Dataset\boa\christian/
        //        /C:\Users\aceslh\Dataset\spaceHockeyChristianBOA/
        //   /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\War Crimes Text Files_Combined/

        //        /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\B/

        //       /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\G/
        //       /C:\Users\aceslh\Dataset\space100/
        // //    /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\testDir/

        int numberOfFiles = 0
        def file = new File(testDir)

        m.each { k, v ->

            WordPairsExtractor wpe = new WordPairsExtractor(powerValue, v[0], v[1])
            Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wpData

            if (file.isDirectory()) {

                 wpData = wpe.processDirectory(file)
            } else if (file.isFile()) {

                wpData = wpe.processText(file.text)
            }

            def wordPairAndCooc = wpData.first
            def stemInfo = wpData.second

            WordPairsToJSON wptj = new WordPairsToJSON(stemInfo)
            String jsonTree = wptj.getJSONtree(wordPairAndCooc)
            String jsonGraph = wptj.getJSONgraph(wordPairAndCooc)

            //     String json = wptj.getJSONgraph(wordPairAndCooc)
            println "Size: $k jsonGraph $jsonGraph  "
            println "Size: $k jsonTree $jsonTree  "

            def fnameGraphWithDir = 'jsout2/' + k + '/' + file.getName() + 'graphDIR.json'
            def fnameTreeWithDir = 'jsout2/' + k + '/' + file.getName() + 'treeDIR.json'

            def outFileGraph = new File(fnameGraphWithDir)
            def outFileTree = new File(fnameTreeWithDir)
            outFileGraph.write(jsonGraph)
            outFileTree.write(jsonTree)

        }

        final Date endRun = new Date()
        TimeDuration duration = TimeCategory.minus(endRun, startRun)

        println "Duration: $duration"
    }
}
