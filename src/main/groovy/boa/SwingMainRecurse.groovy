package boa

import groovy.swing.SwingBuilder
import groovy.time.TimeCategory
import groovy.time.TimeDuration

import javax.swing.*
import javax.swing.border.TitledBorder
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
        JCheckBox cbNER
        JSlider numberOfLinksSlider

        String initialPath = System.getProperty("user.dir");
        ImageIcon loading = new ImageIcon(new URL("https://raw.githubusercontent.com/txt2vz/txt2vz/master/src/main/webapp/images/ajax-loader.gif"));

        JLabel processingLabel = new JLabel('<html><i>processing... <i></html>', loading, JLabel.CENTER);
        processingLabel.setForeground(Color.red)
        processingLabel.setVisible(false)

        SwingBuilder swingBuilder = new SwingBuilder()
        swingBuilder.edt {  // edt method makes sure UI is build on Event Dispatch Thread.
            lookAndFeel 'nimbus'  // Simple change in look and feel.
            frame(title: 'BOA: Generate JSON required for visualisation from text files', size: [1000, 400],
                    show: true, locationRelativeTo: null,
                    defaultCloseOperation: EXIT_ON_CLOSE) {

                borderLayout(vgap: 5)

                outFilePathL = new JLabel("no file selected");
                textFilePathL = new JLabel(initialPath)

                panel(constraints: BorderLayout.CENTER,
                 //       border: compoundBorder([emptyBorder(10), titledBorder('Make selections:')])) {
                    border: compoundBorder([emptyBorder(10)])) {
                    tableLayout {//(background: Color.CYAN) {
                        tr { td { label processingLabel } }

                        tr {
                            td {

                                button(text: 'Select folder containing source text files:',
                                        toolTipText: 'source text files location',
                                        actionPerformed: {

                                            JFileChooser fc = new JFileChooser(initialPath);
                                            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                            fc.setApproveButtonText('Select Folder')
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

                            td(colspan: 2, align: 'left')  {

                                textFilePathL.setText(' ' + initialPath.toString())
                                label textFilePathL
                            }

                        }
                        tr {
                            td {
                                button(text: 'Select folder to save JSON files:',
                                        toolTipText: 'Select folder to store the generated json files ',
                                        actionPerformed: {

                                            JFileChooser fc = new JFileChooser(initialPath);
                                            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                            fc.setApproveButtonText('Select Folder')
                                            int result = fc.showOpenDialog(null);
                                            switch (result) {
                                                case JFileChooser.APPROVE_OPTION:

                                                    outFolderJSON = fc.getSelectedFile()
                                                    outFilePathL.setText(' ' + outFolderJSON.toString())
                                                    break;
                                                case JFileChooser.CANCEL_OPTION:
                                                case JFileChooser.ERROR_OPTION:
                                                    break;
                                            }
                                        })
                            }
                            td(colspan: 2, align: 'left') {

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

                                cbRecurse = checkBox(id: "cb1", text: "Recurse")
                                cbRecurse.setSelected(true)


                            }
                            td {
                                cbSummarise = checkBox(id: "cb2", text: "Summarise")
                                cbSummarise.setSelected(true)

                            }
                            td {
                                  cbNER = checkBox(id: "ner", text: "NER")
                                  cbNER.setSelected(true)

                            }
                        }

                        tr {
                            td {
                                label '<html>Process all sub-folders </html> '
                            }
                            td {

                                label '<html>Create a JSON file for entire sub-folder</html> '
                            }
                            td {

                                label '<html>Use Named Entity Recognition (slower)</html> '
                            }
                        }
                        tr {
                            td(colspan: 3, align: 'center') {
                                label(text: '**************************************************************************************************************************************************************', foreground: Color.BLUE)
                            }
                        }
                        tr {
                            td(colspan: 3, align: 'center') {

                                // Add positions label in the slider
                                Hashtable sliderLabels = new Hashtable()
                                sliderLabels.put(10, new JLabel("small"))
                                sliderLabels.put(200, new JLabel("medium"))
                                sliderLabels.put(390, new JLabel("large"))

                                numberOfLinksSlider = slider(new JSlider(JSlider.HORIZONTAL, 2, 400, 200))

                                numberOfLinksSlider.setLabelTable(sliderLabels)
                                numberOfLinksSlider.setPaintLabels(true)
                                numberOfLinksSlider.setBorder (BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Set visualisation size/complexity", TitledBorder.CENTER, TitledBorder.TOP))

                           //     numberOfLinksSlider.setBorder (BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(2, 2,
                             //           2, 2, Color.gray), "Set visualisation size/complexity", TitledBorder.TOP, TitledBorder.TOP))
                                numberOfLinksSlider.setPreferredSize(new Dimension(400, 80))
                                numberOfLinksSlider.setToolTipText('adjust to alter size/complexity of visualisation. Actual number of links will often be less, especially when using tree based visualisation due to tree pruning.')
                            }
                        }
                        tr {
                            td(colspan: 3, align: 'center') {
                                label(text: '**************************************************************************************************************************************************************', foreground: Color.BLUE)
                            }
                        }

                        tr {

                            td(colspan: 3, align: 'center') {

                                button(text: 'Generate JSON files', background: Color.ORANGE,
                                        toolTipText: 'Use text files in source folder to generate JSON files ready for visualisation',
                                        //  cursor: Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR),
                                        actionPerformed: {


                                            if (sourceTextFolder == null || !sourceTextFolder.isDirectory()) {
                                                JOptionPane.showMessageDialog(null, "Must select text folder");
                                            } else if (outFolderJSON.listFiles()) {
                                                //(outFolderJSON.exists() && outFolderJSON.isDirectory() && (outFolderJSON.listFiles() as List).isEmpty()) {
                                                JOptionPane.showMessageDialog(null, "Output folder must be empty");
                                            } else {

                                                //   edt().frame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR))

                                                processingLabel.setVisible(true)
                                                doOutside {
                                                    final Date startRun = new Date()

                                                    final int maxLinks = numberOfLinksSlider.value
                                                    TextToJSON ttj = new TextToJSON(maxLinks, cbNER.isSelected())
                                                    println "maxLinks $maxLinks"
                                                    final int fileCount = ttj.getJSONfromSourceText(sourceTextFolder, outFolderJSON, cbSummarise.isSelected(), cbRecurse.isSelected())
                                                    final Date endRun = new Date()
                                                    TimeDuration duration = TimeCategory.minus(endRun, startRun)
                                                    println "Duration: $duration"
                                                    processingLabel.setVisible(false)
                                                    JOptionPane.showMessageDialog(null, "<html><b>Processing Complete</b><br>Check output folder: $outFolderJSON <br>Duration: $duration  <br>Text files processed: $fileCount</html>");
                                                }
                                            }
                                        })
                            }
                        }
                        tr {
                            td(colspan: 3, align: 'center') {
                                label(text: '**************************************************************************************************************************************************************', foreground: Color.BLUE)
                            }
                        }
                    }
                }
            }
        }
    }
}