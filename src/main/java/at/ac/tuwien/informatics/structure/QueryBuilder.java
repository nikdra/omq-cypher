package at.ac.tuwien.informatics.structure;

import at.ac.tuwien.informatics.generated.QBaseVisitor;
import at.ac.tuwien.informatics.generated.QParser;
import at.ac.tuwien.informatics.structure.query.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashSet;

/**
 * Class for extracting the query abstract syntax tree from the tree walk in the parser {@link QParser}.
 */
// TODO: change such that role names and concept names can use underscore
public class QueryBuilder extends QBaseVisitor<Object> {

    /**
     * Visit the head of the query and return a set of variables (the answer variables).
     * @param ctx The head context of the parser.
     * @return A set of variables {@link Variable}.
     */
    @Override
    public Object visitHead(QParser.HeadContext ctx) {
        HashSet<Variable> variables = new HashSet<>();
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
     * Visit a path atom.
     * Not implemented yet, and therefore ignored.
     * @param ctx The path context of the parser.
     * @return None.
     */
    @Override
    public Object visitPath(QParser.PathContext ctx) {
        // ignored for now
        return super.visitPath(ctx);
    }

    /**
     * Visit a role atom and return a Role object with the name of the role and the variables.
     * @param ctx The role context of the parser.
     * @return {@link Role}.
     */
    @Override
    public Object visitRole(QParser.RoleContext ctx) {
        return new Role(ctx.WORD().toString(), (Variable) this.visitVariable(ctx.left),
                (Variable) this.visitVariable(ctx.right));
    }

    /**
     * Visit the query. This is the entry point to our parser.
     * Returns a query with head and body.
     * @param ctx The query context of the parser
     * @return
     */
    @Override
    public Object visitQuery(QParser.QueryContext ctx) {
        HashSet<Variable> head = (HashSet<Variable>) visit(ctx.head());
        HashSet<Atom> body = (HashSet<Atom>) visit(ctx.body());
        return new Query(head, body);
    }

    /**
     * Visit a variable in the query and return a Variable object.
     * @param ctx The variable context of the parser.
     * @return {@link Variable}.
     */
    @Override
    public Object visitVariable(QParser.VariableContext ctx) {
        return new Variable(ctx.LETTER().toString());
    }

    /**
     * Visit the body of the query and return a set of atoms.
     * @param ctx The body context of the parser.
     * @return A Set of {@link Atom}.
     */
    @Override
    public Object visitBody(QParser.BodyContext ctx) {
        HashSet<Atom> atoms = new HashSet<>();
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
     * @param ctx The atom context of the parser.
     * @return {@link Atom}
     */
    @Override
    public Object visitAtom(QParser.AtomContext ctx) {
        return super.visitAtom(ctx);
    }

    /**
     * Visit a concept name in the query and return a conceptname object.
     * @param ctx The conceptname context of the parser.
     * @return {@link Conceptname}
     */
    @Override
    public Object visitConceptname(QParser.ConceptnameContext ctx) {
        return new Conceptname(ctx.WORD().toString(), (Variable) this.visit(ctx.variable()));
    }
}

