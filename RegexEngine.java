import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/** 
 *******************************************************************************
 * @file regular-expression-engine/RegexEngine.java
 * @author Daolin Chen - a1838238
 * @date 28082021
 * @brief A java regex engine simulator
 *******************************************************************************
 */

/**
 * regex engine
 */ 
public class RegexEngine {

    static boolean verbose = false;
    Pattern pattern;

    public RegexEngine(String string) {
        this.pattern = genPattern(string);
    }

    public boolean match(String string) {
        String result = this.pattern.trimMatch(string);
        return result != null && result.length() == 0;
    }

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("-v")) {
            verbose = true;
        }
        RegexEngine regexEngine = new RegexEngine("ab|c");
        regexEngine.match("ab");
    }

    //generate a state of a given regex string
    Pattern genPattern(String string) {
        Pattern pattern = null;
        for (int i = 0; i < string.length(); i++) {
            if (Character.isLetter(string.charAt(i))) {
                int len = 1;
                while (i + len < string.length() && Character.isLetter(string.charAt(i + len))) {
                    len++;
                }
                pattern = new StringPattern(string.substring(i, i + len));
                i += len-1;
                continue;
            }
            switch (string.charAt(i)) {
                case '+':
                    pattern = new PlusPattern(pattern);
                    break;
                case '|':
                    return new OrPattern(pattern, genPattern(string.substring(i+1)));
                default:
                    break;
            }
        }
        return pattern;
    }

}

/**
 * @brief it the given string matches thie pattern, then trim the matching part
 * from string
 * @param string
 * @retval the remaining string if it matches, none otherwiese
 */
interface Pattern {
    String trimMatch(String string);
}

class StringPattern implements Pattern {
    String string;

    public StringPattern(String string) {
        this.string = string;
    }

    @Override
    public String trimMatch(String string) {
        if (string.startsWith(this.string)) {
            return string.substring(this.string.length());
        }
        return null;
    }
}

class PlusPattern implements Pattern {
    Pattern pattern;

    public PlusPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public String trimMatch(String string) {
        String rest = pattern.trimMatch(string);
        if (rest != null) {
            String result;
            do {
                result = rest;
                rest = pattern.trimMatch(rest);
            } while (rest != null);
            return result;
        }
        return null;
    }
}

class OrPattern implements Pattern {
    Pattern left, right;

    public OrPattern(Pattern left, Pattern right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String trimMatch(String string) {
        String left = this.left.trimMatch(string);
        String right = this.right.trimMatch(string);
        return left == null ? right:left;
    }
}
