package at.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.reformulation.Rewriter;
import at.ac.tuwien.informatics.reformulation.RewriterImpl;
import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.exception.NotOWL2QLException;
import at.ac.tuwien.informatics.structure.query.*;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestConceptname {

    @Test
    public void testEqualConceptnames() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university.owl");
        Conceptname c1 = new Conceptname(o.getClassMap().get("Professor"), new Variable("x"));
        Conceptname c2 = new Conceptname(o.getClassMap().get("Professor"), new Variable("x"));

        assertEquals(c1, c2);

        c1 = new Conceptname(o.getClassMap().get("Professor"), new UnboundVariable("x"));
        c2 = new Conceptname(o.getClassMap().get("Professor"), new UnboundVariable("y"));

        assertEquals(c1, c2);
    }

    @Test
    public void testUnequalConcepts() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university.owl");
        Conceptname c1 = new Conceptname(o.getClassMap().get("Professor"), new Variable("x"));
        Conceptname c2 = new Conceptname(o.getClassMap().get("Student"), new Variable("x"));

        assertNotEquals(c1, c2);

        c1 = new Conceptname(o.getClassMap().get("Professor"), new Variable("x"));
        c2 = new Conceptname(o.getClassMap().get("Professor"), new Variable("y"));

        assertNotEquals(c1, c2);

        c1 = new Conceptname(o.getClassMap().get("Professor"), new Variable("x"));
        c2 = new Conceptname(o.getClassMap().get("Professor"), new UnboundVariable("y"));

        assertNotEquals(c1, c2);
    }

    @Test
    public void testSetOfEqualConcepts() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university.owl");
        Conceptname c1 = new Conceptname(o.getClassMap().get("Professor"), new Variable("x"));
        Conceptname c2 = new Conceptname(o.getClassMap().get("Professor"), new Variable("x"));

        assertEquals(c1, c2);

        HashSet<Atom> set1 = new HashSet<>(Arrays.asList(c1, c2));
        HashSet<Atom> set2 = new HashSet<>();
        set2.add(c1);

        assertEquals(set2, set1);

        c1 = new Conceptname(o.getClassMap().get("Professor"), new UnboundVariable("x"));
        c2 = new Conceptname(o.getClassMap().get("Professor"), new UnboundVariable("y"));

        assertEquals(c1, c2);

        set1 = new HashSet<>(Arrays.asList(c1, c2));
        set2 = new HashSet<>();
        set2.add(c1);

        assertEquals(set2, set1);
    }

    @Test
    public void testString() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university.owl");
        Conceptname c1 = new Conceptname(o.getClassMap().get("Professor"), new Variable("x"));

        assertEquals("Professor(x)", c1.toString());
    }

    @Test
    public void testApplicable() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources/university.owl");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath());
        Conceptname a = new Conceptname(o.getClassMap().get("Professor"), new Variable("x"));

        Set<OWLAxiom> applicableAxioms = new HashSet<>();

        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (a.applicable(I)) {
                applicableAxioms.add(I);
            }
        }

        assertEquals(2, applicableAxioms.size());
    }

    @Test
    public void testApply() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources/university.owl");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath());
        Conceptname a = new Conceptname(o.getClassMap().get("Professor"), new Variable("x"));
        Rewriter rewriter = new RewriterImpl();

        Set<RewritableAtom> rewritten =
                new HashSet<>(Collections.singleton(new Conceptname(o.getClassMap().get("Professor"), new Variable("x"))));
        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (a.applicable(I)) {
                rewritten.add(a.apply(I, o, rewriter));
            }
        }
        assertEquals(3, rewritten.size());
        assertEquals(new HashSet<>(
                Arrays.asList(
                        new Conceptname(o.getClassMap().get("Professor"), new Variable("x")),
                        new Conceptname(o.getClassMap().get("Assistant_Prof"), new Variable("x")),
                        new Roles(new HashSet<>(Arrays.asList(o.getPropertyMap().get("teaches"),
                                o.getPropertyMap().get("taughtBy").getInverseProperty())),
                                new Variable("x"), new UnboundVariable("v1")))),
                rewritten);

        a = new Conceptname(o.getClassMap().get("Course"), new Variable("y"));
        rewritten = new HashSet<>(Collections.singleton(new Conceptname(o.getClassMap().get("Course"), new Variable("y"))));
        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (a.applicable(I)) {
                rewritten.add(a.apply(I, o, rewriter));
            }
        }
        assertEquals(2, rewritten.size());
        assertEquals(new HashSet<>(Arrays.asList(new Conceptname(o.getClassMap().get("Course"), new Variable("y")),
                new Roles(new HashSet<>(Arrays.asList(o.getPropertyMap().get("teaches").getInverseProperty(),
                        o.getPropertyMap().get("taughtBy"))),
                        new Variable("y"), new UnboundVariable("v2")))), rewritten);
    }
}
