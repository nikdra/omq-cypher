package at.tuwien.informatics.translation;

import at.ac.tuwien.informatics.reformulation.RewriterImpl;
import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.exception.NotOWL2QLException;
import at.ac.tuwien.informatics.structure.query.*;
import at.ac.tuwien.informatics.translation.CypherTranslator;
import at.ac.tuwien.informatics.translation.Translator;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.util.*;


public class TestCypherTranslator {

    @Test
    public void testSingleQuery() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university2.ttl");
        Translator translator = new CypherTranslator();

        Set<RewritableQuery> queries = new HashSet<>(Collections.singleton(
                new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                        new HashSet<>(Arrays.asList(
                                new Roles(new HashSet<>(Collections.singleton(
                                        o.getPropertyMap().get("isSupervisedBy"))),
                                        new Variable("x"), new Variable("y")),
                                new Conceptname(o.getClassMap().get("Professor"), new Variable("y"))
                        )))
        ));

        System.out.println(translator.translate(new LinkedList<>(Collections.singleton(new Variable("x"))),
                queries));
    }

    @Test
    public void testSingleQueryAtom() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university2.ttl");
        Translator translator = new CypherTranslator();

        Set<RewritableQuery> queries = new HashSet<>(Collections.singleton(
                new RewritableQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                        new HashSet<>(Collections.singleton(
                                new Conceptname(o.getClassMap().get("Professor"), new Variable("y"))
                        )))
        ));

        System.out.println(translator.translate(new LinkedList<>(Collections.singleton(new Variable("x"))),
                queries));
    }

    @Test
    public void testCQUniversityTranslation() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university.owl");

        RewriterImpl rewriter = new RewriterImpl();
        InputQuery q;
        Set<RewritableQuery> Q;
        // q(x):-teaches(x,y), Course(y)
        q = new InputQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("teaches"))),
                                new Variable("x"), new Variable("y")),
                        new Conceptname(o.getClassMap().get("Course"), new Variable("y"))
                )));

        Q = rewriter.rewrite(q, o);

        Translator translator = new CypherTranslator();

        String res = translator.translate(q.getHead(), Q);

        System.out.println(res);
    }

    @Test
    public void testCQUniversity2Translation() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university2.ttl");

        RewriterImpl rewriter = new RewriterImpl();
        InputQuery q;
        Set<RewritableQuery> Q;
        // q(x):-supervisedBy(x,y), Professor(y)
        // this query needs to merge two roles atoms at some point such that
        // GradStudent(x) (and its subclasses) can be derived from the query.
        q = new InputQuery(new LinkedList<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Collections.singleton(
                                o.getPropertyMap().get("isSupervisedBy"))),
                                new Variable("x"), new Variable("y")),
                        new Conceptname(o.getClassMap().get("Professor"), new Variable("y"))
                )));

        Q = rewriter.rewrite(q, o);

        Translator translator = new CypherTranslator();

        String res = translator.translate(q.getHead(), Q);

        System.out.println(res);
    }

    @Test
    public void testCRPQWithConcatenationTwoTailArbitraryTranslation() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/paths1.owl");

        RewriterImpl rewriter = new RewriterImpl();
        InputQuery q;
        Set<RewritableQuery> Q;

        // q():-t*(x,z1),s*(z1,z2),r(z2,x)
        q = new InputQuery(new LinkedList<>(),
                new HashSet<>(Arrays.asList(
                        new Path(new LinkedList<>(Collections.singleton(
                                new ArbitraryLengthPathElement(
                                        new HashSet<>(Collections.singleton(o.getPropertyMap().get("t")))))),
                                new Variable("y"), new Variable("z1")),
                        new Path(new LinkedList<>(Collections.singleton(
                                new ArbitraryLengthPathElement(
                                        new HashSet<>(Collections.singleton(o.getPropertyMap().get("s")))))),
                                new Variable("z1"), new Variable("z2")),
                        new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))),
                                new Variable("z2"), new Variable("x"))
                )));

        Q = rewriter.rewrite(q, o);

        Translator translator = new CypherTranslator();

        String res = translator.translate(q.getHead(), Q);

        System.out.println(res);
    }

    @Test
    public void testCRPQWithConcatenationOneTailArbTranslation() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/paths1.owl");

        RewriterImpl rewriter = new RewriterImpl();
        InputQuery q;
        Set<RewritableQuery> Q;

        // q():-t*(x,z1),s*(z1,z2),r(z2,x)
        q = new InputQuery(new LinkedList<>(),
                new HashSet<>(Arrays.asList(
                        new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("t"))),
                                new Variable("y"), new Variable("z1")),
                        new Path(new LinkedList<>(Collections.singleton(
                                new ArbitraryLengthPathElement(
                                        new HashSet<>(Collections.singleton(o.getPropertyMap().get("s")))))),
                                new Variable("z1"), new Variable("z2")),
                        new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))),
                                new Variable("z2"), new Variable("x"))
                )));

        Q = rewriter.rewrite(q, o);

        Translator translator = new CypherTranslator();

        String res = translator.translate(q.getHead(), Q);

        System.out.println(res);
    }

    @Test
    public void testCRPQWithConcatenationNoDropTranslation() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/paths2.owl");

        RewriterImpl rewriter = new RewriterImpl();
        InputQuery q;
        Set<RewritableQuery> Q;

        // q():-A(x),r*(x,y),B(y)
        q = new InputQuery(new LinkedList<>(),
                new HashSet<>(Arrays.asList(
                        new Conceptname(o.getClassMap().get("A"), new Variable("x")),
                        new Path(new LinkedList<>(Collections.singleton(
                                new ArbitraryLengthPathElement(
                                        new HashSet<>(Collections.singleton(o.getPropertyMap().get("r")))))),
                                new Variable("x"), new Variable("y")),
                        new Conceptname(o.getClassMap().get("B"), new Variable("y"))
                )));

        Q = rewriter.rewrite(q, o);

        Translator translator = new CypherTranslator();

        String res = translator.translate(q.getHead(), Q);

        System.out.println(res);
    }
}
