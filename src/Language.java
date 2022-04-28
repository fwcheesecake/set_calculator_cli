import java.util.*;

public class Language extends LinkedHashSet<String> {
    public Language() {
    }

    public Language(String[] values) {
        this.addAll(Arrays.asList(values));
    }

    public Language(Language l) {
        this.addAll(l);
    }

    public String print() {
        StringBuilder s = new StringBuilder("{");

        for(String l : this) {
            if(l.equals(""))
                s.append("ε");
            else
                s.append(l);
            s.append(", ");
        }
        s.delete(s.length() - 2, s.length());
        s.append("}");
        return s.toString();
    }
}
