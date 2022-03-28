package at.ac.tuwien.informatics.structure.query;

import java.util.Set;

public abstract class SinglePathAtom implements RewritableAtom {
    protected final Set<String> rolenames;
    protected final Term left;
    protected final Term right;

    public SinglePathAtom(Set<String> rolenames, Term left, Term right) {
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

    public Term getLeft() {
        return left;
    }

    public Term getRight() {
        return right;
    }

    public abstract SinglePathAtom replaceTerms(Term left, Term right);
}
