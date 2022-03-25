package at.ac.tuwien.informatics.structure.query;


import java.util.List;
import java.util.stream.Collectors;

/**
 * A class that represents a path atom \rho(x,y) in the query for a \Xi-restricted regular expression \rho and
 * variables "x", "y".
 */
public class Path implements Atom {

    /**
     * The variable on the left.
     */
    private Variable left;
    /**
     * The variable on the right.
     */
    private Variable right;
    /**
     * The elements of the path.
     */
    private final List<PathElement> elements;

    public Path(List<PathElement> elements, Variable left, Variable right) {
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
}
