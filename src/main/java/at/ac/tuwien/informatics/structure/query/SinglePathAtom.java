package at.ac.tuwien.informatics.structure.query;

import java.util.Set;

public abstract class SinglePathAtom implements RewritableAtom {

    /**
     * The set (disjunction) of role names occurring in this path element.
     */
    protected final Set<String> rolenames;

    /**
     * The term on the left.
     */
    protected final Term left;

    /**
     * The term on the right.
     */
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

    /**
     * Get the left Term.
     * @return The left Term.
     */
    public Term getLeft() {
        return left;
    }

    /**
     * Get the right Term.
     * @return The right Term.
     */
    public Term getRight() {
        return right;
    }

    /**
     * Create a new arbitrary length single path atom with the given terms.
     *
     * @param left The left {@link Term}.
     * @param right The right {@link Term}.
     * @return A new arbitrary length single path atom, that can be rewritten.
     */
    public abstract SinglePathAtom replaceTerms(Term left, Term right);

    public Set<String> getRolenames() {
        return rolenames;
    }
}
