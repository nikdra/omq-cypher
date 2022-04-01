package at.tuwien.informatics.structure.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import at.ac.tuwien.informatics.reformulation.Rewriter;
import at.ac.tuwien.informatics.reformulation.RewriterImpl;
import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.exception.NotOWL2QLException;
import at.ac.tuwien.informatics.structure.query.*;
import com.google.errorprone.annotations.Var;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.util.*;

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

        r1 = new Roles(Collections.singleton(o.getPropertyMap().get("r")),
                new Variable("x"), new UnboundVariable("z"));
        r2 = new Roles(Collections.singleton(o.getPropertyMap().get("r")),
                new Variable("x"), new UnboundVariable("y"));

        assertEquals(r1, r2);

        r1 = new Roles(Collections.singleton(o.getPropertyMap().get("r").getInverseProperty()),
                new UnboundVariable("z"), new Variable("x"));
        r2 = new Roles(Collections.singleton(o.getPropertyMap().get("r")),
                new Variable("x"), new UnboundVariable("y"));

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

        assertEquals(set2, set1);

        r1 = new Roles(Collections.singleton(o.getPropertyMap().get("r")),
                new Variable("x"), new UnboundVariable("z"));
        r2 = new Roles(Collections.singleton(o.getPropertyMap().get("r")),
                new Variable("x"), new UnboundVariable("y"));

        assertEquals(r1, r2);

        set1 = new HashSet<>(Arrays.asList(r1, r2));
        set2 = new HashSet<>(Collections.singleton(r1));

        assertEquals(set2, set1);

        r1 = new Roles(Collections.singleton(o.getPropertyMap().get("r").getInverseProperty()),
                new UnboundVariable("z"), new Variable("x"));
        r2 = new Roles(Collections.singleton(o.getPropertyMap().get("r")),
                new Variable("x"), new UnboundVariable("y"));

        assertEquals(r1, r2);

        set1 = new HashSet<>(Arrays.asList(r1, r2));
        set2 = new HashSet<>(Collections.singleton(r1));

        assertEquals(set2, set1);
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
    @Test
    public void testApplicable() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources/university.owl");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath());
        Roles p = new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("teaches"))),
                new Variable("x"),
                new UnboundVariable("y"));

        Set<OWLAxiom> applicableAxioms = new HashSet<>();

        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (p.applicable(I)) {
                applicableAxioms.add(I);
            }
        }

        assertEquals(1, applicableAxioms.size());

        // load ontology
        resourcesDirectory = new File("src/test/resources/university2.ttl");
        o = new Ontology(resourcesDirectory.getAbsolutePath());

        p = new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy"))),
                new Variable("x"),
                new UnboundVariable("y"));

        p.saturate(o);

        applicableAxioms = new HashSet<>();

        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (p.applicable(I)) {
                applicableAxioms.add(I);
            }
        }

        assertEquals(1, applicableAxioms.size());

        p = new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy"))),
                new UnboundVariable("x"),
                new UnboundVariable("y"));

        p.saturate(o);

        applicableAxioms = new HashSet<>();

        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (p.applicable(I)) {
                applicableAxioms.add(I);
            }
        }

        assertEquals(1, applicableAxioms.size());

        p = new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy")
                .getInverseProperty())),
                new UnboundVariable("x"),
                new UnboundVariable("y"));

        p.saturate(o);

        applicableAxioms = new HashSet<>();

        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (p.applicable(I)) {
                applicableAxioms.add(I);
            }
        }

        assertEquals(1, applicableAxioms.size());

        p = new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy")
                .getInverseProperty())),
                new UnboundVariable("x"),
                new Variable("y"));

        p.saturate(o);

        applicableAxioms = new HashSet<>();

        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (p.applicable(I)) {
                applicableAxioms.add(I);
            }
        }

        assertEquals(1, applicableAxioms.size());

        p = new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy")
                .getInverseProperty())),
                new Variable("x"),
                new Variable("y"));

        p.saturate(o);

        applicableAxioms = new HashSet<>();

        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (p.applicable(I)) {
                applicableAxioms.add(I);
            }
        }

        assertEquals(0, applicableAxioms.size());

        p = new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy"))),
                new Variable("x"),
                new Variable("y"));

        p.saturate(o);

        applicableAxioms = new HashSet<>();

        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (p.applicable(I)) {
                applicableAxioms.add(I);
            }
        }

        assertEquals(0, applicableAxioms.size());
    }

    @Test
    public void testApply() throws OWLOntologyCreationException, NotOWL2QLException {
        // load ontology
        File resourcesDirectory = new File("src/test/resources/university.owl");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath());
        Rewriter rewriter = new RewriterImpl();
        Roles p = new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("teaches"))),
                new Variable("x"),
                new UnboundVariable("y"));

        Set<RewritableAtom> rewritten = new HashSet<>(Collections.singleton(
                new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("teaches"))),
                        new Variable("x"), new UnboundVariable("y"))));

        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (p.applicable(I)) {
                rewritten.add(p.apply(I, o, rewriter));
            }
        }

        assertEquals(2, rewritten.size());
        assertEquals(new HashSet<>(Arrays.asList(
                new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("teaches"))),
                        new Variable("x"), new UnboundVariable("y")),
                new Conceptname(o.getClassMap().get("Professor"), new Variable("x"))
        )), rewritten);

        // load ontology
        resourcesDirectory = new File("src/test/resources/university2.ttl");
        o = new Ontology(resourcesDirectory.getAbsolutePath());

        p = new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy"))),
                new Variable("x"),
                new UnboundVariable("y"));

        p.saturate(o);

        rewritten = new HashSet<>(Collections.singleton(
                new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy"))),
                        new Variable("x"), new UnboundVariable("y"))));

        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (p.applicable(I)) {
                rewritten.add(p.apply(I, o, rewriter));
            }
        }

        assertEquals(2, rewritten.size());
        assertEquals(new HashSet<>(Arrays.asList(
                new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy"))),
                        new Variable("x"), new UnboundVariable("y")),
                new Conceptname(o.getClassMap().get("GraduateStudent"), new Variable("x"))
        )), rewritten);

        p = new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy"))),
                new UnboundVariable("x"), new UnboundVariable("y"));

        p.saturate(o);

        rewritten = new HashSet<>(Collections.singleton(
                new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy"))),
                        new UnboundVariable("x"), new UnboundVariable("y"))));

        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (p.applicable(I)) {
                rewritten.add(p.apply(I, o, rewriter));
            }
        }

        assertEquals(2, rewritten.size());
        assertEquals(new HashSet<>(Arrays.asList(
                new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy"))),
                        new UnboundVariable("x"), new UnboundVariable("y")),
                new Conceptname(o.getClassMap().get("GraduateStudent"), new UnboundVariable("x"))
        )), rewritten);

        p = new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy")
                .getInverseProperty())),
                new UnboundVariable("x"),
                new UnboundVariable("y"));

        p.saturate(o);

        rewritten = new HashSet<>(Collections.singleton(
                new Roles(new HashSet<>(
                        Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy").getInverseProperty())),
                        new UnboundVariable("x"), new UnboundVariable("y"))));

        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (p.applicable(I)) {
                rewritten.add(p.apply(I, o, rewriter));
            }
        }

        assertEquals(2, rewritten.size());
        assertEquals(new HashSet<>(Arrays.asList(
                new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy")
                        .getInverseProperty())),
                        new UnboundVariable("x"),
                        new UnboundVariable("y")),
                new Conceptname(o.getClassMap().get("GraduateStudent"), new UnboundVariable("y"))
        )), rewritten);

        p = new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy")
                .getInverseProperty())),
                new UnboundVariable("x"),
                new Variable("y"));

        p.saturate(o);

        rewritten = new HashSet<>(
                Collections.singleton(
                        new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy")
                                .getInverseProperty())), new UnboundVariable("x"), new Variable("y"))));

        for (OWLAxiom I: o.getOntology().getAxioms()) {
            if (p.applicable(I)) {
                rewritten.add(p.apply(I, o, rewriter));
            }
        }

        assertEquals(2, rewritten.size());
        assertEquals(new HashSet<>(Arrays.asList(
                new Roles(new HashSet<>(Collections.singleton(o.getPropertyMap().get("gradStudentSupervisedBy")
                        .getInverseProperty())),
                        new UnboundVariable("x"),
                        new Variable("y")),
                new Conceptname(o.getClassMap().get("GraduateStudent"), new Variable("y"))
        )), rewritten);
    }
}
