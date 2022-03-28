package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.Ontology;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * A class that represents a query atom of the form A(_) for a concept name A.
 */
public class Conceptname implements RewritableAtom {

    /**
     * The name of the concept.
     */
    private final String name;
    /**
     * The term in the atom.
     */
    private final Term term;

    /**
     * Initialize a new Conceptname object.
     * @param name The name of the concept.
     * @param term The {@link Term} in the atom.
     */
    public Conceptname(String name, Term term) {
        this.name = name;
        this.term = term;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if(!(obj instanceof Conceptname)) {
            return false;
        }

        Conceptname c = (Conceptname) obj;

        return this.name.equals(c.name) && this.term.equals(c.term);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.term != null ? this.term.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.name + '(' + this.term.toString() + ')';
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
}
