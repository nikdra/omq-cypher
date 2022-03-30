package at.ac.tuwien.informatics.structure;

import at.ac.tuwien.informatics.generated.QBaseVisitor;
import at.ac.tuwien.informatics.generated.QParser;
import at.ac.tuwien.informatics.structure.query.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for extracting the query abstract syntax tree from the tree walk in the parser {@link QParser}.
 */
public class InputQueryBuilder extends QBaseVisitor<Object> {

    private final Ontology ontology;

    public InputQueryBuilder(Ontology ontology){
        this.ontology = ontology;
    }

    /**
     * Visit the query. This is the entry point to our parser.
     * Returns a query with head and body.
     *
     * @param ctx The query context of the parser
     * @return {@link InputQuery}.
     */
    @Override
    public Object visitQuery(QParser.QueryContext ctx) {
        List<Variable> head = (List<Variable>) visit(ctx.head());
        Set<Atom> body = (Set<Atom>) visit(ctx.body());
        return new InputQuery(head, body);
    }

    /**
     * Visit the head of the query and return a set of variables (the answer variables).
     *
     * @param ctx The head context of the parser.
     * @return A set of variables {@link Variable}.
     */
    @Override
    public Object visitHead(QParser.HeadContext ctx) {
        List<Variable> variables = new LinkedList<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree c = ctx.getChild(i);
            Object result = c.accept(this);

            if (result != null) {
                variables.add((Variable) result);
            }
        }
        return variables;
    }

    /**
     * Visit a variable in the query and return a Variable object.
     *
     * @param ctx The variable context of the parser.
     * @return {@link Variable}.
     */
    @Override
    public Object visitVariable(QParser.VariableContext ctx) {
        return new Variable(ctx.WORD().toString());
    }

    /**
     * Visit the body of the query and return a set of atoms.
     *
     * @param ctx The body context of the parser.
     * @return A Set of {@link Atom}.
     */
    @Override
    public Object visitBody(QParser.BodyContext ctx) {
        Set<Atom> atoms = new HashSet<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree c = ctx.getChild(i);
            Object result = c.accept(this);

            if (result != null) {
                atoms.add((Atom) result);
            }
        }
        return atoms;
    }

    /**
     * Visit an atom in the body of a query.
     * Because the atom is a role, concept name or a path, just descend in the AST.
     *
     * @param ctx The atom context of the parser.
     * @return {@link Atom}
     */
    @Override
    public Object visitAtom(QParser.AtomContext ctx) {
        return super.visitAtom(ctx);
    }

    @Override
    public Object visitConceptname(QParser.ConceptnameContext ctx) {
        return new Conceptname(ontology.getClassMap().get((String) this.visit(ctx.words())),
                (Variable) this.visit(ctx.variable()));
    }

    @Override
    public Object visitRoles(QParser.RolesContext ctx) {
        Set<OWLObjectPropertyExpression> properties = (Set<OWLObjectPropertyExpression>) this.visitProperties(ctx.properties());
        return new Roles(properties, (Variable) this.visitVariable(ctx.left), (Variable) this.visitVariable(ctx.right));
    }

    @Override
    public Object visitPath(QParser.PathContext ctx) {
        return super.visitPath(ctx);
    }

    @Override
    public Object visitElements(QParser.ElementsContext ctx) {
        return super.visitElements(ctx);
    }

    @Override
    public Object visitPathElement(QParser.PathElementContext ctx) {
        return super.visitPathElement(ctx);
    }

    @Override
    public Object visitArbitraryLengthPathElement(QParser.ArbitraryLengthPathElementContext ctx) {
        return super.visitArbitraryLengthPathElement(ctx);
    }

    @Override
    public Object visitSingleLengthPathElement(QParser.SingleLengthPathElementContext ctx) {
        return super.visitSingleLengthPathElement(ctx);
    }

    @Override
    public Object visitProperties(QParser.PropertiesContext ctx) {
        Set<OWLObjectPropertyExpression> properties = new HashSet<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree c = ctx.getChild(i);
            Object result = c.accept(this);

            if (result != null) {
                properties.add((OWLObjectPropertyExpression) result);
            }
        }
        return properties;
    }

    @Override
    public Object visitProperty(QParser.PropertyContext ctx) {
        return super.visitProperty(ctx);
    }

    @Override
    public Object visitRolename(QParser.RolenameContext ctx) {
        return this.ontology.getPropertyMap().get((String) this.visitWords(ctx.words()));
    }

    @Override
    public Object visitInverse(QParser.InverseContext ctx) {
        return this.ontology.getPropertyMap()
                .get((String) this.visitWords(ctx.words()))
                .getInverseProperty();
    }

    @Override
    public Object visitWords(QParser.WordsContext ctx) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree c = ctx.getChild(i);
            builder.append(c);
        }
        return builder.toString();
    }
}

