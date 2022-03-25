package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.Ontology;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * A class that represents a role atom r(x,y) in the query for some role name "r" and variables "x","y".
 */
public class Role implements RewritableAtom {

    /**
     * The name of the role.
     */
    private final String name;
    /**
     * The variable on the left.
     */
    private Variable left;
    /**
     * The variable on the right.
     */
    private Variable right;

    /**
     * Initialize a new Role object.
     * @param name The role name.
     * @param left The left {@link Variable}.
     * @param right The right {@link Variable}.
     */
    public Role(String name, Variable left, Variable right) {
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

        if (!(obj instanceof Role)){
            return false;
        }

        Role r = (Role) obj;

        return this.name.equals(r.name) && this.left.equals(r.left) && this.right.equals(r.right);
    }

    @Override
    public String toString() {
        return this.name + '(' + this.left.toString() + ',' + this.right.toString() + ')';
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
}
