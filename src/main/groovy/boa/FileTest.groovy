package boa

class FileTest {


    static void main(String[] args) {
        File f = new File(/C:\Users\aceslh\IdeaProjects\txt2vz\boaData\text\recurseTest/)

        f.eachFileRecurse() { file->
            print file.getAbsolutePath()
            println " is dir " + file.isDirectory()
        }
    }
}
