package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.exception.NotOWL2QLException;
import at.ac.tuwien.informatics.structure.query.Conceptname;
import at.ac.tuwien.informatics.structure.query.InputQuery;
import at.ac.tuwien.informatics.structure.query.Variable;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestInputQuery {

    @Test
    public void testEqualQueries() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university.owl");
        InputQuery q1 = new InputQuery(new LinkedList<>(Collections.singletonList(new Variable("x"))),
                new HashSet<>(Collections.singleton(new Conceptname(o.getClassMap().get("Professor"),
                        new Variable("x")))));

        InputQuery q2 = new InputQuery(new LinkedList<>(Collections.singletonList(new Variable("x"))),
                new HashSet<>(Collections.singletonList(new Conceptname(o.getClassMap().get("Professor"),
                        new Variable("x")))));

        assertEquals(q1, q2);
    }

    @Test
    public void testUnequalQueries() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university.owl");
        InputQuery q1 = new InputQuery(new LinkedList<>(Collections.singletonList(new Variable("x"))),
                new HashSet<>(Collections.singletonList(new Conceptname(o.getClassMap().get("Professor"),
                        new Variable("x")))));

        InputQuery q2 = new InputQuery(new LinkedList<>(Collections.singletonList(new Variable("x"))),
                new HashSet<>(Collections.singletonList(new Conceptname(o.getClassMap().get("Professor"),
                        new Variable("y")))));

        assertNotEquals(q1, q2);
    }

    @Test
    public void testQueryString() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university.owl");
        InputQuery q1 = new InputQuery(new LinkedList<>(Collections.singletonList(new Variable("x"))),
                new HashSet<>(Collections.singletonList(new Conceptname(o.getClassMap().get("Professor"),
                        new Variable("x")))));
        assertEquals(q1.toString(), "q(x):-Professor(x)");
    }


}
