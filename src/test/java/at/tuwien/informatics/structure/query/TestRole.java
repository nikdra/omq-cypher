package at.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.query.Atom;
import at.ac.tuwien.informatics.structure.query.Role;
import at.ac.tuwien.informatics.structure.query.SingleLengthSinglePathAtom;
import at.ac.tuwien.informatics.structure.query.Variable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestRole {

    @Test
    public void testEqualRoles() {
        Role r1 = new Role("r", new Variable("x"), new Variable("y"));
        Role r2 = new Role("r", new Variable("x"), new Variable("y"));

        assertEquals(r1, r2);
    }

    @Test
    public void testUnequalRoles() {
        Role r1 = new Role("r", new Variable("x"), new Variable("y"));
        Role r2 = new Role("t", new Variable("x"), new Variable("y"));

        assertNotEquals(r1, r2);

        r1 = new Role("r", new Variable("x"), new Variable("y"));
        r2 = new Role("r", new Variable("x"), new Variable("z"));

        assertNotEquals(r1, r2);
    }

    @Test
    public void testSetOfEqualRoles() {
        Role r1 = new Role("r", new Variable("x"), new Variable("y"));
        Role r2 = new Role("r", new Variable("x"), new Variable("y"));

        assertEquals(r1, r2);

        HashSet<Atom> set1 = new HashSet<>(Arrays.asList(r1, r2));
        HashSet<Atom> set2 = new HashSet<>(Collections.singleton(r1));

        assertEquals(set1, set2);
    }

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
}
