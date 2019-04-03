import groovy.io.FileType
import groovy.json.JsonSlurper
import net.sf.ehcache.pool.Size
import org.apache.tika.Tika
import processText.WordPairsExtractor
import processText.WordPairsToJSON


class TextDirToJSON {

    static void main(String[] args) {
        def m = ['small': [30, 80], 'medium': [100, 200], 'large': [200, 400], 'huge': [400, 800]]
        final float powerValue = 0.5f

        final int maxWordPairs = 100
        final int highFreqWords = 200
        String networkType = 'radial'
//def testDir = /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\testDir/
        def testDir = 'testDir'

    //    WordPairsToJSON wptj = new WordPairsToJSON()

//https://stackoverflow.com/questions/7552253/how-to-remove-special-characters-from-a-string

        def dir = new File(testDir)//("path_to_parent_dir")
        m.each {k,v ->
            println "MMMM " + v[0] + " " + v[1]
         //   WordPairsExtractor wpe = new WordPairsExtractor(powerValue, maxWordPairs, highFreqWords)
            WordPairsExtractor wpe = new WordPairsExtractor(powerValue, v[0], v[1])

            dir.eachFileRecurse(FileType.FILES) { file ->

                println "reading file $file"

                Tika t = new Tika();
                def fileText = t.parseToString(file)

                Map<Tuple2<String, String>, Double> wordPairAndCooc = wpe.wordPairCooc(fileText)
                WordPairsToJSON wptj = new WordPairsToJSON()

                String json = wptj.getJSONtree(wordPairAndCooc)
                println "json $json"
                def nj = file.getName().replace('.txt', '.json')
                println "nj $nj"
                def fname = 'jsonOut/' + k +'/' +  nj
                println "fname $fname"

                def outFile = new File(fname)
                outFile.write(json)

            }
        }
    }
}
