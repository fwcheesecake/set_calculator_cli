import java.util.Arrays;
import java.util.HashSet;

public class Language extends HashSet<String> {
    public Language() {
    }

    public Language(String[] values) {
        this.addAll(Arrays.asList(values));
    }

    public Language(Language l) {
        this.addAll(l);
    }
}
