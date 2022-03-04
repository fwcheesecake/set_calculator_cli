import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Lenguajes {
    //private static Lenguajes instancia = null;

    private Lenguajes() {

    }

    private static final Lenguaje universo = new Lenguaje();

    private static final HashSet<Character> alfabeto = new HashSet<>();
    private static final HashMap<String, Lenguaje> lenguajes = new HashMap<>();

    /*
    public static Lenguajes getInstance() {
            if(instancia.equals(null))
                instancia = new Lenguajes();
            return instancia;
    }
     */

    public static Lenguaje getUniverso() {
        return universo;
    }
    public static HashMap<String, Lenguaje> getLenguajes() {
        return lenguajes;
    }
    public static HashSet<Character> getAlfabeto() {
        return alfabeto;
    }

    public static Lenguaje getOne(String name) {
        return lenguajes.get(name);
    }

    public static Lenguaje union(Lenguaje a, Lenguaje b) {
        Lenguaje r = new Lenguaje(a);
        r.addAll(b);
        return r;
    }

    public static Lenguaje interseccion(Lenguaje a, Lenguaje b) {
        Lenguaje r = new Lenguaje(a);
        r.retainAll(b);
        return r;
    }

    public static Lenguaje diferencia(Lenguaje a, Lenguaje b) {
        Lenguaje r = new Lenguaje(a);
        r.removeAll(b);
        return r;
    }

    public static Lenguaje difsim(Lenguaje a, Lenguaje b) {
        return diferencia(union(a, b), interseccion(a, b));
    }

    public static Lenguaje complemento(Lenguaje a) {
        return diferencia(universo, a);
    }

    public static Lenguaje producto(Lenguaje a, Lenguaje b) {
        Lenguaje r = new Lenguaje();
        for(String s : a)
            for(String t : b)
                r.add(s + t);
        return r;
    }

    public static boolean esValido(String[] valores) {
        for (String valor : valores) {
            char[] cs = valor.toCharArray();
            for (char c : cs) {
                if (!alfabeto.contains(c))
                    return false;
            }
        }
        return true;
    }

    public static void agregarSimbolo(Character c) {
        alfabeto.add(c);
    }
    public static void agregaLenguaje(String nombre, Lenguaje valor) {
        lenguajes.put(nombre, valor);
        universo.addAll(valor);
    }
}
