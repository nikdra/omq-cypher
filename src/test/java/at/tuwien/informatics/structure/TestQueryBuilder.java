package at.tuwien.informatics.structure;
import at.ac.tuwien.informatics.generated.QLexer;
import at.ac.tuwien.informatics.generated.QParser;
import at.ac.tuwien.informatics.structure.QueryBuilder;
import at.ac.tuwien.informatics.structure.query.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.antlr.v4.runtime.*;

import java.util.HashSet;

public class TestQueryBuilder {

    @Test
    public void testQueryConcept() {
        CharStream cs = CharStreams.fromString("q(x):-Pizza(x)");
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));

        ParseTree tree = parser.query();

        Query q = (Query) new QueryBuilder().visit(tree);

        HashSet<Variable> head = new HashSet<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();
        body.add(new Conceptname("Pizza", new Variable("x")));

        Query q1 = new Query(head, body);

        assertEquals(q, q1);
    }

    @Test
    public void testQueryConcepts() {
        CharStream cs = CharStreams.fromString("q(x):-Pizza(x),Vegetarian(y)");
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));

        ParseTree tree = parser.query();

        Query q = (Query) new QueryBuilder().visit(tree);

        HashSet<Variable> head = new HashSet<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();
        body.add(new Conceptname("Pizza", new Variable("x")));
        body.add(new Conceptname("Vegetarian", new Variable("y")));

        Query q1 = new Query(head, body);

        assertEquals(q, q1);
    }

    @Test
    public void testQueryRole() {
        CharStream cs = CharStreams.fromString("q(x):-hasIngredient(x,y)");
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));

        ParseTree tree = parser.query();

        Query q = (Query) new QueryBuilder().visit(tree);

        HashSet<Variable> head = new HashSet<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();
        body.add(new Role("hasIngredient", new Variable("x"), new Variable("y")));

        Query q1 = new Query(head, body);

        assertEquals(q, q1);
    }

    @Test
    public void testQueryRoleWithTwoAnswerVariables() {
        CharStream cs = CharStreams.fromString("q(x,y):-hasIngredient(x,y)");
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));

        ParseTree tree = parser.query();

        Query q = (Query) new QueryBuilder().visit(tree);

        HashSet<Variable> head = new HashSet<>();
        head.add(new Variable("x"));
        head.add(new Variable("y"));

        HashSet<Atom> body = new HashSet<>();
        body.add(new Role("hasIngredient", new Variable("x"), new Variable("y")));

        Query q1 = new Query(head, body);

        assertEquals(q, q1);
    }

    @Test
    public void testDoubleConcept() {
        CharStream cs = CharStreams.fromString("q(x):-Pizza(x),Pizza(x)");
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));

        ParseTree tree = parser.query();

        Query q = (Query) new QueryBuilder().visit(tree);

        HashSet<Variable> head = new HashSet<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();
        body.add(new Conceptname("Pizza", new Variable("x")));

        Query q1 = new Query(head, body);

        assertEquals(q, q1);
    }

    @Test
    public void testDoubleConceptWithDifferentVariables() {
        CharStream cs = CharStreams.fromString("q(x):-Pizza(x),Pizza(y)");
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));

        ParseTree tree = parser.query();

        Query q = (Query) new QueryBuilder().visit(tree);

        HashSet<Variable> head = new HashSet<>();
        head.add(new Variable("x"));
        HashSet<Atom> body = new HashSet<>();
        body.add(new Conceptname("Pizza", new Variable("x")));
        body.add(new Conceptname("Pizza", new Variable("y")));

        Query q1 = new Query(head, body);

        assertEquals(q, q1);
    }
}
