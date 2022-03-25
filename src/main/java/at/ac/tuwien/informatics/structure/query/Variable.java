package at.ac.tuwien.informatics.structure.query;

/**
 * A class that represents a variable in the query.
 */
public class Variable {

    /**
     * The name of the variable.
     */
    private final String name;
    /**
     * Boolean indicating if the variable is bound. Default true.
     */
    private boolean bound = true;

    /**
     * Initialize a new variable object with a name.
     * @param name The name of the variable.
     */
    public Variable(String name) {
        this.name = name;
    }

    public boolean isBound() {
        return bound;
    }

    public void setBound(boolean bound) {
        this.bound = bound;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + Boolean.hashCode(this.bound);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO: we can name unbound variables the same, but they are different!
        if (obj == this) {
            return true;
        }

        if(!(obj instanceof Variable)) {
            return false;
        }

        Variable v = (Variable) obj;

        return this.name.equals(v.name) && (this.bound == v.bound);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
