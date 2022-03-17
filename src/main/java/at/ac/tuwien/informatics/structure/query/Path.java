package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.Ontology;
import org.semanticweb.owlapi.model.OWLAxiom;

public class Path implements Atom {
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
    public Atom apply(Ontology o, OWLAxiom a) {
        return null;
    }
}
