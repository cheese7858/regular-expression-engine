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
    List<String> patterns = new ArrayList<>();
    State startState;

    public RegexEngine(String string) {
        this.patterns.add("");
        startState = new EmptyState();
        genState(startState, string);
    }

    public boolean match(String string) {
        return startState.match(string);
    }

    public void verbose() {
        State.verbose(patterns);
    }

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("-v")) {
            verbose = true;
        }
        Scanner scanner = new Scanner(System.in);
        String regex = scanner.nextLine();
        RegexEngine regexEngine = new RegexEngine(regex);
        if (verbose) {
            regexEngine.verbose();
            System.out.println();
            System.out.println("ready");
            String string = "";
            while (true) {
                System.out.println(regexEngine.match(string));
                String s = scanner.nextLine();
                if (s.isEmpty()) {
                    string = "";
                } else {
                    string += s;
                }
            }
        } else {
            System.out.println("ready");
            while (true) {
                String s = scanner.nextLine();
                System.out.println(regexEngine.match(s));
            }
        }
    }

    /**
     * @brief generate a serial of state from the given regex string,
     * which does not have alternation operators in the first layer
     * @param string
     * @retval the tail of this serial
     */
    State genStateWithoutAlter(State head, String s) {
        State tail = head, current_head, current_tail;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '(':
                    int start = i + 1;
                    int pares = 1;
                    while (pares > 0) {
                        i++;
                        if (s.charAt(i) == '(') {
                            pares++;
                        } else if (s.charAt(i) == ')') {
                            pares--;
                        }
                    }
                    current_head = new EmptyState();
                    current_tail = genState(current_head, s.substring(start, i));
                    break;
                default:
                    assert Character.isLetterOrDigit(ch) || 
                           Character.isSpaceChar(ch);
                    current_head = current_tail = 
                    new PatternState(new CharPattern(ch));
                    this.patterns.add(String.valueOf(ch));
                    break;
            }
            if (i + 1 < s.length()) {
                switch (s.charAt(i + 1)) {
                    case '+':
                        EmptyState plus_head = new EmptyState(), 
                                   plus_tail = new EmptyState();
                        plus_head.addNext(current_head);
                        current_tail.addNext(plus_tail);
                        plus_tail.addNext(plus_head);
                        current_head = plus_head;
                        current_tail = plus_tail;
                        i++;
                        break;
                    case '*':
                        EmptyState mult_head = new EmptyState();
                        mult_head.addNext(current_head);
                        current_tail.addNext(mult_head);
                        current_head = mult_head;
                        current_tail = mult_head;
                        i++;
                        break;
                    default:
                        break;
                }
            }
            tail.addNext(current_head);
            tail = current_tail;
        }
        return tail;
    }

    
    /**
     * @brief generate a serial states from the given regex string
     * @param string
     * @retval the tail of this serial
     */
    State genState(State head, String s) {
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
        List<State> body_tails = new ArrayList<>();
        int pre = 0;
        for (Integer i : alters) {
            body_tails.add(genStateWithoutAlter(head, s.substring(pre, i)));
            pre = i + 1;
        }
        body_tails.add(genStateWithoutAlter(head, s.substring(pre)));
        EmptyState tail = new EmptyState();
        for (State body_tail : body_tails) {
            body_tail.addNext(tail);
        }
        return tail;
    }
}

class State {
    private static int count = 0;
    private static List<State> states = new ArrayList<>();
    protected int id;
    protected List<State> next;

    public State() {
        id = count;
        count++;
        next = new ArrayList<>();
        states.add(this);
    }

    /**
     * add next state
     * @param state
     */
    public void addNext(State state) {
        next.add(state);
    }

    /**
     * whether it's the end
     * @retval
     */
    public boolean isEnd() {
        return next.isEmpty();
    }

    /**
     * check this state and its next states is matching a given string
     * @param string
     * @retval
     */
    public boolean match(String string) {
        return false;
    }

    /**
     * check if this state accept a given string 
     * @param string
     * @retval
     */
    public boolean accept(String string) {
        return string.isEmpty();
    }

    /**
     * next states of a given pattern
     * @param pattern
     * @retval states 
     */
    public List<State> toNext(String pattern) {
        List<State> result = new ArrayList<>();
        for (State state : next) {
            if (state.accept(pattern)) {
                result.add(state);
            }
        }
        return result;
    }

    /**
     * print state's transition table, in verbose mode
     * @param patterns
     */
    public void print(List<String> patterns) {
        if (isEnd()) {
            System.out.print("*");
        }
        System.out.print("s" + id);
        for (String pattern : patterns) {
            List<String> children = new ArrayList<>();
            for (State state : toNext(pattern)) {
                children.add("s" + state.id);
            }
            System.out.print("\t" + String.join(",", children));
        }
        System.out.println();
    }

    /**
     * verbose mode
     * @param patterns
     */
    public static void verbose(List<String> patterns) {
        System.out.println("\t" + String.join("\t", patterns));
        System.out.print(">");
        for (State state : states) {
            state.print(patterns);
        }
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
            if (trimed.isEmpty() && isEnd()) {
                return true;
            }
            for (State state : next) {
                if (state.match(trimed)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean accept(String string) {
        String reset = pattern.trimMatch(string);
        return reset != null && reset.isEmpty();
    }
}


class EmptyState extends PatternState {
    public EmptyState() {
        super(new EmptyPattern());
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
