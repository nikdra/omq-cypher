package at.ac.tuwien.informatics.structure.query;


import at.ac.tuwien.informatics.structure.Ontology;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A class that represents a path atom \rho(x,y) in the query for a \Xi-restricted regular expression \rho and
 * variables "x", "y".
 */
public class Path implements Atom {

    /**
     * The term on the left.
     */
    private final Term left;
    /**
     * The term on the right.
     */
    private final Term right;
    /**
     * The elements of the path.
     */
    private final List<PathElement> elements;

    public Path(List<PathElement> elements, Term left, Term right) {
        this.elements = elements;
        this.left = left;
        this.right = right;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.elements != null ? this.elements.hashCode() : 0);
        hash = 53 * hash + (this.left != null ? this.left.hashCode() : 0);
        hash = 53 * hash + (this.right != null ? this.right.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Path)){
            return false;
        }

        Path p = (Path) obj;

        return this.elements.equals(p.elements) && this.left.equals(p.left) && this.right.equals(p.right);
    }

    @Override
    public String toString() {
        return this.elements.stream().map(PathElement::toString).collect(Collectors.joining("/")) +
                "(" + this.left.toString() + "," + this.right.toString() + ")";
    }

    /**
     * Saturate each element of the path by exhaustively applying the subrole axioms for each role in the set of role
     * names in the element.
     * @param o The ontology.
     */
    public void saturate(Ontology o) {
        elements.forEach(e -> e.saturate(o));
    }

    /**
     * Get the list of elements in this path.
     * @return The list of path elements.
     */
    public List<PathElement> getElements() {
        return elements;
    }

    /**
     * Get the Term on the left.
     * @return The Term on the left.
     */
    public Term getLeft() {
        return left;
    }

    /**
     * Get the Term on the right.
     * @return The Term on the right.
     */
    public Term getRight() {
            return right;
    }
}
