package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.Substitution;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class representing a single path atom (r \cup ...)^*(x,y) of arbitrary length.
 */
public class ArbitraryLengthSinglePathAtom extends SinglePathAtom {

    public ArbitraryLengthSinglePathAtom(Set<String> rolenames, Term left, Term right) {
        super(rolenames, left, right);
    }

    /**
     * Return true if the atom can be replaced by another atom given an axiom.
     *
     * @param o The ontology wrapper object.
     * @param a The axiom to be applied.
     * @return True if the axiom is applicable, false otherwise.
     */
    @Override
    public boolean applicable(Ontology o, OWLAxiom a) {
        return false;
    }

    /**
     * Apply a replacement by an axiom on this atom and return the new atom.
     * <p>
     * Precondition for correctness: applicable was called before.
     *
     * @param o The ontology wrapper object.
     * @param a The axiom to be applied.
     * @return The new atom.
     */
    @Override
    public RewritableAtom apply(Ontology o, OWLAxiom a) {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ArbitraryLengthSinglePathAtom)){
            return false;
        }

        ArbitraryLengthSinglePathAtom r = (ArbitraryLengthSinglePathAtom) obj;

        return this.rolenames.equals(r.rolenames) && this.left.equals(r.left) && this.right.equals(r.right);
    }

    @Override
    public String toString() {
        return super.toString() + "*(" + this.left.toString() +"," + this.right.toString() + ")";
    }

    /**
     * Create a new arbitrary length single path atom with the given terms.
     *
     * @param left The left {@link Term}.
     * @param right The right {@link Term}.
     * @return A new arbitrary length single path atom, that can be rewritten.
     */
    @Override
    public SinglePathAtom replaceTerms(Term left, Term right) {
        return new ArbitraryLengthSinglePathAtom(new HashSet<>(this.rolenames), left, right);
    }

    @Override
    public ArbitraryLengthSinglePathAtom applySubstitution(List<Substitution> substitutions) {
        Term left = this.left.getFresh();
        Term right = this.right.getFresh();

        for (Substitution sub : substitutions) {
            left = left.applySubstitution(sub);
            right = right.applySubstitution(sub);
        }
        return new ArbitraryLengthSinglePathAtom(new HashSet<>(this.rolenames), left, right);
    }
}
