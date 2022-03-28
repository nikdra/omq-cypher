package at.ac.tuwien.informatics.structure.query;

import java.util.Set;

public abstract class SinglePathAtom implements RewritableAtom {
    protected final Set<String> rolenames;
    protected final Variable left;
    protected final Variable right;

    public SinglePathAtom(Set<String> rolenames, Variable left, Variable right) {
        this.rolenames = rolenames;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        if (this.rolenames.size() == 1) {
            return this.rolenames.iterator().next();
        } else {
            return "(" + String.join("|", this.rolenames) + ")";
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.rolenames != null ? this.rolenames.hashCode() : 0);
        hash = 53 * hash + (this.left != null ? this.left.hashCode() : 0);
        hash = 53 * hash + (this.right != null ? this.right.hashCode() : 0);
        return hash;
    }
}
