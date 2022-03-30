package at.tuwien.informatics.structure;
import at.ac.tuwien.informatics.generated.QLexer;
import at.ac.tuwien.informatics.generated.QParser;
import at.ac.tuwien.informatics.structure.InputQueryBuilder;
import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.exception.NotOWL2QLException;
import at.ac.tuwien.informatics.structure.query.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.antlr.v4.runtime.*;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.util.*;

public class TestInputQueryBuilder {

    @Test
    public void testQueryConcept() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university.owl");
        CharStream cs = CharStreams.fromString("q(x):-Assistant_Prof(x)");
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));

        ParseTree tree = parser.query();

        InputQuery q = (InputQuery) new InputQueryBuilder(o).visit(tree);

        List<Variable> head = new LinkedList<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();
        body.add(new Conceptname(o.getClassMap().get("Assistant_Prof"), new Variable("x")));

        InputQuery q1 = new InputQuery(head, body);

        assertEquals(q1, q);
    }

    @Test
    public void testQueryConcepts() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university.owl");
        CharStream cs = CharStreams.fromString("q(x):-Assistant_Prof(x),Professor(y)");
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));

        ParseTree tree = parser.query();

        InputQuery q = (InputQuery) new InputQueryBuilder(o).visit(tree);

        List<Variable> head = new LinkedList<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();
        body.add(new Conceptname(o.getClassMap().get("Professor"), new Variable("y")));
        body.add(new Conceptname(o.getClassMap().get("Assistant_Prof"), new Variable("x")));

        InputQuery q1 = new InputQuery(head, body);

        assertEquals(q1, q);
    }

    @Test
    public void testQueryRoles() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university.owl");
        CharStream cs = CharStreams.fromString("q(x):-teaches(x,y)");
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));

        ParseTree tree = parser.query();

        InputQuery q = (InputQuery) new InputQueryBuilder(o).visit(tree);

        List<Variable> head = new LinkedList<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();
        body.add(new Roles(Collections.singleton(o.getPropertyMap().get("teaches")),
                new Variable("x"), new Variable("y")));

        InputQuery q1 = new InputQuery(head, body);

        assertEquals(q1, q);
    }

    @Test
    public void testQueryRoleWithTwoAnswerVariables() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university.owl");
        CharStream cs = CharStreams.fromString("q(x,y):-teaches(x,y)");
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));

        ParseTree tree = parser.query();

        InputQuery q = (InputQuery) new InputQueryBuilder(o).visit(tree);

        List<Variable> head = new LinkedList<>();
        head.add(new Variable("x"));
        head.add(new Variable("y"));
        HashSet<Atom> body = new HashSet<>();
        body.add(new Roles(Collections.singleton(o.getPropertyMap().get("teaches")),
                new Variable("x"), new Variable("y")));

        InputQuery q1 = new InputQuery(head, body);

        assertEquals(q1, q);
    }

    @Test
    public void testDoubleConcept() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university.owl");
        CharStream cs = CharStreams.fromString("q(x):-Professor(x),Professor(x)");
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));

        ParseTree tree = parser.query();

        InputQuery q = (InputQuery) new InputQueryBuilder(o).visit(tree);

        List<Variable> head = new LinkedList<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();
        body.add(new Conceptname(o.getClassMap().get("Professor"), new Variable("x")));

        InputQuery q1 = new InputQuery(head, body);

        assertEquals(q1, q);
    }

    @Test
    public void testDoubleConceptWithDifferentVariables() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/university.owl");
        CharStream cs = CharStreams.fromString("q(x):-Professor(x),Professor(y)");
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));

        ParseTree tree = parser.query();

        InputQuery q = (InputQuery) new InputQueryBuilder(o).visit(tree);

        List<Variable> head = new LinkedList<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();
        body.add(new Conceptname(o.getClassMap().get("Professor"), new Variable("x")));
        body.add(new Conceptname(o.getClassMap().get("Professor"), new Variable("y")));

        InputQuery q1 = new InputQuery(head, body);

        assertEquals(q1, q);
    }

    @Test
    public void testDisjunctionOfRoles() throws OWLOntologyCreationException, NotOWL2QLException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/subroles.owl");
        CharStream cs = CharStreams.fromString("q(x):-(s|r-|t|r)(x,y)");
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));

        ParseTree tree = parser.query();

        InputQuery q = (InputQuery) new InputQueryBuilder(o).visit(tree);
        List<Variable> head = new LinkedList<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();

        body.add(new Roles(new HashSet<>(Arrays.asList(
                o.getPropertyMap().get("s"),
                o.getPropertyMap().get("r").getInverseProperty(),
                o.getPropertyMap().get("t"),
                o.getPropertyMap().get("r"))),
                new Variable("x"), new Variable("y")));

        InputQuery q1 = new InputQuery(head, body);

        assertEquals(q1, q);
    }

    @Test
    public void testQueryPath() {
        // CharStream cs = CharStreams.fromString("q(x):-pat/(pat|rat)*/(cat|re)(x,y)");
        /*
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));

        ParseTree tree = parser.query();

        InputQuery q = (InputQuery) new InputQueryBuilder().visit(tree);
        List<Variable> head = new LinkedList<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();

        List<PathElement> elements = new LinkedList<>();
        elements.add(new SingleLengthPathElement(new HashSet<>(Collections.singletonList("pat"))));
        elements.add(new ArbitraryLengthPathElement(new HashSet<>(Arrays.asList("pat", "rat"))));
        elements.add(new SingleLengthPathElement(new HashSet<>(Arrays.asList("cat", "re"))));

        body.add(new Path(elements, new Variable("x"), new Variable("y")));

        InputQuery q1 = new InputQuery(head, body);

        assertEquals(q, q1);
         */
    }

    @Test
    public void testUnequalQueryPath() {
        // CharStream cs = CharStreams.fromString("q(x):-pat/(pat|rat)*/(cat|re)(x,y)");
        /*
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));

        ParseTree tree = parser.query();

        InputQuery q = (InputQuery) new InputQueryBuilder().visit(tree);
        List<Variable> head = new LinkedList<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();

        List<PathElement> elements = new LinkedList<>();
        elements.add(new SingleLengthPathElement(new HashSet<>(Collections.singletonList("pat"))));
        elements.add(new SingleLengthPathElement(new HashSet<>(Arrays.asList("cat", "re"))));
        elements.add(new ArbitraryLengthPathElement(new HashSet<>(Arrays.asList("pat", "rat"))));

        body.add(new Path(elements, new Variable("x"), new Variable("y")));

        InputQuery q1 = new InputQuery(head, body);

        assertNotEquals(q, q1);
        */
    }
}
