package fileUpload

import groovy.swing.SwingBuilder

import javax.swing.JFileChooser

swing = new SwingBuilder()
fc = new JFileChooser()


frame = swing.frame(title:'Demo') {
    menuBar {
        menu('File') {
            menuItem 'New'
            menuItem 'Open'
        }
    }
    panel {
        label 'Label 1'
        slider()
        comboBox(items:['one','two','three'])

        textField(id:'message', columns:10)
        button(text:'Print', actionPerformed: {
            println swing.message.text
        })

    }


}
frame.pack()
frame.visible = true

