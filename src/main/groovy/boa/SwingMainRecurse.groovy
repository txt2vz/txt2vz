package boa

import groovy.swing.SwingBuilder
import groovy.time.TimeCategory
import groovy.time.TimeDuration

import javax.swing.*
import java.awt.*

import static javax.swing.JFrame.EXIT_ON_CLOSE

class SwingMainRecurse {

    static void main(String[] args) {

        JLabel outFilePathL;
        JLabel textFilePathL;
        File outFolderJSON
        File sourceTextFolder
        JCheckBox cbRecurse
        JCheckBox cbSummarise

        def initialPath = System.getProperty("user.dir");
        //  File outFolder //= new String()

        ImageIcon loading = new ImageIcon(new URL("https://raw.githubusercontent.com/txt2vz/txt2vz/master/src/main/webapp/images/ajax-loader.gif"));

        def processingLabel = new JLabel("processing... ", loading, JLabel.CENTER);
        processingLabel.setVisible(false)

        def swingBuilder = new SwingBuilder()
        swingBuilder.edt {  // edt method makes sure UI is build on Event Dispatch Thread.
            lookAndFeel 'nimbus'  // Simple change in look and feel.
            frame(title: 'BOA: Generate JSON from text', size: [1000, 300],
                    show: true, locationRelativeTo: null,
                    defaultCloseOperation: EXIT_ON_CLOSE) {

                borderLayout(vgap: 5)

                outFilePathL = new JLabel("no file selected");
                textFilePathL = new JLabel(initialPath)

                panel(constraints: BorderLayout.CENTER,
                        border: compoundBorder([emptyBorder(10), titledBorder('Make selections:')])) {
                    tableLayout {
                        tr { td { label processingLabel } }

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

                                                    sourceTextFolder = fc.getSelectedFile()
                                                    textFilePathL.setText(' ' + sourceTextFolder.toString())

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

                                                    outFolderJSON = fc.getSelectedFile()
                                                    // outFolderJSON = fc.getSelectedFile().getCanonicalFile().toString() + '\\'
                                                    outFilePathL.setText(' ' + outFolderJSON.toString())
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

                                cbRecurse = checkBox(id: "cb1", text: "recurse")
                                //  checkBox  cb
                                //cb(label:  "recurse")
                            }
                            td {
                                cbSummarise = checkBox(id: "cb2", text: "summarise")
                            }
                        }
                        tr {
                            td(colspan: 2, align: 'center') {
                                label(text: '**********************************************************', foreground: Color.BLUE)
                            }
                        }

                        tr {

                            td(colspan: 2, align: 'center') {

                                button(text: 'Generate multi JSON files', background: Color.ORANGE,
                                        toolTipText: 'Each text generates its own JSON file',
                                        //  cursor: Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR),
                                        actionPerformed: {


                                            if (sourceTextFolder == null || !sourceTextFolder.isDirectory()) {
                                                JOptionPane.showMessageDialog(null, "Must select text folder for multi option.");
                                            } else if (outFolderJSON.listFiles()) {
                                                //(outFolderJSON.exists() && outFolderJSON.isDirectory() && (outFolderJSON.listFiles() as List).isEmpty()) {
                                                JOptionPane.showMessageDialog(null, "Output folder must be empty");
                                            } else {

//                                                if (cbSummarise.isSelected()) {
//                                                    JOptionPane.showMessageDialog(null, "summarise selected");
                                                //}

                                                //   edt().frame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR))

                                                processingLabel.setVisible(true)
                                                doOutside {
                                                    final Date startRun = new Date()

                                                        new TextToJSON().recurseMulti(sourceTextFolder, outFolderJSON, cbSummarise.isSelected())


                                                    final Date endRun = new Date()
                                                    TimeDuration duration = TimeCategory.minus(endRun, startRun)
                                                    println "Duration: $duration"

                                                    JOptionPane.showMessageDialog(null, "<html>Complete: check output folder <br>Duration: $duration </html>");
                                                    processingLabel.setVisible(false)

                                                }

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

}