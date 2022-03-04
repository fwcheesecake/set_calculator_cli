import java.lang.reflect.Array;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String rawInput;

        System.out.print("Alfabeto: ");
        rawInput = sc.nextLine();

        String[] alfaCrudo = rawInput.split("\\s*,+\\s*");
        for(String s : alfaCrudo)
            Lenguajes.agregarSimbolo(s.charAt(0));

        while(true) {
            System.out.print(">>> ");
            rawInput = sc.nextLine();

            if(rawInput.equals("exit")) break;

            String[] input = rawInput.split("=");

            if (input.length == 1) {
                String realInput = input[0].trim();
                if(!realInput.equals(""))
                    System.out.println(Evaluator.evaluate(realInput));
            } else {
                String nombre = input[0].trim();
                String valor = input[1].trim();
                String[] valores = valor.split("\\s*,+\\s*");
                if(!Lenguajes.esValido(valores))
                    System.out.println("Lenguaje invalido");
                else {
                    Lenguajes.agregaLenguaje(nombre, new Lenguaje(valores));
                }
            }
        }
    }
}
