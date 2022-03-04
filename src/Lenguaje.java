import java.util.Arrays;
import java.util.HashSet;

public class Lenguaje extends HashSet<String> {
    public Lenguaje() {
    }

    public Lenguaje(String[] valores) {
        this.addAll(Arrays.asList(valores));
    }

    public Lenguaje(Lenguaje l) {
        this.addAll(l);
    }
}
