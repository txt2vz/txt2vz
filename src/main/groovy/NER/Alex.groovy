package NER

class Alex {

    File f = new File(/C:\Users\aceslh\OneDrive - Sheffield Hallam University\BritishOnlineArchive\TestData\GutenbergEntity_list.csv/)

    static void main(String[] args) {
        Alex alex = new Alex()
        alex.neFromCSVtoFile()
    }

    void neFromCSVtoFile() {
        Map<String, Integer> neMap = [:]

        f.splitEachLine(',') { fields ->
            String ne = fields[1]
            if (ne.charAt(0).isLetterOrDigit()) {

                final int n0 = neMap.get(ne) ?: 0
                neMap.put(ne, n0 + 1)
            }
        }

        println "neMap $neMap"

        Map<String, Integer> neMapSmall = neMap.findAll { k, v ->
            v > 1
        }
        File nerMapFileAlex = new File('nerAlex.txt')
        println "nerMapFileAlex $neMapSmall"

        nerMapFileAlex.write(neMapSmall.inspect())
    }
}
