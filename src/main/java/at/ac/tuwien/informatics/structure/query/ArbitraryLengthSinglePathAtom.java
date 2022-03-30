package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.reformulation.Rewriter;
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
     * For this type of atom, no rewriting rule is applicable!
     * Hence, this function always returns false.
     *
     * @param a The axiom to be applied.
     * @return True if the axiom is applicable, false otherwise.
     */
    public boolean applicable(OWLAxiom a) {
        return false;
    }

    /**
     * Apply a replacement by an axiom on this atom and return the new atom.
     * For this type of atom, no rewriting rule is applicable!
     * Hence, this function does not do anything.
     *
     * Precondition for correctness: applicable was called before.
     *
     * @param a The axiom to be applied.
     * @return The new atom.
     */
    public RewritableAtom apply(OWLAxiom a, Rewriter rewriter) {
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

    /**
     * Apply a list of substitutions to the terms of this atom.
     *
     * @param substitutions A list of substitutions.
     * @return A new ArbitraryLengthSinglePathAtom with the substitutions applied to its terms.
     */
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
