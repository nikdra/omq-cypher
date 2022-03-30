package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.reformulation.Rewriter;
import at.ac.tuwien.informatics.structure.Substitution;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class that represents a role atom (R \cup ...)(x,y) in the query for some role names "R" (can be inverse!)
 * and variables "x","y".
 */
public class Roles implements RewritableAtom {

    /**
     * The names of the roles.
     */
    private final Set<OWLObjectPropertyExpression> name;
    /**
     * The term on the left.
     */
    private final Term left;
    /**
     * The term on the right.
     */
    private final Term right;

    /**
     * Initialize a new Role object.
     * @param name The role name.
     * @param left The left {@link Term}.
     * @param right The right {@link Term}.
     */
    public Roles(Set<OWLObjectPropertyExpression> name, Term left, Term right) {
        this.name = name;
        this.left = left;
        this.right = right;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.left != null ? this.left.hashCode() : 0);
        hash = 53 * hash + (this.right != null ? this.right.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Roles)){
            return false;
        }

        Roles r = (Roles) obj;

        return this.name.equals(r.name) && this.left.equals(r.left) && this.right.equals(r.right);
    }

    @Override
    public String toString() {
        return '(' + this.name
                .stream()
                .map(p -> p.getNamedProperty().getIRI().getFragment() + ((p instanceof OWLObjectInverseOf) ? "-" : ""))
                .collect(Collectors.joining("|")) + ')' +
                '(' + this.left.toString() + ',' + this.right.toString() + ')';
    }

    /**
     * Return true if the atom can be replaced by another atom given an axiom.
     *
     * @param a The axiom to be applied.
     * @return True if the axiom is applicable, false otherwise.
     */
    @Override
    public boolean applicable(OWLAxiom a) {
        return false;
    }

    /**
     * Apply a replacement by an axiom on this atom and return the new atom.
     * <p>
     * Precondition for correctness: applicable was called before.
     *
     * @param a        The axiom to be applied.
     * @param rewriter
     * @return The new atom.
     */
    @Override
    public RewritableAtom apply(OWLAxiom a, Rewriter rewriter) {
        return null;
    }

    /**
     * Apply a list of substitutions to the terms of an atom.
     *
     * @param substitutions A list of substitutions.
     * @return A new RewritableAtom with the substitutions applied to its terms.
     */
    @Override
    public RewritableAtom applySubstitution(List<Substitution> substitutions) {
        return null;
    }

    /*
     * Transform this Role atom into a SingleLengthSinglePathAtom.
     *
     * @return Role as SingleLengthSinglePathAtom.
     */
    /*
    public SingleLengthSinglePathAtom toSingleLengthSinglePathAtom() {
        return new SingleLengthSinglePathAtom(new HashSet<>(Collections.singleton(this.name)), this.left, this.right);
    }
     */
}
