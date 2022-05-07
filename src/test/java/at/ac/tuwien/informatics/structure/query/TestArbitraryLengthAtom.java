package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.exception.NotOWL2QLException;
import at.ac.tuwien.informatics.structure.query.ArbitraryLengthAtom;
import at.ac.tuwien.informatics.structure.query.RewritableAtom;
import at.ac.tuwien.informatics.structure.query.Variable;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestArbitraryLengthAtom {

    @Test
    public void testEqualArbitraryLengthAtoms() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");
        ArbitraryLengthAtom p1 = new ArbitraryLengthAtom(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("s"))),
                new Variable("x"), new Variable("y"));
        ArbitraryLengthAtom p2 = new ArbitraryLengthAtom(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("s"))),
                new Variable("x"), new Variable("y"));

        assertEquals(p1, p2);

        p1 = new ArbitraryLengthAtom(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))),
                new Variable("x"), new Variable("y"));
        p2 = new ArbitraryLengthAtom(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))),
                new Variable("x"), new Variable("y"));

        assertEquals(p1, p2);
    }

    @Test
    public void testUnequalArbitraryLengthAtoms() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");
        ArbitraryLengthAtom p1 = new ArbitraryLengthAtom(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("s"))),
                new Variable("x"), new Variable("y"));
        ArbitraryLengthAtom p2 = new ArbitraryLengthAtom(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("t"), o.getPropertyMap().get("s"))),
                new Variable("x"), new Variable("y"));

        assertNotEquals(p1, p2);

        p1 = new ArbitraryLengthAtom(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))),
                new Variable("x"), new Variable("y"));
        p2 = new ArbitraryLengthAtom(new HashSet<>(Collections.singleton(o.getPropertyMap().get("s"))),
                new Variable("x"), new Variable("y"));

        assertNotEquals(p1, p2);
    }

    @Test
    public void testSetOfEqualArbitraryLengthAtoms() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");

        ArbitraryLengthAtom p1 = new ArbitraryLengthAtom(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("s"))),
                new Variable("x"), new Variable("y"));
        ArbitraryLengthAtom p2 = new ArbitraryLengthAtom(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("s"))),
                new Variable("x"), new Variable("y"));

        assertEquals(p1, p2);

        Set<RewritableAtom> set1 = new HashSet<>(Arrays.asList(p1, p2));
        Set<RewritableAtom> set2 = new HashSet<>(Collections.singleton(p1));

        assertEquals(set1, set2);
    }

    @Test
    public void testSetOfUnequalSinglePathAtoms () throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");
        ArbitraryLengthAtom p1 = new ArbitraryLengthAtom(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("s"))),
                new Variable("x"), new Variable("y"));
        ArbitraryLengthAtom p2 = new ArbitraryLengthAtom(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("t"), o.getPropertyMap().get("s"))),
                new Variable("x"), new Variable("z"));

        assertNotEquals(p1, p2);

        HashSet<RewritableAtom> set1 = new HashSet<>(Arrays.asList(p1, p2));
        HashSet<RewritableAtom> set2 = new HashSet<>(Collections.singleton(p1));

        assertNotEquals(set1, set2);
        assertEquals(set1.size(), 2);
    }
 
}
