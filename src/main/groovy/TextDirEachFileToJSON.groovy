import groovy.io.FileType
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import processText.WordPairsExtractor
import processText.WordPairsToJSON


class TextDirEachFileToJSON {

    static void main(String[] args) {
        final Date startRun = new Date()
        def m = ['small': [30, 80], 'medium': [100, 200], 'large': [200, 400], 'huge': [400, 800]]
        final float powerValue = 0.5f

        String networkType = 'radial'
        def testDir =// /D:\boa\C/
            //    /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\testDir/
                /D:\boa\TestData\sci.space\59873/


        //  def allFiles = /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\War Crimes Text Files_Combined/
        int numberOfFiles = 0

        def dir = new File(testDir)
        m.each { k, v ->

            WordPairsExtractor wpe = new WordPairsExtractor(powerValue, v[0], v[1])

            dir.eachFileRecurse(FileType.FILES) { file ->
                numberOfFiles++

                println "reading file $file"

                Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wpData = wpe.processDirectory(dir)
                            def wordPairAndCooc = wpData.first
                def stemInfo = wpData.second

                WordPairsToJSON wptj = new WordPairsToJSON(stemInfo)

                String json = wptj.getJSONtree(wordPairAndCooc)
                def jsonOutFileName = file.getName().replace('.txt', '.json')
                def fnameWithDir = 'jsout2/' + k + '/' + jsonOutFileName

                def outFile = new File(fnameWithDir)
                outFile.write(json)
            }
        }

        final Date endRun = new Date()
        TimeDuration duration = TimeCategory.minus(endRun, startRun)
        println "Number of files created: $numberOfFiles  Duration: $duration"
    }
}
