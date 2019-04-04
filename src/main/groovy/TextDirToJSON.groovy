import groovy.io.FileType
import groovy.json.JsonSlurper
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import net.sf.ehcache.pool.Size
import org.apache.tika.Tika
import processText.WordPairsExtractor
import processText.WordPairsToJSON


class TextDirToJSON {

    static void main(String[] args) {
        final Date startRun = new Date()
        def m = ['small': [30, 80], 'medium': [100, 200], 'large': [200, 400], 'huge': [400, 800]]
        final float powerValue = 0.5f

        String networkType = 'radial'
        def testDir = /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\testDir/
      //  def allFiles = /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\War Crimes Text Files_Combined/
int numberOfFiles = 0
        def dir = new File(testDir)
        m.each { k, v ->

            WordPairsExtractor wpe = new WordPairsExtractor(powerValue, v[0], v[1])

            dir.eachFileRecurse(FileType.FILES) { file ->
                numberOfFiles++

                println "reading file $file"

                Tika t = new Tika();
                def fileText = t.parseToString(file)

                Map<Tuple2<String, String>, Double> wordPairAndCooc = wpe.wordPairCooc(fileText)
                WordPairsToJSON wptj = new WordPairsToJSON()

                String json = wptj.getJSONtree(wordPairAndCooc)
                def jsonOutFileName = file.getName().replace('.txt', '.json')
                def fnameWithDir = 'jsonOut/' + k + '/' + jsonOutFileName

                def outFile = new File(fnameWithDir)
                outFile.write(json)
            }
        }

        final Date endRun = new Date()
        TimeDuration duration = TimeCategory.minus(endRun, startRun)
        println "Number of files created: $numberOfFiles  Duration: $duration"
    }
}
