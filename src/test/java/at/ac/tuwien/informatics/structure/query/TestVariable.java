package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.query.Variable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestVariable {

    @Test
    public void testEqualVariables() {
        Variable v1 = new Variable("x");
        Variable v2 = new Variable("x");

        assertEquals(v1, v2);
    }

    @Test
    public void testUnequalVariables() {
        Variable v1 = new Variable("x");
        Variable v2 = new Variable("y");

        assertNotEquals(v1, v2);
    }

    @Test
    public void testSetOfEqualVariables() {
        Variable v1 = new Variable("x");
        Variable v2 = new Variable("x");

        assertEquals(v1, v2);

        HashSet<Variable> set1 = new HashSet<>(Arrays.asList(v1, v2));
        HashSet<Variable> set2 = new HashSet<>();
        set2.add(v1);

        assertEquals(set1, set2);
    }

    @Test
    public void testString() {
        Variable v1 = new Variable("x");

        assertEquals("x", v1.toString());
    }
}
