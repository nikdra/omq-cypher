package at.ac.tuwien.informatics.structure.query;

/**
 * This class represents an unbound variable.
 * By default, each unbound variable is considered a different variable.
 */
public class UnboundVariable implements Term {
    @Override
    public String toString() {
        return "_";
    }
}
