import groovy.io.FileType
import groovy.json.JsonSlurper
import net.sf.ehcache.pool.Size
import org.apache.tika.Tika
import processText.WordPairsExtractor
import processText.WordPairsToJSON



class TextDirToJSON {





    static void main(String[]args){
        def m = ['small': [20,40], 'medium': [40,100], 'large': [200, 400] ]
        final float powerValue = 0.5d

        final int maxWordPairs = 40
        final int highFreqWords = 100
        String networkType = 'radial'
//def testDir = /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\testDir/
        def testDir = 'testDir'

        WordPairsExtractor wpe = new WordPairsExtractor(powerValue, maxWordPairs, highFreqWords)

        WordPairsToJSON wptj = new WordPairsToJSON()

//https://stackoverflow.com/questions/7552253/how-to-remove-special-characters-from-a-string

        def dir = new File (testDir)//("path_to_parent_dir")
        m.each{
            println  "MMMM " + it.value[0] + " " + it.value[1]

        }
        dir.eachFileRecurse (FileType.FILES) { file ->

            println "reading file $file"

            Tika t = new Tika();
            def p = t.parseToString(file)

            Map<Tuple2<String,String>,Double> wordPairAndCooc = wpe.wordPairCooc( p )

            String json =  wptj.getJSONtree(wordPairAndCooc)
            println "json $json"
            def nj = file.getName().replace('.txt', '.json')
            println "nj $nj"

            def outFile = new File('jsonOut/' + nj)
            outFile << json

        }
    }
}
