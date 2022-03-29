package at.ac.tuwien.informatics.structure.query;

import java.util.Collections;
import java.util.HashSet;

/**
 * A class that represents a role atom r(x,y) in the query for some role name "r" and variables "x","y".
 */
public class Role implements Atom {

    /**
     * The name of the role.
     */
    private final String name;
    /**
     * The term on the left.
     */
    private final Term left;
    /**
     * The term on the right.
     */
    private final Term right;

    /**
     * Initialize a new Role object.
     * @param name The role name.
     * @param left The left {@link Term}.
     * @param right The right {@link Term}.
     */
    public Role(String name, Term left, Term right) {
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
     * Transform this Role atom into a SingleLengthSinglePathAtom.
     *
     * @return Role as SingleLengthSinglePathAtom.
     */
    public SingleLengthSinglePathAtom toSingleLengthSinglePathAtom() {
        return new SingleLengthSinglePathAtom(new HashSet<>(Collections.singleton(this.name)), this.left, this.right);
    }

}
