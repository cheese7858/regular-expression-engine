
/** 
 *******************************************************************************
 * @file regular-expression-engine/RegexEngine.java
 * @author Daolin Chen - a1838238
 * @date 28082021
 * @brief A java regex engine simulator
 *******************************************************************************
 */

/**
 * regexengine
 */ 
public class RegexEngine {

    Pattern pattern;

    public RegexEngine(String string) {
        this.pattern = genPattern(string);
    }

    public boolean match(String string){
        String result = this.pattern.trimMatch(string);
        return result != null && result.length() == 0;
    }

    public static void main(String args){

    }

    Pattern genPattern(String string) {

         Pattern pattern = null;
         


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
