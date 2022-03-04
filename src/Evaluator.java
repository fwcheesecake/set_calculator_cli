import java.util.Stack;

public class Evaluator {
    public static Lenguaje evaluate(String expression) {
        //ArrayList<String> procedimiento = new ArrayList<>();
        //procedimiento.add(expresion);

        char[] tokens = expression.toCharArray();

        Stack<Character> operadores = new Stack<>();
        Stack<Lenguaje> valores = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            if(tokens[i] == '\0' || tokens[i] == ' ')
                continue;

            //TODO inline laguages

            if ((tokens[i] >= 65 && tokens[i] <= 90) ||
                    (tokens[i] >= 97 && tokens[i] <= 122)) {
                //Obtiene el nombre completo del lenguaje encontrado
                StringBuilder buf = new StringBuilder();
                while (i < tokens.length && !esDelimitador(tokens[i])) {
                    buf.append(tokens[i]);
                    i++;
                }

                Lenguaje op = new Lenguaje(Lenguajes.getOne(buf.toString()));
                valores.push(op);
                i--;
            }
            else if (tokens[i] == '(') {
                operadores.push(tokens[i]);
            }
            else if(tokens[i] == ')') {
                while(operadores.peek() != '(')
                    operacion(operadores, valores);
                operadores.pop();
            }
            else if (esOperador(tokens[i])) {
                while(!operadores.isEmpty() && operadores.peek() != '(')
                    operacion(operadores, valores);
                operadores.push(tokens[i]);
            }
        }

        while(!operadores.isEmpty())
            operacion(operadores, valores);

        return valores.pop();
    }

    private static void operacion(Stack<Character> operadores, Stack<Lenguaje> valores) {
        char op = operadores.pop();

        Lenguaje b;
        Lenguaje a;

        b = new Lenguaje(valores.pop());
        if(op == '\'') {
            valores.push(Lenguajes.complemento(b));
            return;
        }
        a = new Lenguaje(valores.pop());

        Lenguaje resultado = switch (op) {
            case '∪' -> Lenguajes.union(a, b);
            case '∩' -> Lenguajes.interseccion(a, b);
            case '-' -> Lenguajes.diferencia(a, b);
            case 'Δ' -> Lenguajes.difsim(a, b);
            case '*' -> Lenguajes.producto(a, b);
            default -> null;
        };

        valores.push(resultado);
    }

    private static boolean esOperador(char token) {
        return (token == '\'' || token == '*' || token == '∪' ||
                token == '∩'  || token == 'Δ' || token == '-');
    }
    private static boolean esDelimitador(char token) {
        return (token == ' '  || token == '(' || token == ')' ||
                token == '\'' || token == '*' || token == '∪' ||
                token == '∩'  || token == 'Δ' || token == '-');
    }
}
