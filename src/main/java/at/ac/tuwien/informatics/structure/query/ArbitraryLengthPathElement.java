package at.ac.tuwien.informatics.structure.query;

import java.util.Set;
import java.util.stream.Collectors;

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
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.rolenames != null ? this.rolenames.hashCode() : 0);
        return hash;
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

}
