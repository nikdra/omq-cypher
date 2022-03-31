package at.tuwien.informatics.structure.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.exception.NotOWL2QLException;
import at.ac.tuwien.informatics.structure.query.Atom;
import at.ac.tuwien.informatics.structure.query.Roles;
import at.ac.tuwien.informatics.structure.query.UnboundVariable;
import at.ac.tuwien.informatics.structure.query.Variable;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TestRoles {

    @Test
    public void testEqualRoles() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");
        Roles r1 = new Roles(Collections.singleton(o.getPropertyMap().get("r")),
                new Variable("x"), new Variable("y"));
        Roles r2 = new Roles(Collections.singleton(o.getPropertyMap().get("r")),
                new Variable("x"), new Variable("y"));

        assertEquals(r1, r2);
    }

    @Test
    public void testUnequalRoles() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");
        Roles r1 = new Roles(Collections.singleton(o.getPropertyMap().get("r")),
                new Variable("x"), new Variable("y"));
        Roles r2 = new Roles(Collections.singleton(o.getPropertyMap().get("t")),
                new Variable("x"), new Variable("y"));

        assertNotEquals(r1, r2);

        r1 = new Roles(Collections.singleton(o.getPropertyMap().get("r")),
                new Variable("x"), new Variable("y"));
        r2 = new Roles(Collections.singleton(o.getPropertyMap().get("r")),
                new Variable("x"), new Variable("z"));

        assertNotEquals(r1, r2);

        r1 = new Roles(Collections.singleton(o.getPropertyMap().get("r")),
                new Variable("x"), new UnboundVariable("z"));
        r2 = new Roles(Collections.singleton(o.getPropertyMap().get("r")),
                new Variable("x"), new UnboundVariable("y"));

        assertNotEquals(r1, r2);
    }

    @Test
    public void testSetOfEqualRoles() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");
        Roles r1 = new Roles(Collections.singleton(o.getPropertyMap().get("r")),
                new Variable("x"), new Variable("y"));
        Roles r2 = new Roles(Collections.singleton(o.getPropertyMap().get("r")),
                new Variable("x"), new Variable("y"));

        assertEquals(r1, r2);

        HashSet<Atom> set1 = new HashSet<>(Arrays.asList(r1, r2));
        HashSet<Atom> set2 = new HashSet<>(Collections.singleton(r1));

        assertEquals(set1, set2);
    }

    @Test
    public void testSaturate() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");
        Roles r1 = new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("s"))),
                new Variable("x"), new Variable("y"));
        r1.saturate(o);
        Set<OWLObjectPropertyExpression> subroles = new HashSet<>(Arrays.asList(o.getPropertyMap().get("r"),
                o.getPropertyMap().get("s"), o.getPropertyMap().get("t")));

        assertEquals(subroles, r1.getRoles());

        r1 = new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("s").getInverseProperty())),
                new Variable("x"), new Variable("y"));
        r1.saturate(o);
        subroles = new HashSet<>(Arrays.asList(o.getPropertyMap().get("r").getInverseProperty(),
                o.getPropertyMap().get("s").getInverseProperty(), o.getPropertyMap().get("t").getInverseProperty()));

        assertEquals(subroles, r1.getRoles());

        o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university2.ttl");
        r1 = new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("teaches"))),
                new Variable("x"), new Variable("y"));

        r1.saturate(o);

        subroles = new HashSet<>(Arrays.asList(o.getPropertyMap().get("teaches"),
                o.getPropertyMap().get("isTaughtBy").getInverseProperty(), o.getPropertyMap().get("givesLab"),
                o.getPropertyMap().get("givesLecture")));

        assertEquals(subroles, r1.getRoles());

        r1 = new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("teaches").getInverseProperty())),
                new Variable("x"), new Variable("y"));

        r1.saturate(o);

        subroles = new HashSet<>(Arrays.asList(o.getPropertyMap().get("teaches").getInverseProperty(),
                o.getPropertyMap().get("isTaughtBy"), o.getPropertyMap().get("givesLab").getInverseProperty(),
                o.getPropertyMap().get("givesLecture").getInverseProperty()));

        assertEquals(subroles, r1.getRoles());
    }
/*
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