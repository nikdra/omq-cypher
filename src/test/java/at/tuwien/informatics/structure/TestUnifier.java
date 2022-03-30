package at.tuwien.informatics.structure;

import at.ac.tuwien.informatics.structure.Substitution;
import at.ac.tuwien.informatics.structure.Unifier;
import at.ac.tuwien.informatics.structure.query.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUnifier {
/*
    @Test
    public void testUnifier() {
        List<Term> t1 = Collections.singletonList(new Variable("x"));
        List<Term> t2 = Collections.singletonList(new Variable("y"));

        Unifier unifier = new Unifier(t1, t2);
        assertEquals(new LinkedList<>(Collections
                        .singletonList(new Substitution(new Variable("x"), new Variable("y")))),
                unifier.getSubstitutions());

        t1 = Collections.singletonList(new Variable("y"));
        t2 = Collections.singletonList(new UnboundVariable("x"));

        unifier = new Unifier(t1, t2);

        assertEquals(new LinkedList<>(Collections
                        .singletonList(new Substitution(new UnboundVariable("x"), new Variable("y")))),
                unifier.getSubstitutions());

        t1 = Arrays.asList(new Variable("z1"), new Variable("z"));
        t2 = Arrays.asList(new Variable("x"), new Variable("z1"));

        unifier = new Unifier(t1, t2);

        assertEquals(new LinkedList<>(
                Arrays.asList(new Substitution(new Variable("z1"), new Variable("x")),
                        new Substitution(new Variable("z"), new Variable("x")))),
                unifier.getSubstitutions());

        t1 = Arrays.asList(new UnboundVariable("z1"), new Variable("z"));
        t2 = Arrays.asList(new Variable("x"), new UnboundVariable("y"));

        unifier = new Unifier(t1, t2);

        assertEquals(new LinkedList<>(
                        Arrays.asList(new Substitution(new UnboundVariable("z1"), new Variable("x")),
                                new Substitution(new UnboundVariable("y"), new Variable("z")))),
                unifier.getSubstitutions());

        t1 = Collections.singletonList(new Variable("x"));
        t2 = Collections.singletonList(new Variable("x"));

        unifier = new Unifier(t1, t2);

        assertEquals(new LinkedList<>(), unifier.getSubstitutions());

        t1 = Arrays.asList(new Variable("x"), new Variable("z"));
        t2 = Arrays.asList(new Variable("x"), new UnboundVariable("y"));

        unifier = new Unifier(t1, t2);

        assertEquals(new LinkedList<>(
                List.of(new Substitution(new UnboundVariable("y"), new Variable("z")))),
                unifier.getSubstitutions());
    }

    @Test
    public void testApplyUnifier() {
        RewritableQuery q;
        RewritableQuery qp;
        List<Variable> head;
        HashSet<RewritableAtom> body;
        Unifier unifier;

        head = new LinkedList<>(Arrays.asList(new Variable("x"), new Variable("y"),
                new Variable("z")));
        body = new HashSet<>(Arrays.asList(
                new SingleLengthSinglePathAtom(Collections.singleton("r"),
                        new Variable("x"), new Variable("y")),
                new SingleLengthSinglePathAtom(Collections.singleton("r"),
                        new Variable("y"), new Variable("z")),
                new Conceptname("A", new Variable("z"))
                ));
        q = new RewritableQuery(head, body);

        unifier = new Unifier(Arrays.asList(new Variable("x"), new Variable("y")),
                Arrays.asList(new Variable("y"), new Variable("z")));

        qp = new RewritableQuery(new LinkedList<>(Arrays.asList(new Variable("z"), new Variable("z"),
                new Variable("z"))),
                new HashSet<>(Arrays.asList(new SingleLengthSinglePathAtom(Collections.singleton("r"),
                        new Variable("z"), new Variable("z")),
                        new Conceptname("A", new Variable("z")))));
        // test unifier correctly applied
        assertEquals(qp, unifier.apply(q));
        // test no side effects on the initial query
        assertEquals(new RewritableQuery(new LinkedList<>(Arrays.asList(new Variable("x"), new Variable("y"),
                new Variable("z"))),
                new HashSet<>(Arrays.asList(
                        new SingleLengthSinglePathAtom(Collections.singleton("r"),
                                new Variable("x"), new Variable("y")),
                        new SingleLengthSinglePathAtom(Collections.singleton("r"),
                                new Variable("y"), new Variable("z")),
                        new Conceptname("A", new Variable("z"))
                ))), q);
    }

 */
}
