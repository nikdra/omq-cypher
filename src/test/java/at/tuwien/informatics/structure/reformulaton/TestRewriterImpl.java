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

        HashSet<Variable> head = new HashSet<>();
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
        RewritableQuery qpp = new RewritableQuery(new HashSet<>(Collections.singleton(new Variable("x"))),
                new HashSet<>(Arrays.asList(
                        new SingleLengthSinglePathAtom(new HashSet<>(Arrays.asList("r", "s")),
                                new Variable("x"), new Variable("v1")),
                        new ArbitraryLengthSinglePathAtom(new HashSet<>(Collections.singleton("r")),
                                new Variable("v1"), new Variable("y"))
                )));

        assertEquals(qp, qpp);
    }
}
