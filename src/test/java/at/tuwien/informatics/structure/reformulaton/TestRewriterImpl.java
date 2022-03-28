package at.tuwien.informatics.structure.reformulaton;

import at.ac.tuwien.informatics.reformulation.RewriterImpl;
import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.exception.NotOWL2QLException;
import at.ac.tuwien.informatics.structure.query.*;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRewriterImpl {

    @Test
    public void testSaturatePaths() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources/subroles.owl");

        List<Variable> head = new LinkedList<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();
        List<PathElement> elements = new LinkedList<>();
        elements.add(new SingleLengthPathElement(new HashSet<>(Collections.singleton("s"))));
        elements.add(new ArbitraryLengthPathElement(new HashSet<>(Collections.singleton("r"))));
        body.add(new Path(elements, new Variable("x"), new Variable("y")));

        // create query
        InputQuery q = new InputQuery(head, body);

        // call rewriter
        RewriterImpl rewriter = new RewriterImpl();
        RewritableQuery qp = rewriter.saturatePaths(q, new Ontology(resourcesDirectory.getAbsolutePath()));

        // check if results are as expected
        RewritableQuery qpp = new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new SingleLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "s", "t")),
                                new Variable("x"), new Variable("v1")),
                        new ArbitraryLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "t")),
                                new Variable("v1"), new Variable("y"))
                )));

        assertEquals(qp, qpp);
    }

    @Test
    public void testSaturateTwoPaths() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources/subroles.owl");

        List<Variable> head = new LinkedList<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();
        List<PathElement> elements = new LinkedList<>();
        elements.add(new SingleLengthPathElement(new HashSet<>(Collections.singleton("s"))));
        elements.add(new ArbitraryLengthPathElement(new HashSet<>(Collections.singleton("r"))));
        body.add(new Path(elements, new Variable("x"), new Variable("y")));

        elements = new LinkedList<>();
        elements.add(new ArbitraryLengthPathElement(new HashSet<>(Collections.singleton("s"))));
        elements.add(new ArbitraryLengthPathElement(new HashSet<>(Collections.singleton("t"))));
        elements.add(new SingleLengthPathElement(new HashSet<>(Collections.singleton("t"))));
        body.add(new Path(elements, new Variable("z"), new Variable("y")));

        // create query
        InputQuery q = new InputQuery(head, body);

        // call rewriter
        RewriterImpl rewriter = new RewriterImpl();
        RewritableQuery qp = rewriter.saturatePaths(q, new Ontology(resourcesDirectory.getAbsolutePath()));

        // check if results are as expected
        RewritableQuery qpp = new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new SingleLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "s", "t")),
                                new Variable("x"), new Variable("v3")),
                        new ArbitraryLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "t")),
                                new Variable("v3"), new Variable("y")),
                        new ArbitraryLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "s", "t")),
                                new Variable("z"), new Variable("v1")),
                        new ArbitraryLengthSinglePathAtom(new HashSet<>(Collections.singleton("t")),
                                new Variable("v1"), new Variable("v2")),
                        new SingleLengthSinglePathAtom(new HashSet<>(Collections.singleton("t")),
                                new Variable("v2"), new Variable("y"))
                )));

        assertEquals(qpp, qp);
    }

    @Test
    public void testTau() {
        // input query q(x) :- (r|s)(x, v1), r*(v1, y), (r|s)*(z,v2), r*(v2, v3), r(v3, y), A(u).
        RewritableQuery q = new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new SingleLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "s")),
                                new Variable("x"), new Variable("v1")),
                        new ArbitraryLengthSinglePathAtom(new HashSet<>(Collections.singleton("r")),
                                new Variable("v1"), new Variable("y")),
                        new ArbitraryLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "s")),
                                new Variable("z"), new Variable("v2")),
                        new ArbitraryLengthSinglePathAtom(new HashSet<>(Collections.singleton("r")),
                                new Variable("v2"), new Variable("v3")),
                        new SingleLengthSinglePathAtom(new HashSet<>(Collections.singleton("r")),
                                new Variable("v3"), new Variable("y")),
                        new Conceptname("A", new Variable("u"))
                )));

        // call rewriter
        RewriterImpl rewriter = new RewriterImpl();
        RewritableQuery qp = rewriter.tau(q);

        // should become q(x):-(r|s)(x, v1),r*(v1, y),(r|s)*(_,v2),r*(v2, v3),r(v3, y), A(_).
        // we can't check equality because all unbound variables are different unless they're the same object
        // (by definition). However, we can check the query strings.
        System.out.println(q);
        System.out.println(qp);
    }
}
