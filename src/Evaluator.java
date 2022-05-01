import exceptions.InvalidLanguageException;
import exceptions.InvalidSyntaxException;

import java.util.Stack;

public class Evaluator {
    public static Language evaluate(String expression) throws InvalidLanguageException, InvalidSyntaxException {
        char[] tokens = expression.toCharArray();

        Stack<Character> operators = new Stack<>();
        Stack<Language> values = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            if(tokens[i] == ' ')
                continue;

            if(tokens[i] >= '0' && tokens[i] <= '9')
                throw  new InvalidLanguageException("Syntax error. Language name must start with a letter");

            if ((tokens[i] >= 'a' && tokens[i] <= 'z') ||
                    (tokens[i] >= 'A' && tokens[i] <= 'Z' || tokens[i] == '$' || tokens[i] == '_')) {
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
                while(i < tokens.length && tokens[i] != '}')
                    i++;

                if(i == tokens.length)
                    throw new InvalidSyntaxException(" Syntax error. Missing }");

                String subL = expression.substring(ini, i);

                String[] subLSSplit = subL.split("\\s*,\\s*");
                if(!Languages.isValid(subLSSplit)) {
                    throw new InvalidLanguageException(" Invalid Inline language");
                } else {
                    values.push(new Language(subLSSplit));
                }
            }  else if(tokens[i] == '}') {
                throw new InvalidLanguageException(" Syntax error. Missing {");
            } else if (tokens[i] == '(') {
                operators.push(tokens[i]);
            }
            else if(tokens[i] == ')') {
                while(operators.peek() != '(')
                    applyOp(operators, values);
                operators.pop();
            }
            else if (isOperator(tokens[i])) {
                while(!operators.isEmpty() && precedence(operators.peek()) >= precedence(tokens[i]))
                    applyOp(operators, values);
                operators.push(tokens[i]);
            }
        }

        while(!operators.isEmpty())
            applyOp(operators, values);

        Language ret = values.pop();
        System.out.println("Size: " + ret.size());
        return ret;
    }

    private static void applyOp(Stack<Character> operators, Stack<Language> values) {
        char op = operators.pop();

        Language b;
        Language a;

        b = new Language(values.pop());
        if(op == '\'') {
            values.push(Languages.complement(b));
            return;
        } else if(op == '*') {
            values.push(Languages.kleeneClosure(b));
            return;
        } else if (op == '+') {
            values.push(Languages.positiveClosure(b));
            return;
        }
        a = new Language(values.pop());

        Language result = switch (op) {
            case '∪' -> Languages.union(a, b);
            case '∩' -> Languages.intersection(a, b);
            case '-' -> Languages.difference(a, b);
            case 'Δ' -> Languages.symmetricalDifference(a, b);
            case '×' -> Languages.product(a, b);
            default -> null;
        };

        values.push(result);
    }

    private static boolean isOperator(char token) {
        return (token == '\'' || token == '*' || token == '∪' ||
                token == '∩'  || token == 'Δ' || token == '-' ||
                token == '+'  || token == '×');
    }
    private static boolean isDelimiter(char token) {
        return (token == ' '  || token == '{' || token == '}'  ||
                token == '('  || token == ')' || token == '\'' ||
                token == '*'  || token == '∪' || token == '∩'  ||
                token == 'Δ'  || token == '-' || token == '+' || token == '×');
    }
    private static int precedence(char token) {
        if(token == '\'' || token == '∪' || token == '∩'  ||
                token == 'Δ' || token == '-' || token == '×')
            return 1;
        if(token == '*' || token == '+')
            return 2;
        return 0;
    }
}
