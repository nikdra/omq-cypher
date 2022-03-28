package at.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.query.Conceptname;
import at.ac.tuwien.informatics.structure.query.InputQuery;
import at.ac.tuwien.informatics.structure.query.Variable;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestInputQuery {

    @Test
    public void testEqualQueries() {
        InputQuery q1 = new InputQuery(new HashSet<>(Collections.singletonList(new Variable("x"))),
                new HashSet<>(Collections.singletonList(new Conceptname("Pizza", new Variable("x")))));

        InputQuery q2 = new InputQuery(new HashSet<>(Collections.singletonList(new Variable("x"))),
                new HashSet<>(Collections.singletonList(new Conceptname("Pizza", new Variable("x")))));

        assertEquals(q1, q2);
    }

    @Test
    public void testUnequalQueries() {
        InputQuery q1 = new InputQuery(new HashSet<>(Collections.singletonList(new Variable("x"))),
                new HashSet<>(Collections.singletonList(new Conceptname("Pizza", new Variable("x")))));

        InputQuery q2 = new InputQuery(new HashSet<>(Collections.singletonList(new Variable("x"))),
                new HashSet<>(Collections.singletonList(new Conceptname("Pizza", new Variable("y")))));

        assertNotEquals(q1, q2);
    }

    @Test
    public void testQueryString() {
        InputQuery q1 = new InputQuery(new HashSet<>(Collections.singletonList(new Variable("x"))),
                new HashSet<>(Collections.singletonList(new Conceptname("Pizza", new Variable("x")))));
        assertEquals(q1.toString(), "q(x):-Pizza(x)");
    }
}
