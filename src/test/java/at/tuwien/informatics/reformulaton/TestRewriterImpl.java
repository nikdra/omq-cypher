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
        RewritableQuery qpp = new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                                o.getPropertyMap().get("s"))), new Variable("x"), new Variable("v1")),
                        new ArbitraryLengthAtom(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))),
                                new Variable("v1"), new Variable("y")),
                        new ArbitraryLengthAtom(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                                o.getPropertyMap().get("s"))), new UnboundVariable("z"), new Variable("v2")),
                        new ArbitraryLengthAtom(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))),
                                new Variable("v2"), new Variable("v3")),
                        new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))),
                                new Variable("v3"), new Variable("y")),
                        new Conceptname(o.getClassMap().get("A"), new UnboundVariable("u"))
                )));
        assertEquals(qpp, qp);
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

    @Test
    public void testReduceSingleAtom() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");

        RewriterImpl rewriter = new RewriterImpl();
        RewritableQuery qp;
        Set<RewritableQuery> Q;

        // input query q(x,y):-s(x,y)
        // nothing to replace, mgu is empty
        RewritableQuery q = new RewritableQuery(new LinkedList<>(Arrays.asList(new Variable("x"),
                new Variable("y"))),
                new HashSet<>(Collections.singleton(new Roles(Collections.singleton(o.getPropertyMap().get("s")),
                        new Variable("x"), new Variable("y")))));

        Q = new HashSet<>(Collections.singleton(q));

        for (RewritableAtom a1: q.getBody()) {
            for (RewritableAtom a2 : q.getBody()) {
                Q.add(rewriter.tau(rewriter.reduce(q, a1, a2)));
            }
        }
        assertTrue(Q.contains(new RewritableQuery(new LinkedList<>(Arrays.asList(new Variable("x"),
                new Variable("y"))),
                new HashSet<>(Collections.singleton(new Roles(Collections.singleton(o.getPropertyMap().get("s")),
                        new Variable("x"), new Variable("y")))))));
        assertEquals(1, Q.size());
    }

    @Test
    public void testReduceInverseAtoms() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");

        RewriterImpl rewriter = new RewriterImpl();
        RewritableQuery qp;
        Set<RewritableQuery> Q;

        // input query q(x,y):-(s|r-)(x,y), (s-|r)(_,x)
        // unifier is both (s-|r)(y,x) and (s|r-)(x,y), which are semantically equivalent
        // but only one of them is kept (which one is irrelevant)
        RewritableQuery q = new RewritableQuery(
                new LinkedList<>(Arrays.asList(new Variable("x"), new Variable("y"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Arrays.asList(o.getPropertyMap().get("s"),
                                o.getPropertyMap().get("r").getInverseProperty())),
                                new Variable("x"), new Variable("y")),
                        new Roles(new HashSet<>(Arrays.asList(o.getPropertyMap().get("s").getInverseProperty(),
                                o.getPropertyMap().get("r"))),
                                new UnboundVariable("z"), new Variable("x")))));

        Q = new HashSet<>(Collections.singleton(q));

        for (RewritableAtom a1 : q.getBody()) {
            for (RewritableAtom a2 : q.getBody()) {
                Q.add(rewriter.tau(rewriter.reduce(q, a1, a2)));
            }
        }

        assertTrue(Q.contains(new RewritableQuery(
                new LinkedList<>(Arrays.asList(new Variable("x"), new Variable("y"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Arrays.asList(o.getPropertyMap().get("s"),
                                o.getPropertyMap().get("r").getInverseProperty())),
                                new Variable("x"), new Variable("y")),
                        new Roles(new HashSet<>(Arrays.asList(o.getPropertyMap().get("s").getInverseProperty(),
                                o.getPropertyMap().get("r"))),
                                new UnboundVariable("z"), new Variable("x")))))));

        assertTrue(Q.contains(new RewritableQuery(
                new LinkedList<>(Arrays.asList(new Variable("x"), new Variable("y"))),
                new HashSet<>(Collections.singleton(
                        new Roles(new HashSet<>(Arrays.asList(o.getPropertyMap().get("s"),
                                o.getPropertyMap().get("r").getInverseProperty())),
                                new Variable("x"), new Variable("y")))))));

        assertEquals(2, Q.size());
    }

    @Test
    public void testCQUniversity() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university.owl");

        RewriterImpl rewriter = new RewriterImpl();
        InputQuery q;
        Set<RewritableQuery> Q;

        q = new InputQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("teaches"))),
                                new Variable("x"), new Variable("y")),
                        new Conceptname(o.getClassMap().get("Course"), new Variable("y"))
        )));

        Q = rewriter.rewrite(q, o);

        assertEquals(5, Q.size());

        Set<RewritableQuery> Qp = new HashSet<>();
        Qp.add(new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Arrays.asList(o.getPropertyMap().get("teaches"),
                                o.getPropertyMap().get("taughtBy").getInverseProperty())),
                                new Variable("x"), new Variable("y")),
                        new Conceptname(o.getClassMap().get("Course"), new Variable("y"))
                ))));
        Qp.add(new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Arrays.asList(o.getPropertyMap().get("teaches"),
                                o.getPropertyMap().get("taughtBy").getInverseProperty())),
                                new Variable("x"), new Variable("y")),
                        new Roles(new HashSet<>(Arrays.asList(o.getPropertyMap().get("teaches").getInverseProperty(),
                                o.getPropertyMap().get("taughtBy"))),
                                new Variable("y"), new UnboundVariable("z"))
                ))));
        Qp.add(new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Collections.singleton(
                        new Roles(new HashSet<>(Arrays.asList(o.getPropertyMap().get("teaches").getInverseProperty(),
                                o.getPropertyMap().get("taughtBy"))),
                                new UnboundVariable("y"), new Variable("x")).getInverse()
                ))));
        Qp.add(new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Collections.singleton(
                        new Conceptname(o.getClassMap().get("Professor"), new Variable("x"))
                ))));
        Qp.add(new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Collections.singleton(
                        new Conceptname(o.getClassMap().get("Assistant_Prof"), new Variable("x"))
                ))));

        assertEquals(Qp, Q);
    }

    @Test
    public void testCQUniversity2() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university2.ttl");

        RewriterImpl rewriter = new RewriterImpl();
        InputQuery q;
        Set<RewritableQuery> Q;

        q = new InputQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Collections.singleton(
                                o.getPropertyMap().get("isSupervisedBy"))),
                                new Variable("x"), new Variable("y")),
                        new Conceptname(o.getClassMap().get("Professor"), new Variable("y"))
                )));

        Q = rewriter.rewrite(q, o);

        assertEquals(8, Q.size());

        Set<RewritableQuery> Qp = new HashSet<>();
        Qp.add(new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Arrays.asList(
                                o.getPropertyMap().get("gradStudentSupervisedBy"),
                                o.getPropertyMap().get("isSupervisedBy").getInverseProperty())),
                                new Variable("x"), new Variable("y")),
                        new Conceptname(o.getClassMap().get("Professor"), new Variable("y"))
                ))));
        Qp.add(new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Arrays.asList(
                                o.getPropertyMap().get("gradStudentSupervisedBy"),
                                o.getPropertyMap().get("isSupervisedBy").getInverseProperty())),
                                new Variable("x"), new Variable("y")),
                        new Conceptname(o.getClassMap().get("AssistantProfessor"), new Variable("y"))
                ))));
        Qp.add(new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Arrays.asList(
                                o.getPropertyMap().get("gradStudentSupervisedBy"),
                                o.getPropertyMap().get("isSupervisedBy").getInverseProperty())),
                                new Variable("x"), new Variable("y")),
                        new Conceptname(o.getClassMap().get("AssociateProfessor"), new Variable("y"))
                ))));
        Qp.add(new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Arrays.asList(
                                o.getPropertyMap().get("gradStudentSupervisedBy"),
                                o.getPropertyMap().get("isSupervisedBy").getInverseProperty())),
                                new Variable("x"), new Variable("y")),
                        new Conceptname(o.getClassMap().get("FullProfessor"), new Variable("y"))
                ))));
        Qp.add(new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Arrays.asList(
                                o.getPropertyMap().get("gradStudentSupervisedBy"),
                                o.getPropertyMap().get("isSupervisedBy").getInverseProperty())),
                                new Variable("x"), new Variable("y")),
                        new Roles(new HashSet<>(Arrays.asList(
                                o.getPropertyMap().get("gradStudentSupervisedBy").getInverseProperty(),
                                o.getPropertyMap().get("isSupervisedBy"))),
                                new Variable("y"), new UnboundVariable("z"))
                ))));
        Qp.add(new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Collections.singleton(
                        new Roles(new HashSet<>(Arrays.asList(
                                o.getPropertyMap().get("gradStudentSupervisedBy").getInverseProperty(),
                                o.getPropertyMap().get("isSupervisedBy"))),
                                new Variable("y"), new Variable("x"))
                ))));
        Qp.add(new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Collections.singleton(
                        new Conceptname(o.getClassMap().get("GraduateStudent"), new Variable("x"))
                ))));
        Qp.add(new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Collections.singleton(
                        new Conceptname(o.getClassMap().get("PhDStudent"), new Variable("x"))
                ))));

        assertEquals(Qp, Q);
    }
}
