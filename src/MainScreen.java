import javax.security.auth.callback.LanguageCallback;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainScreen {
    private JPanel mainPanel;
    private JTextPane outputTextPane;
    private JTextField commandTextField;
    private JButton enterButton;
    private JTextArea alphabetTextArea;
    private JTable languageTable;
    private JPanel IOPanel;
    private JPanel dataPanel;
    private JLabel alphabetLabel;
    private JLabel languageLabel;
    private JTextArea languagesTextArea;

    //Local variables
    private enum Mode {TYPING, SHORTCUT};
    private Mode mode = Mode.TYPING;
    private final SimpleAttributeSet attrs = new SimpleAttributeSet();

    private final ArrayList<String> history = new ArrayList<>();
    private int historyIndex = 0;

    private void enterPressed() {
        String rawInput= commandTextField.getText();

        normalLog(">>> " + rawInput);

        if(rawInput.equals("universe")) {
            normalLog(" U = " + Lenguajes.getUniverso().toString());
        } else if(rawInput.equals("exit")) {
            System.exit(0);
        } else if(rawInput.equals("clear")) {
            outputTextPane.setText("");
        } else if(rawInput.matches("alphabet\\s*=\\s*\\{([a-zA-Z0-9]{1}\\s*,\\s*)*[^,\\s]\\}")) {
            //TODO Valid alphabet
            if(Lenguajes.getAlfabeto().isEmpty()) {
                String alphabet = rawInput.split("\\s*=\\s*")[1].trim();
                alphabet = alphabet.substring(1, alphabet.length() - 1);
                for(String s : alphabet.split("\\s*,\\s*"))
                    Lenguajes.agregarSimbolo(s.charAt(0));
                alphabetTextArea.setText(rawInput);
            } else {
                errorLog("The alphabet is already set");
            }
        }
        else if(rawInput.matches("[a-zA-Z][0-9]*\\s*=\\s*\\{(.*,*)\\}")) {
            String[] splittedInput = rawInput.split("\\s*=\\s*");
            String name = splittedInput[0].trim();
            String value = splittedInput[1].trim();
            String[] values = value.substring(1, value.length() - 1).split("\\s*,\\s*");

            if(Lenguajes.esValido(values)) {
                Lenguajes.agregaLenguaje(name, new Lenguaje(values));
                updateLanguages();
                successLog(" Language added succesfully");
            } else {
                errorLog(" Invalid language");
            }
        } else if(rawInput.matches("[a-zA-Z][0-9]*\\s*=\\s*[a-zA-Z0-9\\sΔ\\-\\*∪∩'\\{,\\}\\(\\)]+")) {
            //TODO Valid assignation with one or more operation
            String[] splittedInput = rawInput.split("\\s*=\\s*");
            String name = splittedInput[0].trim();
            Lenguaje value = Evaluator.evaluate(splittedInput[1].trim());

            //TODO I need to figure out how to validate inline laguages

            Lenguajes.agregaLenguaje(name, value);
            updateLanguages();

            successLog(" Language added succesfully: " + value.toString());
        } else if(rawInput.matches("[a-zA-Z0-9\\s∪\\-\\*\\'∩Δ\\{\\},]+")) {
            //TODO Valid operation with inline languages
            successLog(" " + Evaluator.evaluate(rawInput).toString());
        }

        commandTextField.setText("");
    }

    public void normalLog(String s) {
        StyleConstants.setForeground(attrs, Color.BLACK);
        try {
            outputTextPane.getStyledDocument().insertString(
                    outputTextPane.getStyledDocument().getLength(), s + '\n', attrs);
        } catch (BadLocationException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void errorLog(String s) {
        StyleConstants.setForeground(attrs, Color.RED);
        try {
            outputTextPane.getStyledDocument().insertString(
                    outputTextPane.getStyledDocument().getLength(), s + '\n', attrs);
        } catch (BadLocationException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void successLog(String s) {
        StyleConstants.setForeground(attrs, new Color(50, 168, 82));
        try {
            outputTextPane.getStyledDocument().insertString(
                    outputTextPane.getStyledDocument().getLength(), s + '\n', attrs);
        } catch (BadLocationException ex) {
            Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public MainScreen() {

        enterButton.addActionListener(e -> enterPressed());
        commandTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                int key = e.getKeyCode();

                if(mode == Mode.SHORTCUT) {
                    StringBuilder buf = new StringBuilder(commandTextField.getText());
                    if(key == 85) {
                        //union
                        buf.append("∪");
                    } else if (key == 73) {
                        //interseccion
                        buf.append("∩");
                    } else if (key == 68) {
                        //diferencia
                        buf.append("-");
                    } else if (key == 83) {
                        //diferencia simetrica
                        buf.append("Δ");
                    } else if (key == 80) {
                        //producto
                        buf.append("*");
                    }else if (key == 67) {
                        //complemento
                        buf.append("'");
                    }
                    commandTextField.setText(buf.toString());
                    mode = Mode.TYPING;
                }

                if(key == 17) {
                    mode = Mode.SHORTCUT;
                } else if (key == 10) {
                    enterPressed();
                } else if(key == 38) {
                    //TODO up key pressed

                } else if(key == 40) {
                    //TODO down key pressed

                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame ms = new JFrame();
        ms.setContentPane(new MainScreen().mainPanel);
        ms.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ms.setSize( 800, 600);
        ms.setLocationRelativeTo(null);
        ms.pack();
        ms.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private void updateLanguages() {
        String[] keys = Lenguajes.getLenguajes().keySet().toArray(new String[0]);
        languagesTextArea.setText("");

        for(String k : keys)
            languagesTextArea.append(k + " = " + Lenguajes.getOne(k).toString() + "\n");
    }
}
