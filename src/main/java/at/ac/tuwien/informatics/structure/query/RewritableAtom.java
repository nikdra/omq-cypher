package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.reformulation.Rewriter;
import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.Substitution;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.List;

/**
 * An interface that represents a rewritable atom in the query body.
 */

public interface RewritableAtom extends Atom {

    /**
     * Return true if the atom can be replaced by another atom given an axiom.
     *
     * @param a The axiom to be applied.
     * @return True if the axiom is applicable, false otherwise.
     */
    boolean applicable(OWLAxiom a);

    /**
     * Apply a replacement by an axiom on this atom and return the new atom.
     *
     * Precondition for correctness: applicable was called before.
     * @param a The axiom to be applied.
     * @return The new atom.
     */
    RewritableAtom apply(OWLAxiom a, Rewriter rewriter);

    /**
     * Apply a list of substitutions to the terms of an atom.
     *
     * @param substitutions A list of substitutions.
     * @return A new RewritableAtom with the substitutions applied to its terms.
     */
    RewritableAtom applySubstitution(List<Substitution> substitutions);
}
