package at.ac.tuwien.informatics.structure.query;

import java.util.Set;

/**
 * An interface that represents an element of a path \rho(x,y).
 * Can be arbitrary length or single length.
 */
public abstract class PathElement {

    /**
     * The set (disjunction) of role names occurring in this path element.
     */
    protected final Set<String> rolenames;

    /**
     * Initialize a new path element.
     *
     * @param rolenames the set (disjunction) of role names occuring in this path element.
     */
    public PathElement(Set<String> rolenames) {
        this.rolenames = rolenames;
    }

    @Override
    public String toString() {
        if (this.rolenames.size() == 1) {
            return this.rolenames.iterator().next();
        } else {
            return "(" + String.join("|", this.rolenames) + ")";
        }
    }

}
