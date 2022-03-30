package at.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.reformulation.Rewriter;
import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.exception.NotOWL2QLException;
import at.ac.tuwien.informatics.structure.query.*;
import com.google.errorprone.annotations.Var;
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

public class TestSinglePathAtom {
/*
    @Test
    public void testEqualSinglePathAtoms() {
        SinglePathAtom p1 = new SingleLengthSinglePathAtom(new HashSet<>(Collections.singleton("r")),
                new Variable("x"), new Variable("y"));
        SinglePathAtom p2 = new SingleLengthSinglePathAtom(new HashSet<>(Collections.singleton("r")),
                new Variable("x"), new Variable("y"));

        assertEquals(p1, p2);

        p1 = new SingleLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "s")),
                new Variable("x"), new Variable("y"));
        p2 = new SingleLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "s")),
                new Variable("x"), new Variable("y"));

        assertEquals(p1, p2);

        p1 = new ArbitraryLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "s")),
                new Variable("x"), new Variable("y"));
        p2 = new ArbitraryLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "s")),
                new Variable("x"), new Variable("y"));

        assertEquals(p1, p2);

        p1 = new ArbitraryLengthSinglePathAtom(new HashSet<>(Collections.singleton("r")),
                new Variable("x"), new Variable("y"));
        p2 = new ArbitraryLengthSinglePathAtom(new HashSet<>(Collections.singleton("r")),
                new Variable("x"), new Variable("y"));

        assertEquals(p1, p2);
    }

    @Test
    public void testUnequalSinglePathAtoms() {
        SinglePathAtom p1 = new SingleLengthSinglePathAtom(new HashSet<>(Collections.singleton("r")),
                new Variable("x"), new Variable("y"));
        SinglePathAtom p2 = new SingleLengthSinglePathAtom(new HashSet<>(Collections.singleton("s")),
                new Variable("x"), new Variable("y"));

        assertNotEquals(p1, p2);

        p1 = new SingleLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "t", "s")),
                new Variable("x"), new Variable("y"));
        p2 = new SingleLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "s")),
                new Variable("x"), new Variable("y"));

        assertNotEquals(p1, p2);

        p1 = new ArbitraryLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "s")),
                new Variable("x"), new Variable("y"));
        p2 = new ArbitraryLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "t", "s")),
                new Variable("x"), new Variable("y"));

        assertNotEquals(p1, p2);

        p1 = new ArbitraryLengthSinglePathAtom(new HashSet<>(Collections.singleton("r")),
                new Variable("x"), new Variable("y"));
        p2 = new ArbitraryLengthSinglePathAtom(new HashSet<>(Collections.singleton("s")),
                new Variable("x"), new Variable("y"));

        assertNotEquals(p1, p2);

        p1 = new SingleLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "t", "s")),
                new Variable("x"), new Variable("y"));
        p2 = new ArbitraryLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "t", "s")),
                new Variable("x"), new Variable("y"));

        assertNotEquals(p1, p2);
    }

    @Test
    public void testSetOfEqualSinglePathAtoms() {
        SinglePathAtom p1 = new SingleLengthSinglePathAtom(new HashSet<>(Collections.singleton("r")),
                new Variable("x"), new Variable("y"));
        SinglePathAtom p2 = new SingleLengthSinglePathAtom(new HashSet<>(Collections.singleton("r")),
                new Variable("x"), new Variable("y"));

        assertEquals(p1, p2);

        HashSet<RewritableAtom> set1 = new HashSet<>(Arrays.asList(p1, p2));
        HashSet<RewritableAtom> set2 = new HashSet<>(Collections.singleton(p1));

        assertEquals(set1, set2);

        p1 = new ArbitraryLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "s")),
                new Variable("x"), new Variable("y"));
        p2 = new ArbitraryLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "s")),
                new Variable("x"), new Variable("y"));

        assertEquals(p1, p2);

        set1 = new HashSet<>(Arrays.asList(p1, p2));
        set2 = new HashSet<>(Collections.singleton(p1));

        assertEquals(set1, set2);
    }

    @Test
    public void testSetOfUnequalSinglePathAtoms () {
        SinglePathAtom p1 = new SingleLengthSinglePathAtom(new HashSet<>(Collections.singleton("r")),
                new Variable("x"), new Variable("y"));
        SinglePathAtom p2 = new ArbitraryLengthSinglePathAtom(new HashSet<>(Collections.singleton("r")),
                new Variable("x"), new Variable("y"));

        assertNotEquals(p1, p2);

        HashSet<RewritableAtom> set1 = new HashSet<>(Arrays.asList(p1, p2));
        HashSet<RewritableAtom> set2 = new HashSet<>(Collections.singleton(p1));

        assertNotEquals(set1, set2);
        assertEquals(set1.size(), 2);
    }

    @Test
    public void testApplicable() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources/university.owl");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath());
        SinglePathAtom p = new SingleLengthSinglePathAtom(Collections.singleton("teaches"), new Variable("x"),
                new UnboundVariable("y"));

        Set<OWLAxiom> applicableAxioms = new HashSet<>();

        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (p.applicable(o, I)) {
                applicableAxioms.add(I);
            }
        }

        assertEquals(1, applicableAxioms.size());

        // load ontology
        resourcesDirectory = new File("src/test/resources/university2.ttl");
        o = new Ontology(resourcesDirectory.getAbsolutePath());

        applicableAxioms = new HashSet<>();

        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (p.applicable(o, I)) {
                applicableAxioms.add(I);
            }
        }
        // inverses!
        assertEquals(3, applicableAxioms.size());
    }
 */
}
