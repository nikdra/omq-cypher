package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.Ontology;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * An interface that represents a rewritable atom in the query body.
 */

public interface RewritableAtom extends Atom{

    /**
     * Return true if the atom can be replaced by another atom given an axiom.
     *
     * @param o The ontology wrapper object.
     * @param a The axiom to be applied.
     * @return True if the axiom is applicable, false otherwise.
     */
    boolean applicable(Ontology o, OWLAxiom a);

    /**
     * Apply a replacement by an axiom on this atom and return the new atom.
     *
     * Precondition for correctness: applicable was called before.
     * @param o The ontology wrapper object.
     * @param a The axiom to be applied.
     * @return The new atom.
     */
    RewritableAtom apply(Ontology o, OWLAxiom a);
}
