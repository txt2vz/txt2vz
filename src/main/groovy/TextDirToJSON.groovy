import groovy.io.FileType
import groovy.json.JsonSlurper
import processText.WordPairsExtractor
import processText.WordPairsToJSON

class TextDirToJSON {

    static void main(String[]args){
        def jsonSlurper = new JsonSlurper()

        def jfile =  new File('OfficersDiary3210.json')

     //   def jText = jsonSlurper.parseText(jfile.text)

        def outFile = new File ('officersDiary3210.txt')
        def list = []
        final float powerValue = 0.5d
        final int maxWordPairs = 30
        final int highFreqWords = 100
        String networkType = 'radial'
//def testDir = /C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\holocaust\testDir/
        def testDir = 'textFiles'

        WordPairsExtractor wpe = new WordPairsExtractor(powerValue, maxWordPairs, highFreqWords)

        //Map<Tuple2<String,String>,Double> wordPairAndCooc = wpe.wordPairCooc( text)

      //  println "in plain text wordpairandcooc " + wordPairAndCooc.take(20)

      //  WordPairsToJSON wptj = new WordPairsToJSON()

      //  String json = (networkType == 'forceNet') ? wptj.getJSONgraph(wordPairAndCooc) :   wptj.getJSONtree(wordPairAndCooc)
       // println "json: $json"
//https://stackoverflow.com/questions/7552253/how-to-remove-special-characters-from-a-string
        def dir = new File (testDir)//("path_to_parent_dir")
        dir.eachFileRecurse (FileType.FILES) { file ->
            list << file
            println "reading file $file"

            Map<Tuple2<String,String>,Double> wordPairAndCooc = wpe.wordPairCooc( file.text)

              println "in plain text wordpairandcooc " + wordPairAndCooc.take(20)

        }
        println list.size() + " list $list "

    }


}
