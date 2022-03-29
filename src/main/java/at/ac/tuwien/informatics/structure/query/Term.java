package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.Substitution;

import java.util.List;

/**
 * An interface that represents a term in a query atom.
 */
public interface Term {

    Term applySubstitution(Substitution s);

    String getName();

    Term getFresh();
}
