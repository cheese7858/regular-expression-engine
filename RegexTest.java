import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Test {
    @Test
    public void testPlus() {
        RegexEngine regexEngine = new RegexEngine("c+");
        assertTrue(regexEngine.match("ccc"));
        assertFalse(regexEngine.match("cca"));
    }
    @Test
    public void testMult() {
        RegexEngine regexEngine = new RegexEngine("ab*");
        assertTrue(regexEngine.match("a"));
        assertTrue(regexEngine.match("abbb"));
    }
    @Test
    public void testOr() {
        RegexEngine regexEngine = new RegexEngine("ab|c");
        assertTrue(regexEngine.match("ab"));
        assertTrue(regexEngine.match("c"));
        assertFalse(regexEngine.match("abc"));
    }
}
