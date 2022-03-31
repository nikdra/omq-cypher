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
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

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
    /*
    @Test
    public void testToSingleLengthSinglePathAtom() {
        Role r = new Role("r", new Variable("x"), new Variable("y"));
        SingleLengthSinglePathAtom rp = r.toSingleLengthSinglePathAtom();
        SingleLengthSinglePathAtom rpp = new SingleLengthSinglePathAtom(new HashSet<>(Collections.singleton("r")),
                new Variable("x"), new Variable("y"));

        assertEquals(rp, rpp);

        rpp = new SingleLengthSinglePathAtom(new HashSet<>(Collections.singleton("r")), new Variable("x"),
                new Variable("x"));

        assertNotEquals(rp, rpp);
    }

     */

}
