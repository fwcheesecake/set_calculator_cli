import exceptions.InvalidLanguageException;

import java.util.Stack;

public class Evaluator {
    public static Language evaluate(String expression) throws InvalidLanguageException {
        char[] tokens = expression.toCharArray();

        Stack<Character> operators = new Stack<>();
        Stack<Language> values = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            if(tokens[i] == ' ')
                continue;

            //TODO validate language names. The mustn't start with a number
            if(tokens[i] >= '0' && tokens[i] <= '9')
                throw  new InvalidLanguageException("Syntax error. Language name must start with a letter");

            if ((tokens[i] >= 65 && tokens[i] <= 90) ||
                    (tokens[i] >= 97 && tokens[i] <= 122)) {
                //Obtains the full name of the found language
                StringBuilder buf = new StringBuilder();
                while (i < tokens.length && !isDelimiter(tokens[i])) {
                    buf.append(tokens[i]);
                    i++;
                }

                Language op = new Language(Languages.getOne(buf.toString()));
                values.push(op);
                i--;
            }
            else if(tokens[i] == '{') {
                int ini = i + 1;
                while(tokens[i] != '}')
                    i++;
                String subL = expression.substring(ini, i);

                String[] subLSSplit = subL.split("\\s*,\\s*");
                if(!Languages.isValid(subLSSplit)) {
                    throw new InvalidLanguageException(" Invalid Inline language");
                } else {
                    values.push(new Language(subLSSplit));
                }
            } else if (tokens[i] == '(') {
                operators.push(tokens[i]);
            }
            else if(tokens[i] == ')') {
                while(operators.peek() != '(')
                    applyOp(operators, values);
                operators.pop();
            }
            else if (isOperator(tokens[i])) {
                while(!operators.isEmpty() && operators.peek() != '(')
                    applyOp(operators, values);
                operators.push(tokens[i]);
            }
        }

        while(!operators.isEmpty())
            applyOp(operators, values);

        return values.pop();
    }

    private static void applyOp(Stack<Character> operators, Stack<Language> values) {
        char op = operators.pop();

        Language b;
        Language a;

        b = new Language(values.pop());
        if(op == '\'') {
            values.push(Languages.complement(b));
            return;
        }
        a = new Language(values.pop());

        Language result = switch (op) {
            case '∪' -> Languages.union(a, b);
            case '∩' -> Languages.intersection(a, b);
            case '-' -> Languages.difference(a, b);
            case 'Δ' -> Languages.symmetricalDifference(a, b);
            case '*' -> Languages.product(a, b);
            default -> null;
        };

        values.push(result);
    }

    private static boolean isOperator(char token) {
        return (token == '\'' || token == '*' || token == '∪' ||
                token == '∩'  || token == 'Δ' || token == '-');
    }
    private static boolean isDelimiter(char token) {
        return (token == ' '  || token == '{' || token == '}'  ||
                token == '('  || token == ')' || token == '\'' ||
                token == '*'  || token == '∪' || token == '∩'  ||
                token == 'Δ'  || token == '-');
    }
}
