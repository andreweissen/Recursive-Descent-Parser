### CMSC 330 Recursive Descent Parser ###

#### Overview ####

This project, created for CMSC 330 Advanced Programming Languages, is a recursive descent parser written in Java that accepts a formatted text file and translates that file's grammar into a working Swing GUI. It was submitted on July 13, 2018 and received a grade of 100%. Images of the program in action are provided in the appropriate folder, along with test text files and the Java files themselves. The grammar in question is included below in Backus–Naur form:

gui ::=
    Window STRING '(' NUMBER ',' NUMBER ')' layout widgets End '.'
layout ::=
    Layout layout_type ':'
layout_type ::=
    Flow |
    Grid '(' NUMBER ',' NUMBER [',' NUMBER ',' NUMBER] ')'
widgets ::=
    widget widgets |
    widget
widget ::=
    Button STRING ';' |
    Group radio_buttons End ';' |
    Label STRING ';' |
    Panel layout widgets End ';' |
    Textfield NUMBER ';'
radio_buttons ::=
    radio_button radio_buttons |
    radio_button
radio_button ::=
    Radio STRING ';'