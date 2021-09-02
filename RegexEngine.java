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
    State startState;

    public RegexEngine(String string) {
        this.startState = genState(string);
    }

    public boolean match(String string) {
        return startState.match(string);
    }

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("-v")) {
            verbose = true;
        }
        RegexEngine regexEngine = new RegexEngine("c+");
        regexEngine.match("ccc");
    }

    /**
     * @brief generate a serial of state from the given regex string,
     * which does not have alternation operators in the first layer
     * @param string
     * @retval the tail of this serial
     */
    State genStateWithoutAlter(String s) {
        State tail = null, head = null, current;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '(':
                    int start = i + 1;
                    int len = 0;
                    int pares = 1;
                    while (pares > 0) {
                        len++;
                        if (s.charAt(start + len - 1) == ')') {
                            pares--;
                        }
                    }
                    current = genStateWithoutAlter(s.substring(start, start + len));
                    i = start + len;
                    break;
                default:
                    assert Character.isLetterOrDigit(ch) || Character.isSpaceChar(ch);
                    current = new PatternState(new CharPattern(ch));
                    break;
            }
            if (i + 1 < s.length()) {
                switch (s.charAt(i + 1)) {
                    case '+':
                        PlusState plusState = new PlusState(current);
                        current = plusState;
                        i++;
                        break;
                    case '*':
                        MultState multState = new MultState(current);
                        current = multState;
                        i++;
                        break;
                    default:
                        break;
                }
            }
            if (head == null) {
                head = current;
                tail = current;
            } else {
                tail.addNext(current);
                tail = current;
            }
        }
        return head;
    }
    
    /**
     * @brief generate a serial states from the given regex string
     * @param string
     * @retval the tail of this serial
     */
    State genState(String s) {
        List<Integer> alters = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '|':
                    alters.add(i);
                    break;
                case '(':
                    int pares = 1;
                    while (pares > 0) {
                        i++;
                        if (s.charAt(i) == '(') {
                            pares++;
                        } else if (s.charAt(i) == ')') {
                            pares--;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        GroupState group = new GroupState();
        EmptyState entrance = new EmptyState();
        List<State> bodys = new ArrayList<>();
        int pre = 0;
        for (Integer i : alters) {
            bodys.add(genStateWithoutAlter(s.substring(pre, i)));
            pre = i + 1;
        }
        bodys.add(genStateWithoutAlter(s.substring(pre)));
        EmptyState exit = new EmptyState();
        group.entrance = entrance;
        group.exit = exit;
        for (State state : bodys) {
            group.addBody(state);
        }
        return group;
    }
}

class State {
    static int count = 0;
    int id;
    List<State> next;

    public State() {
        id = count;
        count++;
        next = new ArrayList<>();
    }

    public void addNext(State state) {
        next.add(state);
    }

    public boolean isEnd() {
        return next.isEmpty();
    }

    public boolean match(String string) {
        return false;
    }

    public List<State> toTails() {
        List<State> result = new ArrayList<>();
        if (isEnd()) {
            result.add(this);
        } else {
            for (State state : next) {
                result.addAll(state.toTails());
            }
        }
        return result;
    }
}

class GroupState extends State {
    EmptyState entrance, exit;

    public GroupState() {
        entrance = new EmptyState();
        exit = new EmptyState();
    }

    public void addBody(State state) {
        entrance.addNext(state);
        List<State> tails = state.toTails();
        for (State tail : tails) {
            tail.addNext(exit);
        }
    }

    @Override
    public boolean match(String string) {
        for (State state : entrance.next) {
            if (state.match(string)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEnd() {
        return exit.isEnd();
    }

    @Override
    public void addNext(State state) {
        exit.addNext(state);
    }

    @Override
    public List<State> toTails() {
        List<State> result = new ArrayList<>();
        if (isEnd()) {
            result.add(exit);
        } else {
            for (State state : exit.next) {
                if (state != entrance) {
                    result.addAll(state.toTails());
                }
            }
        }
        return result;
    }
}

class PlusState extends GroupState {
    public PlusState(State state) {
        addBody(state);
        exit.addNext(entrance);
    }
    @Override
    public boolean isEnd() {
        return exit.next.size() == 1 && exit.next.get(0).equals(entrance);
    }
}

class MultState extends GroupState {
    public MultState(State state) {
        PlusState plusState = new PlusState(state);
        EmptyState emptyState = new EmptyState();
        addBody(plusState);
        addBody(emptyState);
    }
}

class PatternState extends State {
    Pattern pattern;

    public PatternState(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean match(String string) {
        String trimed = pattern.trimMatch(string);
        if (trimed != null) {
            for (State state : next) {
                if (state.match(trimed)) {
                    return true;
                }
            }
        }
        return false;
    }
}

class EmptyState extends PatternState {
    public EmptyState() {
        super(new EmptyPattern());
    }

    @Override
    public boolean match(String string) {
        if (isEnd() && string.isEmpty()) {
            return true;
        }
        return super.match(string);
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

class EmptyPattern implements Pattern {
    @Override
    public String trimMatch(String string) {
        return string;
    }
}

class CharPattern implements Pattern {
    char c;

    public CharPattern(char c) {
        this.c = c;
    }

    @Override
    public String trimMatch(String string) {
        if (string.isEmpty()) {
            return null;
        }
        if (string.charAt(0) == c) {
            return string.substring(1);
        }
        return null;
    }
}
