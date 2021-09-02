import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
/** 
 *******************************************************************************
 * @file regular-expression-engine/RegexTest.java
 * @author Daolin Chen - a1838238
 * @date 02092021
 * @brief Junit test for Regex engine
 *******************************************************************************
 */

public class RegexTest {

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

    @Test
    public void testPlusDigit() {
        RegexEngine regexEngine = new RegexEngine("12+");
        assertTrue(regexEngine.match("12222"));
        assertFalse(regexEngine.match("1"));
    }

    @Test
    public void testMultDigit() {
        RegexEngine regexEngine = new RegexEngine("123*");
        assertTrue(regexEngine.match("1233333"));
        assertTrue(regexEngine.match("12"));
    }

    @Test
    public void testOrDigit() {
        RegexEngine regexEngine = new RegexEngine("123|4");
        assertTrue(regexEngine.match("123"));
        assertTrue(regexEngine.match("4"));
        assertFalse(regexEngine.match("1234"));
    }

    @Test
    public void testPlusWithSpace() {
        RegexEngine regexEngine = new RegexEngine("c+ 123");
        assertTrue(regexEngine.match("ccc 123"));
        assertFalse(regexEngine.match("cc123"));
    }

    @Test
    public void testMultWithSpace() {
        RegexEngine regexEngine = new RegexEngine("ab *123");
        assertTrue(regexEngine.match("ab123"));
        assertTrue(regexEngine.match("ab    123"));
    }
    
    @Test
    public void testOrWithSpace() {
        RegexEngine regexEngine = new RegexEngine("ab|a b");
        assertTrue(regexEngine.match("ab"));
        assertTrue(regexEngine.match("a b"));
        assertFalse(regexEngine.match("ab b"));
    }

    @Test
    public void test01() {
        RegexEngine regexEngine = new RegexEngine("ab*|c+");
        assertTrue(regexEngine.match("abb"));
        assertTrue(regexEngine.match("ccc"));
        assertFalse(regexEngine.match("abc"));
    }

    @Test
    public void test02() {
        RegexEngine regexEngine = new RegexEngine("a+b*|c* 1*|d *e+");
        assertTrue(regexEngine.match("aaa"));
        assertTrue(regexEngine.match("c 1111"));
        assertTrue(regexEngine.match("deeeeeee"));
        assertTrue(regexEngine.match("d      e"));
        assertFalse(regexEngine.match("bbb"));
        assertFalse(regexEngine.match("c  "));
        assertFalse(regexEngine.match("d    "));
    }
}

