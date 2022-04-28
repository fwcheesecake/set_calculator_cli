import exceptions.InvalidLanguageException;

import java.util.HashMap;
import java.util.HashSet;

public class Languages {
    private Languages() {

    }

    private static final HashSet<Character> alphabet = new HashSet<>();
    private static final Language universe = new Language();
    private static final HashMap<String, Language> languages = new HashMap<>();

    public static HashSet<Character> getAlphabet() {
        return alphabet;
    }
    public static Language getUniverse() {
        return universe;
    }
    public static HashMap<String, Language> getLanguages() {
        return languages;
    }

    public static Language getOne(String name) throws InvalidLanguageException {
        if(languages.get(name) == null)
            throw new InvalidLanguageException(" " + name + " does not exists");
        return languages.get(name);
    }

    public static Language union(Language a, Language b) {
        Language r = new Language(a);
        r.addAll(b);
        return r;
    }
    public static Language intersection(Language a, Language b) {
        Language r = new Language(a);
        r.retainAll(b);
        return r;
    }
    public static Language difference(Language a, Language b) {
        Language r = new Language(a);
        r.removeAll(b);
        return r;
    }
    public static Language symmetricalDifference(Language a, Language b) {
        return difference(union(a, b), intersection(a, b));
    }
    public static Language complement(Language a) {
        return difference(universe, a);
    }
    public static Language product(Language a, Language b) {
        Language r = new Language();
        for(String s : a)
            for(String t : b)
                r.add(s + t);
        return r;
    }
    public static Language kleeneClosure(Language a) {
        //
        Language r = new Language();

        for(int i = 0; i <= 3; i++)
            r.addAll(power(a, i));

        return r;
    }
    public static Language positiveClosure(Language a) {
        //ε
        Language r = new Language();

        for(int i = 1; i <= 3; i++)
            r.addAll(power(a, i));

        System.out.println(a.size());

        return r;
    }

    private static Language power(Language a, int n) {
        if(n == 0)
            return new Language(new String[]{""});
        Language r = new Language(a);
        for(int i = 1; i < n; i++) {
            r = product(a, r);
        }
        return r;
    }

    public static boolean isValid(String[] values) {
        for (String value : values) {
            char[] cs = value.toCharArray();
            for (char c : cs) {
                if (!alphabet.contains(c))
                    return false;
            }
        }
        return true;
    }

    public static void addSymbol(Character c) {
        alphabet.add(c);
    }
    public static void addLanguage(String name, Language value) {
        languages.put(name, value);
        universe.addAll(value);
    }
}
