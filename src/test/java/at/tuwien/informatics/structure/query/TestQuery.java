package at.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.query.Atom;
import at.ac.tuwien.informatics.structure.query.Conceptname;
import at.ac.tuwien.informatics.structure.query.Query;
import at.ac.tuwien.informatics.structure.query.Variable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestQuery {

    @Test
    public void testEqualQueries() {
        Query q1 = new Query(new HashSet<>(Collections.singletonList(new Variable("x"))),
                new HashSet<>(Collections.singletonList(new Conceptname("Pizza", new Variable("x")))));

        Query q2 = new Query(new HashSet<>(Collections.singletonList(new Variable("x"))),
                new HashSet<>(Collections.singletonList(new Conceptname("Pizza", new Variable("x")))));

        assertEquals(q1, q2);
    }

    @Test
    public void testUnequalQueries() {
        Query q1 = new Query(new HashSet<>(Collections.singletonList(new Variable("x"))),
                new HashSet<>(Collections.singletonList(new Conceptname("Pizza", new Variable("x")))));

        Query q2 = new Query(new HashSet<>(Collections.singletonList(new Variable("x"))),
                new HashSet<>(Collections.singletonList(new Conceptname("Pizza", new Variable("y")))));

        assertNotEquals(q1, q2);
    }

    @Test
    public void testQueryString() {
        Query q1 = new Query(new HashSet<>(Collections.singletonList(new Variable("x"))),
                new HashSet<>(Collections.singletonList(new Conceptname("Pizza", new Variable("x")))));
        assertEquals(q1.toString(), "q(x):-Pizza(x)");
    }
}
