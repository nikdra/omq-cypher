package at.tuwien.informatics.reformulaton;

import at.ac.tuwien.informatics.reformulation.RewriterImpl;
import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.exception.NotOWL2QLException;
import at.ac.tuwien.informatics.structure.query.*;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRewriterImpl {

    @Test
    public void testSaturatePaths() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");

        List<Variable> head = new LinkedList<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();
        List<PathElement> elements = new LinkedList<>();
        elements.add(new SingleLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("s")))));
        elements.add(new ArbitraryLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r")))));
        body.add(new Path(elements, new Variable("x"), new Variable("y")));

        // create query
        InputQuery q = new InputQuery(head, body);

        // call rewriter
        RewriterImpl rewriter = new RewriterImpl();
        RewritableQuery qp = rewriter.saturatePaths(q, o);

        // check if results are as expected
        RewritableQuery qpp = new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"), o.getPropertyMap().get("s"),
                                o.getPropertyMap().get("t"))),
                                new Variable("x"), new Variable("v1")),
                        new ArbitraryLengthAtom(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                                o.getPropertyMap().get("t"))),
                                new Variable("v1"), new Variable("y"))
                )));

        assertEquals(qp, qpp);
    }

    @Test
    public void testSaturateTwoPaths() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");

        List<Variable> head = new LinkedList<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();
        List<PathElement> elements = new LinkedList<>();
        elements.add(new SingleLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("s")))));
        elements.add(new ArbitraryLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r")))));
        body.add(new Path(elements, new Variable("x"), new Variable("y")));

        elements = new LinkedList<>();
        elements.add(new ArbitraryLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("s")))));
        elements.add(new ArbitraryLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("t")))));
        elements.add(new SingleLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("t")))));
        body.add(new Path(elements, new Variable("z"), new Variable("y")));

        // create query
        InputQuery q = new InputQuery(head, body);

        // call rewriter
        RewriterImpl rewriter = new RewriterImpl();
        RewritableQuery qp = rewriter.saturatePaths(q, o);

        // check if results are as expected
        RewritableQuery qpp = new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"), o.getPropertyMap().get("s"),
                                o.getPropertyMap().get("t"))),
                                new Variable("x"), new Variable("v3")),
                        new ArbitraryLengthAtom(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                                o.getPropertyMap().get("t"))),
                                new Variable("v3"), new Variable("y")),
                        new ArbitraryLengthAtom(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                                o.getPropertyMap().get("s"), o.getPropertyMap().get("t"))),
                                new Variable("z"), new Variable("v1")),
                        new ArbitraryLengthAtom(new HashSet<>(Collections.singleton(o.getPropertyMap().get("t"))),
                                new Variable("v1"), new Variable("v2")),
                        new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("t"))),
                                new Variable("v2"), new Variable("y"))
                )));

        assertEquals(qpp, qp);
    }

    @Test
    public void testTau() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");
        // input query q(x) :- (r|s)(x, v1), r*(v1, y), (r|s)*(z,v2), r*(v2, v3), r(v3, y), A(u).
        RewritableQuery q = new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                                o.getPropertyMap().get("s"))), new Variable("x"), new Variable("v1")),
                        new ArbitraryLengthAtom(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))),
                                new Variable("v1"), new Variable("y")),
                        new ArbitraryLengthAtom(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                                o.getPropertyMap().get("s"))), new Variable("z"), new Variable("v2")),
                        new ArbitraryLengthAtom(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))),
                                new Variable("v2"), new Variable("v3")),
                        new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))),
                                new Variable("v3"), new Variable("y")),
                        new Conceptname(o.getClassMap().get("A"), new Variable("u"))
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

    @Test
    public void testTauBoolean() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");
        // input query q():-s(x,y)
        RewritableQuery q = new RewritableQuery(new LinkedList<>(),
                new HashSet<>(Collections.singleton(new Roles(Collections.singleton(o.getPropertyMap().get("s")),
                        new Variable("x"), new Variable("y")))));

        // call rewriter
        RewriterImpl rewriter = new RewriterImpl();
        RewritableQuery qp = rewriter.tau(q);

        assertEquals("q():-s(_,_)", qp.toString());

        // input query q():-s*(x,y)
        q = new RewritableQuery(new LinkedList<>(),
                new HashSet<>(Collections.singleton(
                        new ArbitraryLengthAtom(Collections.singleton(o.getPropertyMap().get("s")),
                        new Variable("x"), new Variable("y")))));

        // call rewriter
        qp = rewriter.tau(q);

        assertEquals("q():-s*(_,_)", qp.toString());
    }
/*
    @Test
    public void testReduce() {
        RewriterImpl rewriter = new RewriterImpl();
        RewritableQuery qp;
        Set<RewritableQuery> Q;

        // input query q(x,y):-s(x,y)
        // nothing to replace, mgu is empty
        RewritableQuery q = new RewritableQuery(new LinkedList<>(Arrays.asList(new Variable("x"),
                new Variable("y"))),
                new HashSet<>(Collections.singleton(new SingleLengthSinglePathAtom(Collections.singleton("s"),
                        new Variable("x"), new Variable("y")))));

        Q = new HashSet<>(Collections.singleton(q));

        for (RewritableAtom a1: q.getBody()) {
            for (RewritableAtom a2 : q.getBody()) {
                Q.add(rewriter.tau(rewriter.reduce(q, a1, a2)));
            }
        }
        assertTrue(Q.contains(new RewritableQuery(new LinkedList<>(Arrays.asList(new Variable("x"),
                new Variable("y"))),
                new HashSet<>(Collections.singleton(new SingleLengthSinglePathAtom(Collections.singleton("s"),
                        new Variable("x"), new Variable("y")))))));
        assertEquals(1, Q.size());
    }
     */
}
