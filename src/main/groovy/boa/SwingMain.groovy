import boa.GenerateJSON

import groovy.swing.SwingBuilder

import javax.swing.JFileChooser
import javax.swing.JLabel

import static javax.swing.JFrame.EXIT_ON_CLOSE
import java.awt.*

JLabel outFilePathL;
JLabel textFilePathL;
File outFile
File textFile

def initialPath = System.getProperty("user.dir");
String outFolder

def swingBuilder = new SwingBuilder()
swingBuilder.edt {  // edt method makes sure UI is build on Event Dispatch Thread.
    lookAndFeel 'nimbus'  // Simple change in look and feel.
    frame(title: 'Generate JSON from text', size: [700, 250],
            show: true, locationRelativeTo: null,
            defaultCloseOperation: EXIT_ON_CLOSE) {
        borderLayout(vgap: 5)

        outFilePathL = new JLabel("no file selected");
        textFilePathL = new JLabel(initialPath)

        panel(constraints: BorderLayout.CENTER,
                border: compoundBorder([emptyBorder(10), titledBorder('Make selections:')])) {
            tableLayout {

                tr {
                    td {
                        button(text: 'Select folder to save JSON files:',
                                actionPerformed: {

                                    JFileChooser fc = new JFileChooser(initialPath);
                                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                    int result = fc.showOpenDialog(null);
                                    switch (result) {
                                        case JFileChooser.APPROVE_OPTION:

                                            outFile = fc.getSelectedFile()
                                            outFolder = fc.getSelectedFile().getCanonicalFile().toString() + '\\'
                                            outFilePathL.setText(' ' + outFolder.toString())

                                            break;
                                        case JFileChooser.CANCEL_OPTION:
                                        case JFileChooser.ERROR_OPTION:
                                            break;
                                    }
                                })
                    }
                    td {

                        outFilePathL.setText(' ' + initialPath.toString())
                        label outFilePathL
                    }
                }

                tr {
                    td {

                        button(text: 'Select text file/folder  ',
                                actionPerformed: {

                                    JFileChooser fc = new JFileChooser(initialPath);
                                    fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                                    int result = fc.showOpenDialog(null);

                                    switch (result) {
                                        case JFileChooser.APPROVE_OPTION:

                                            textFile = fc.getSelectedFile()
                                            textFilePathL.setText(' ' + textFile.toString())
                                            break;
                                        case JFileChooser.CANCEL_OPTION:
                                        case JFileChooser.ERROR_OPTION:
                                            break;
                                    }
                                })
                    }
                    td {

                        textFilePathL.setText(' ' + initialPath.toString())
                        label textFilePathL
                    }
                }
                tr {

                    td {

                        label '    '
                    }

                }

                tr {
                    td {
                        button(text: 'Generate single JSON file', background: Color.ORANGE,
                                actionPerformed: {

                                    def genJ = new GenerateJSON(textFile, outFolder)
                                    genJ.generateSingle()

                                })
                    }

                    td {
                        button(text: 'Generate multi JSON files', background: Color.ORANGE, toolTipText: 'Each text generates its own JSON file',
                                actionPerformed: {

                                    def genJ = new GenerateJSON(textFile, outFolder)
                                    genJ.generateMulti()
                                })
                    }
                }
                tr {
                    td {
                        label '<html>  If selected text source is folder  <br>Files will be merged to one JSON output file </html> '
                    }
                    td {
                        label '<html>  JSON output file <br>Created for each file found in the source folder </html> '
                    }
                }
                tr {
                    td {
                        label '**********************************************************'
                    }
                    td {
                        label '**********************************************************'
                    }
                }

            }
        }
    }
}
