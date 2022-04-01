package at.ac.tuwien.informatics.structure.query;

import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import java.util.HashSet;
import java.util.Set;

/**
 * A class that represents an element of a path that is of arbitrary length.
 */
public class ArbitraryLengthPathElement extends PathElement {

    /**
     * Initialize a new path element.
     *
     * @param roles the set (disjunction) of role names occuring in this path element.
     */
    public ArbitraryLengthPathElement(Set<OWLObjectPropertyExpression> roles) {
        super(roles);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ArbitraryLengthPathElement)) {
            return false;
        }

        ArbitraryLengthPathElement a = (ArbitraryLengthPathElement) obj;

        return this.roles.equals(a.roles);
    }

    @Override
    public String toString() {
        return super.toString() + "*";
    }

    /**
     * Convert this path element to an arbitrary length atom.
     *
     * @param left  The left {@link Term}.
     * @param right The right {@link Term}.
     * @return This path element as a rewritable single path atom.
     */
    @Override
    public ArbitraryLengthAtom toBinary(Term left, Term right) {
        return new ArbitraryLengthAtom(new HashSet<>(this.roles), left, right);
    }
}
