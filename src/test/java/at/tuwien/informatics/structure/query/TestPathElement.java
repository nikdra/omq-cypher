package at.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.exception.NotOWL2QLException;
import at.ac.tuwien.informatics.structure.query.ArbitraryLengthPathElement;
import at.ac.tuwien.informatics.structure.query.PathElement;
import at.ac.tuwien.informatics.structure.query.SingleLengthPathElement;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestPathElement {

    @Test
    public void testEqualPathElements() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");
        PathElement p1 = new SingleLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))));
        PathElement p2 = new SingleLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))));

        assertEquals(p1, p2);

        p1 = new SingleLengthPathElement(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("s"))));
        p2 = new SingleLengthPathElement(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("s"))));

        assertEquals(p1, p2);

        p1 = new ArbitraryLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))));
        p2 = new ArbitraryLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))));

        assertEquals(p1, p2);

        p1 = new ArbitraryLengthPathElement(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("s"))));
        p2 = new ArbitraryLengthPathElement(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("s"))));

        assertEquals(p1, p2);
    }

    @Test
    public void testUnequalElements() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");
        PathElement p1 = new SingleLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))));
        PathElement p2 = new SingleLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("s"))));

        assertNotEquals(p1, p2);

        p1 = new SingleLengthPathElement(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("s"), o.getPropertyMap().get("t"))));
        p2 = new SingleLengthPathElement(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("s"))));

        assertNotEquals(p1, p2);

        p1 = new ArbitraryLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))));
        p2 = new ArbitraryLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("s"))));

        assertNotEquals(p1, p2);

        p1 = new ArbitraryLengthPathElement(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("s"), o.getPropertyMap().get("t"))));
        p2 = new ArbitraryLengthPathElement(new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("s"))));

        assertNotEquals(p1, p2);

        p1 = new SingleLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))));
        p2 = new ArbitraryLengthPathElement(new HashSet<>(Collections.singleton(o.getPropertyMap().get("r"))));

        assertNotEquals(p1, p2);
    }
    /*
    @Test
    public void testSaturate() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources/subroles.owl");

        PathElement p1 = new SingleLengthPathElement(new HashSet<>(Arrays.asList("r", "s", "t")));
        PathElement p2 = new SingleLengthPathElement(new HashSet<>(Collections.singleton("s")));
        p2.saturate(new Ontology(resourcesDirectory.getAbsolutePath()));

        assertEquals(p1, p2);
    }
    */
}
