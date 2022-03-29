package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.Substitution;

/**
 * An interface that represents a term in a query atom.
 */
public interface Term {

    /**
     * Apply a substitution to this term.
     *
     * @param s The substitution to be applied
     * @return A new Term with the substitution applied.
     */
    Term applySubstitution(Substitution s);

    /**
     * Get the name of this term.
     *
     * @return The name of this term.
     */
    String getName();

    /**
     * Get a fresh object that is equal to this Term.
     *
     * @return This Term as a new object.
     */
    Term getFresh();
}
