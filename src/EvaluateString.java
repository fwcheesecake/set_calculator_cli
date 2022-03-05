import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class EvaluateString
{
	/*
		llave: valor

		L1: {adfia, bsdfajl, jdfc, dxdkaj}
		L2: {ada, bsjl, dfc, dxkaj}
		L3: {adskflj, dlsajf, jdfc, dxdkaj}
		L4: {adfia, bsdfajl, asdjlf, jzxco}
	*/
	private static final HashMap<String, Language> lenguajes = new HashMap<>();

	public static Language evaluate(String expression)
	{
		char[] tokens = expression.toCharArray();

		// Pila para los lenguajes
		Stack<Language> valores = new Stack<>();

		// Pila para los operadores
		Stack<Character> operadores = new Stack<>();

		for (int i = 0; i < tokens.length; i++)
		{
			// Si el caracter actual es un
			// espacio en blanco, te lo saltas
			if (tokens[i] == ' ')
				continue;

			//Si el caracter actual
			//es un nombre
			if ((tokens[i] >= 'a' && tokens[i] <= 'z') ||
					(tokens[i] >= 'A' && tokens[i] <= 'Z'))
			{
				StringBuilder sbuf = new StringBuilder();

				// There may be more than one
				// digits in number
				while (i < tokens.length && (
						(tokens[i] >= '0' && tokens[i] <= '9') ||
						(tokens[i] >= 'a' && tokens[i] <= 'z') ||
						(tokens[i] >= 'A' && tokens[i] <= 'Z'))) {
					sbuf.append(tokens[i++]);
				}
				valores.push(lenguajes.get(sbuf.toString()));
				i--;
			}

			// Current token is an opening brace,
			// push it to 'ops'
			else if (tokens[i] == '(')
				operadores.push(tokens[i]);

				// Closing brace encountered,
				// solve entire brace
			else if (tokens[i] == ')')
			{
				while (operadores.peek() != '(')
					valores.push(applyOp(operadores.pop(),
							valores.pop(),
							valores.pop()));
				operadores.pop();
			}

			// Current token is an operator.
			else if (tokens[i] == 'Δ' ||
					tokens[i] == '-' ||
					tokens[i] == '*' ||
					tokens[i] == '∪' ||
					tokens[i] == '∩' ||
					tokens[i] == '\'')
			{
				// While top of 'ops' has same
				// or greater precedence to current
				// token, which is an operator.
				// Apply operator on top of 'ops'
				// to top two elements in values stack
				while (!operadores.isEmpty() &&
						hasPrecedence(tokens[i], operadores.peek()))
					valores.push(applyOp(operadores.pop(),
							valores.pop(),
							valores.pop()));

				// Push current token to 'ops'.
				operadores.push(tokens[i]);
			}
		}

		// Entire expression has been
		// parsed at this point, apply remaining
		// ops to remaining values
		while (!operadores.empty())
			valores.push(applyOp(operadores.pop(),
					valores.pop(),
					valores.pop()));

		// Top of 'values' contains
		// result, return it
		return valores.pop();
	}

	// Returns true if 'op2' has higher
	// or same precedence as 'op1',
	// otherwise returns false.
	public static boolean hasPrecedence(char op1, char op2)
	{
			return !(op2 == '(' || op2 == ')');
	}

	// A utility method to apply an
	// operator 'op' on operands 'a'
	// and 'b'. Return the result.
	public static Language applyOp(char op,
                                   Language b, Language a)
	{
		return switch (op) {
			case 'Δ' -> difsim(a, b);
			case '∪' -> union(a, b);
			case '-' -> diferencia(a, b);
			case '∩' -> interseccion(a, b);
			default -> null;
		};
	}

	private static Language union(Language a, Language b) {
		Language resultado = new Language();
		resultado.addAll(a);
		resultado.addAll(b);
		return resultado;
	}
	private static Language interseccion(Language a, Language b) {
		Language resultado = new Language();
		resultado.addAll(a);
		resultado.retainAll(b);
		return resultado;
	}
	private static Language diferencia(Language a, Language b) {
		Language resultado = new Language();
		resultado.addAll(a);
		resultado.removeAll(b);
		return resultado;
	}
	private static Language difsim(Language a, Language b) {
		Language op1 = union(a, b);
		Language op2 = interseccion(a, b);
		return diferencia(op1, op2);
	}
	private static Language complemento(Language a) {
		Language resultado = new Language();
		resultado.addAll(a);
		return resultado;
	}
	private static Language producto(Language a, Language b) {
		Language resultado = new Language();
		resultado.addAll(a);
		resultado.addAll(b);
		return resultado;
	}

	// Driver method to test above methods
	public static void main(String[] args) {
		lenguajes.put("L1", new Language(new String[]{"abc", "ac", "12"}));
		lenguajes.put("L2", new Language(new String[]{"23", "12", "ab", "c2"}));
		lenguajes.put("L3", new Language(new String[]{"23", "13", "a3", "ab"}));
		Scanner sc = new Scanner(System.in);

		System.out.println("Cuantos lenguajes quieres introducir?");
		int n = sc.nextInt();
		sc.nextLine();

		/*
		for(int i = 1; i <= n; i++) {
			String l = sc.nextLine();
			String[] casiConjunto = l.split(",*\\s*");
			Lenguaje L = new Lenguaje(casiConjunto);
			lenguajes.put("L" + n, L);
		}*/

		while(true) {
			System.out.println("Ingresa la operacion: ");
			String operacion = sc.nextLine();

			System.out.println(EvaluateString.
					evaluate(operacion));
		}
	}
}
