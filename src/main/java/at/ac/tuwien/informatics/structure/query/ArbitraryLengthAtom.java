package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.reformulation.Rewriter;
import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.Substitution;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class representing a single path atom (r \cup ...)^*(x,y) of arbitrary length.
 */
public class ArbitraryLengthAtom implements Binary {

    /**
     * The set (disjunction) of role names occurring in this path element.
     */
    private final Set<OWLObjectPropertyExpression> roles;

    /**
     * The term on the left.
     */
    private final Term left;

    /**
     * The term on the right.
     */
    private final Term right;

    /**
     * Initialize a new Arbitrary Length Atom
     *
     * @param roles The disjunction of roles in this atom.
     * @param left The term on the left.
     * @param right The term on the right.
     */
    public ArbitraryLengthAtom(Set<OWLObjectPropertyExpression> roles, Term left, Term right) {
        this.roles = roles;
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ArbitraryLengthAtom)){
            return false;
        }

        ArbitraryLengthAtom r = (ArbitraryLengthAtom) obj;

        return this.roles.equals(r.roles) && this.left.toString().equals(r.left.toString())
                && this.right.toString().equals(r.right.toString());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.roles != null ? this.roles.hashCode() : 0);
        hash = 53 * hash + (this.left != null ? this.left.hashCode() : 0);
        hash = 53 * hash + (this.right != null ? this.right.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        String rolestring = this.roles.stream()
                .map(p -> p.getNamedProperty().getIRI().getFragment()) // remember, no inverses allowed/possible here
                .collect(Collectors.joining("|"));
        if (this.roles.size() > 1) {
            rolestring = '(' + rolestring + ')';
        }
        return rolestring + "*(" + this.left.toString() + "," + this.right.toString() + ')';
    }

    /**
     * Create a new arbitrary length single path atom with the given terms.
     *
     * @param left The left {@link Term}.
     * @param right The right {@link Term}.
     * @return A new arbitrary length single path atom, that can be rewritten.
     */
    public ArbitraryLengthAtom replaceTerms(Term left, Term right) {
        return new ArbitraryLengthAtom(new HashSet<>(this.roles), left, right);
    }

    /**
     * Apply a list of substitutions to the terms of this atom.
     *
     * @param substitutions A list of substitutions.
     * @return A new ArbitraryLengthSinglePathAtom with the substitutions applied to its terms.
     */
    public ArbitraryLengthAtom applySubstitution(List<Substitution> substitutions) {
        Term left = this.left.getFresh();
        Term right = this.right.getFresh();

        for (Substitution sub : substitutions) {
            left = left.applySubstitution(sub);
            right = right.applySubstitution(sub);
        }
        return new ArbitraryLengthAtom(new HashSet<>(this.roles), left, right);
    }

    /**
     * Return true if the atom can be replaced by another atom given an axiom.
     * To arbitrary length atoms, no axioms are applicable.
     *
     * @param I The axiom to be applied.
     * @return True if the axiom is applicable, false otherwise.
     */
    @Override
    public boolean applicable(OWLAxiom I) {
        return false;
    }

    /**
     * Apply a replacement by an axiom on this atom and return the new atom.
     * <p>
     * Precondition for correctness: applicable was called before.
     * Note that no axiom is applicable to atoms of this type.
     *
     * @param I The axiom to be applied.
     * @param o The ontology.
     * @param rewriter The rewriter that called this function on this atom.
     * @return The new atom.
     */
    @Override
    public RewritableAtom apply(OWLAxiom I, Ontology o, Rewriter rewriter) {
        return new ArbitraryLengthAtom(new HashSet<>(this.roles), this.left.getFresh(), this.right.getFresh());
    }

    @Override
    public Term getLeft() {
        return this.left.getFresh();
    }

    @Override
    public Term getRight() {
        return this.right.getFresh();
    }

    @Override
    public Set<OWLObjectPropertyExpression> getRoles() {
        return this.roles;
    }
}
