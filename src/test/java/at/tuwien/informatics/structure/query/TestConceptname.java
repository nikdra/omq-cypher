package at.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.query.Atom;
import at.ac.tuwien.informatics.structure.query.Conceptname;
import at.ac.tuwien.informatics.structure.query.UnboundVariable;
import at.ac.tuwien.informatics.structure.query.Variable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestConceptname {

    @Test
    public void testEqualConceptnames() {
        Conceptname c1 = new Conceptname("A", new Variable("x"));
        Conceptname c2 = new Conceptname("A", new Variable("x"));

        assertEquals(c1, c2);
    }

    @Test
    public void testUnequalConcepts() {
        Conceptname c1 = new Conceptname("A", new Variable("x"));
        Conceptname c2 = new Conceptname("B", new Variable("x"));

        assertNotEquals(c1, c2);

        c1 = new Conceptname("A", new Variable("x"));
        c2 = new Conceptname("A", new Variable("y"));

        assertNotEquals(c1, c2);

        c1 = new Conceptname("A", new UnboundVariable("x"));
        c2 = new Conceptname("A", new UnboundVariable("y"));

        assertNotEquals(c1, c2);
    }

    @Test
    public void testSetOfEqualConcepts() {
        Conceptname c1 = new Conceptname("A", new Variable("x"));
        Conceptname c2 = new Conceptname("A", new Variable("x"));

        assertEquals(c1, c2);

        HashSet<Atom> set1 = new HashSet<>(Arrays.asList(c1, c2));
        HashSet<Atom> set2 = new HashSet<>();
        set2.add(c1);

        assertEquals(set1, set2);
    }

    @Test
    public void testString() {
        Conceptname c1 = new Conceptname("A", new Variable("x"));

        assertEquals(c1.toString(), "A(x)");
    }
}
