package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.Ontology;

import java.util.Set;

/**
 * A class that represents an element of a path that is of arbitrary length.
 */
public class ArbitraryLengthPathElement extends PathElement {

    /**
     * Initialize a new path element.
     *
     * @param rolenames the set (disjunction) of role names occuring in this path element.
     */
    public ArbitraryLengthPathElement(Set<String> rolenames) {
        super(rolenames);
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

        return this.rolenames.equals(a.rolenames);
    }

    @Override
    public String toString() {
        return super.toString() + "*";
    }

    @Override
    public SinglePathAtom toSinglePathAtom(Term left, Term right) {
        return new ArbitraryLengthSinglePathAtom(this.rolenames, left, right);
    }
}
