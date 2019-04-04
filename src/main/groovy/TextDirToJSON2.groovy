import processText.WordPairsExtractor
import processText.WordPairsToJSON

class TextDirToJSON2 {


    static void main(String[] args) {

        final Date startRun = new Date()
        def m = ['small': [30, 80], 'medium': [100, 200], 'large': [200, 400], 'huge': [400, 800]]
        final float powerValue = 0.5f

        String networkType = 'radial'
        def testDir =
             //   /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\testDir/
          /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\B/

      //  /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\G/
        //  def allFiles = /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\War Crimes Text Files_Combined/
        int numberOfFiles = 0
        def dir = new File(testDir)
        WordPairsExtractor wpe = new WordPairsExtractor(powerValue, 400, 800)
        //Map<Tuple2<String, String>, Double> wordPairAndCooc  = wpe.wordPairCooc(dir)

        Tuple2< Map<Tuple2<String, String>, Double>,Map<String, Map<String, Integer>> > wpData = wpe.wordPairCooc(dir)

        def stemInfo = wpData.second
        def wordPairAndCooc = wpData.first

       // println "zzz " + wordPairAndCooc.take(30)
        println "yyyy " + wpData


        WordPairsToJSON wptj = new WordPairsToJSON(stemInfo)
        String json = wptj.getJSONtree(wordPairAndCooc)
        println "json $json"


        def outFile = new File('B.json')
        outFile.write(json)

    }
}
