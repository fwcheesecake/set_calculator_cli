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
        String rawInput = commandTextField.getText();

        normalLog(">>> " + rawInput);

        try {
            if (rawInput.equals("universe")) {
                normalLog(" U = " + Languages.getUniverse().toString());
            } else if (rawInput.equals("exit")) {
                System.exit(0);
            } else if (rawInput.equals("clear")) {
                outputTextPane.setText("");
            } else if(rawInput.matches(".*alphabet.*")) {
                if (rawInput.matches("alphabet\\s*=\\s*\\{([a-zA-Z0-9]\\s*,\\s*)*[^,\\s]}")) {
                    if (Languages.getAlphabet().isEmpty()) {
                        String alphabet = rawInput.split("\\s*=\\s*")[1].trim();
                        alphabet = alphabet.substring(1, alphabet.length() - 1);
                        for (String s : alphabet.split("\\s*,\\s*"))
                            Languages.addSymbol(s.charAt(0));
                        alphabetTextPane.setText(rawInput);
                        successLog(" Alphabet initialized successfully");
                    } else {
                        throw new InvalidAlphabetException(" The alphabet is already set");
                    }
                } else {
                    throw new InvalidAlphabetException(" The alphabet is not valid");
                }
            } else if (rawInput.matches("[a-zA-Z][0-9]*\\s*=\\s*\\{(.*,*)}")) {
                String[] splitInput = rawInput.split("\\s*=\\s*");
                String name = splitInput[0].trim();
                String value = splitInput[1].trim();
                String[] values = value.substring(1, value.length() - 1).split("\\s*,\\s*");

                if (Languages.isValid(values)) {
                    Languages.addLanguage(name, new Language(values));
                    updateLanguages();
                    successLog(" Language added successfully");
                } else if (Languages.getAlphabet().isEmpty()) {
                    throw new InvalidLanguageException(" The alphabet is empty. Set it with alphabet = {[values]}");
                } else {
                    throw new InvalidLanguageException(" Invalid language");
                }
            } else if (rawInput.matches("[a-zA-Z][0-9]*\\s*=\\s*[a-zA-Z0-9\\sΔ\\-*×+∪∩'{,}()]+")) {
                //TODO new regex to validate L1 = {a, b,

                String[] splitInput = rawInput.split("\\s*=\\s*");
                String name = splitInput[0].trim();
                Language value = Evaluator.evaluate(splitInput[1].trim());

                Languages.addLanguage(name, value);
                updateLanguages();

                successLog(" Language added successfully: " + value.toString());
            } else if (rawInput.matches("[a-zA-Z0-9\\s∪\\-*+'×∩Δ{},]+")) {
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
                System.out.println(key);

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
