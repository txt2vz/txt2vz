package boa

import groovy.swing.SwingBuilder

import javax.swing.*
import java.awt.*

import static java.awt.Frame.getFrames
import static javax.swing.JFrame.EXIT_ON_CLOSE

class SwingMain2 {

    public static void main(String[] args) {

        JLabel outFilePathL;
        JLabel textFilePathL;
        File outFile
        File textFile

        def initialPath = System.getProperty("user.dir");
        String outFolder = new String()

        def swing = new SwingBuilder()

        def frame = swing.frame(title: 'BOA: Generate JSON from text', size: [1000, 300],
                show: true, locationRelativeTo: null,
                cursor: Cursor.DEFAULT_CURSOR,
                defaultCloseOperation: EXIT_ON_CLOSE)

//        borderLayout(vgap: 5)

        outFilePathL = new JLabel("no file selected");
        textFilePathL = new JLabel(initialPath)

//        frame.setCursor()

        swing.edt {
            frame
            panel {//(constraints: BorderLayout.CENTER,
                   // border: compoundBorder([emptyBorder(10), titledBorder('Make selections:')])) {
                tableLayout {

                    tr {
                        td {

                            button(text: 'Select text file/folder  ',
                                    toolTipText: 'select the source text file(s)',
                                    actionPerformed: {

                                        JFileChooser fc = new JFileChooser(initialPath);
                                        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                                        fc.setApproveButtonText('Select File or Folder')
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
                            button(text: 'Select folder to save JSON files:',
                                    toolTipText: 'select folder to store the generated json file(s) ',
                                    actionPerformed: {

                                        JFileChooser fc = new JFileChooser(initialPath);
                                        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                        fc.setApproveButtonText('Select Folder')
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

                            label '    '
                        }

                    }
                    tr {
                        td {
                            label(text: '**********************************************************', foreground: Color.BLUE)
                        }
                        td {
                            label(text: '**********************************************************', foreground: Color.BLUE)
                        }
                    }

                    tr {
                        td {
                            button(text: 'Generate single JSON file', background: Color.ORANGE,
                                    toolTipText: 'Single JSON file output - merge if multiple input files',
                                    actionPerformed: {

                                        def genJ = new GenerateJSON(textFile, outFolder)
                                        genJ.generateSingle()
                                        JOptionPane.showMessageDialog(null, "Complete: check output folder.");

                                    })
                        }

                        td {
                            button(text: 'Generate multi JSON files', background: Color.ORANGE,
                                    toolTipText: 'Each text generates its own JSON file',
                                    actionPerformed: {

                                        if (!textFile.isDirectory()) {
                                            JOptionPane.showMessageDialog(null, "Must select text folder for multi option.");
                                        } else {


                                            swingBuilder.curs
                                            //this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                            getFrames().setProperty('cursor').

                                                    //   edt().frame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR))

                                                    def genJ = new GenerateJSON(textFile, outFolder)
                                            genJ.generateMulti()
                                            JOptionPane.showMessageDialog(null, "Complete: check output folder.");


                                            //  edt().frame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))

                                        }
                                    })
                        }
                    }
                    tr {
                        td {
                            label '<html>  <br>Multiple files will be merged to one JSON output file </html> '
                        }
                        td {
                            label '<html>  <br>JSON output file created for each file found in the source folder </html> '
                        }
                    }
                    tr {
                        td {
                            label(text: '**********************************************************', foreground: Color.BLUE)
                        }
                        td {
                            label(text: '**********************************************************', foreground: Color.BLUE)
                        }
                    }

                }
            }
        }
    }
  }
//}