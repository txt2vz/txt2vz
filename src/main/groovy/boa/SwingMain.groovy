package boa

import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.swing.JFileChooser;

class SwingMain {

    public static void main(String[] args) {

        String outFolder
        def count = 0
        new SwingBuilder().edt {
            frame(title: 'Frame', size: [300, 300], show: true) {
                borderLayout()
                textlabel = label(text: 'Click the button!', constraints: BL.NORTH)
                button(text: 'select folder to save files',
                        actionPerformed: {
                            def initialPath = System.getProperty("user.dir");
                            JFileChooser fc = new JFileChooser(initialPath);
// fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                            int result = fc.showOpenDialog(null);
                            switch (result) {
                                case JFileChooser.APPROVE_OPTION:
                                //
                                    //    outFolder = fc.getSelectedFile();

                                    def path = fc.getCurrentDirectory().getAbsolutePath();
                                def pc = fc.getSelectedFile().getCanonicalFile()
                                outFolder = pc.toString() + '\\'
                                println "pc  $pc outfolder $outFolder"
                                   // println "outfolder path=" + path + "\noutFolder name=" + outFolder.toString();


                                    break;
                                case JFileChooser.CANCEL_OPTION:
                                case JFileChooser.ERROR_OPTION:
                                    break;
                            }

                        }, constraints: BL.NORTH)

                button(text: 'select files for JSON extract',
                        actionPerformed: {
                            def initialPath = System.getProperty("user.dir");
                            JFileChooser fc = new JFileChooser(initialPath);
// fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                            int result = fc.showOpenDialog(null);
                            switch (result) {
                                case JFileChooser.APPROVE_OPTION:
                                    File file = fc.getSelectedFile();

                                    def path = fc.getCurrentDirectory().getAbsolutePath();
                                    println "path=" + path + "\nfile name=" + file.toString();

                                    def ctdtj = new CombineTextDirToJSON_t2_old()
                                    ctdtj.combineDir(file, outFolder)

                                    break;
                                case JFileChooser.CANCEL_OPTION:
                                case JFileChooser.ERROR_OPTION:
                                    break;
                            }

                        }, constraints: BL.SOUTH)

            }
        }
    }
}
