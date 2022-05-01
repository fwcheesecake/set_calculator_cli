import exceptions.InvalidAlphabetException;
import exceptions.InvalidLanguageException;
import exceptions.InvalidSyntaxException;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainScreen {
    private JPanel mainPanel;
    private JTextPane outputTextPane;
    private JTextField commandTextField;
    private JButton enterButton;
    private JPanel IOPanel;
    private JPanel dataPanel;
    private JLabel alphabetLabel;
    private JLabel languageLabel;
    private JTextPane languageTextPane;
    private JTextPane alphabetTextPane;

    private enum Mode {TYPING, SHORTCUT}
    private Mode mode = Mode.TYPING;
    private final SimpleAttributeSet attrs = new SimpleAttributeSet();

    private final Stack<String> prev = new Stack<>();
    private final Stack<String> next = new Stack<>();

    private void enterPressed() {
        //Reads the input
        String rawInput = commandTextField.getText();

        //Prints the command in the screen
        normalLog(">>> " + rawInput);

        try {
            if (rawInput.equals("universe")) { // If input is "universe"
                normalLog(" U = " + Languages.getUniverse().print()); // Just print the universe
            } else if (rawInput.equals("exit")) { // If input is "exit"
                System.exit(0); // Kill the program
            } else if (rawInput.equals("clear")) { // If input is "clear"
                outputTextPane.setText(""); // Clear the screen
            } else if(rawInput.matches("^alphabet.*$")) { // Check if it's an alphabet initialization
                if (rawInput.matches("^alphabet\\s*=\\s*\\{(.{1}\\s*,{1}\\s*)*.}$")) { // Check if the syntax is correct
                    if (Languages.getAlphabet().isEmpty()) { // If the alphabet it's not set
                        String alphabet = rawInput.split("\\s*=\\s*")[1].trim(); // Then split the input and get only the part where the value is
                        alphabet = alphabet.substring(1, alphabet.length() - 1);  // Remove the curly braces

                        String[] alphabetChars = alphabet.split("\\s*,\\s*"); // Then split it to get individual values
                        // And then add every single character to the alphabet
                        for (String s : alphabetChars)
                            Languages.addSymbol(s.charAt(0));

                        alphabetTextPane.setText(rawInput); // Display the alphabet value in the corresponding panel
                        successLog(" Alphabet initialized successfully"); // Display a success message
                    } else {
                        // If the alphabet was already set, it'll throw an exception
                        throw new InvalidAlphabetException(" The alphabet is already set");
                    }
                } else {
                    // If the alphabet it's not valid e.g. contains an element with 2 or more characters, it'll throw an exception too
                    throw new InvalidAlphabetException(" The alphabet is not valid");
                }
            } else if(rawInput.matches("^([a-zA-Z_$]\\w*)\\s*=\\s*[^∪∩×\\-Δ'+*]+$")) { // Check if the expression does not contain an operator
                if (rawInput.matches("^([a-zA-Z_$]\\w*)\\s*=\\s*\\{(.+\\s*,{1}\\s*)*.+}$")) {
                    String[] splitInput = rawInput.split("\\s*=\\s*");
                    String name = splitInput[0];
                    String value = splitInput[1];
                    String[] values = value.substring(1, value.length() - 1).split("\\s*,\\s*");

                    if (Languages.getAlphabet().isEmpty()) {
                        throw new InvalidLanguageException(" The alphabet is empty. Set it with alphabet = {[values]}");
                    } else if (Languages.isValid(values)) {
                        Languages.addLanguage(name, new Language(values));
                        updateLanguages();
                        successLog(" Language added successfully");
                    } else {
                        throw new InvalidLanguageException(" Invalid language");
                    }
                } else {
                    throw new InvalidSyntaxException(" Syntax error");
                }
            } else if (rawInput.matches("^([a-zA-Z_$]\\w*)\\s*=\\s*([a-zA-Z0-9_$Δ\\-*×+∪∩'{,}()]\\s*)+$")) {
                String[] splitInput = rawInput.split("\\s*=\\s*");
                String name = splitInput[0].trim();
                Language value = Evaluator.evaluate(splitInput[1].trim());

                Languages.addLanguage(name, value);
                updateLanguages();
                //([a-zA-Z_$]\w*)
                successLog(" Language added successfully: " + value.print());
            } else if (rawInput.matches("[a-zA-Z0-9_$\\s∪\\-*+'×∩Δ{},()]+")) {
                successLog(" " + Evaluator.evaluate(rawInput).print());
            } else {
                throw new InvalidSyntaxException(" Syntax error");
            }
        } catch (InvalidLanguageException | InvalidSyntaxException | InvalidAlphabetException e) {
            errorLog(e.getMessage());
        }

        if(!next.isEmpty()) {
            while (next.size() > 1) {
                prev.push(next.pop());
            }
            next.pop();
        }
        prev.push(commandTextField.getText());
        commandTextField.setText("");
    }
    private void updateLanguages() throws InvalidLanguageException {
        String[] keys = Languages.getLanguages().keySet().toArray(new String[0]);
        StringBuilder buf = new StringBuilder();

        for(String k : keys)
            buf.append(k).append(" = ").append(Languages.getOne(k).print()).append("\n");

        languageTextPane.setText(buf.toString());
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
                    int caretPosition = commandTextField.getCaretPosition();
                    if(key == 85) {
                        //union
                        buf.insert(caretPosition, "∪");
                    } else if (key == 73) {
                        //intersection
                        buf.insert(caretPosition, "∩");
                    } else if (key == 68) {
                        //difference
                        buf.insert(caretPosition, "-");
                    } else if (key == 83) {
                        //symmetrical difference
                        buf.insert(caretPosition, "Δ");
                    } else if (key == 80) {
                        //product
                        buf.insert(caretPosition, "×");
                    }else if (key == 67) {
                        //complement
                        buf.insert(caretPosition, "'");
                    }
                    commandTextField.setText(buf.toString());
                    commandTextField.setCaretPosition(caretPosition + 1);
                    mode = Mode.TYPING;
                }

                if(key == 17) {
                    mode = Mode.SHORTCUT;
                } else if (key == 10) {
                    enterPressed();
                } else if(key == 38) {
                    //Up key
                    if(!prev.isEmpty()) {
                        next.push(commandTextField.getText());
                        commandTextField.setText(prev.pop());
                    }
                } else if(key == 40) {
                    //Down key
                    if(!next.isEmpty()) {
                        prev.push(commandTextField.getText());
                        commandTextField.setText(next.pop());
                    }
                }
            }
        });
    }
    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public static void main(String[] args) {
        JFrame ms = new JFrame();

        ms.setLocationRelativeTo(null);

        ms.setContentPane(new MainScreen().mainPanel);
        ms.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ms.pack();

        ms.setVisible(true);
    }
}
