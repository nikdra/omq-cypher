package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.Substitution;

/**
 * A class that represents a variable in the query.
 */
public class Variable implements Term {

    /**
     * The name of the variable.
     */
    private final String name;

    /**
     * Initialize a new variable object with a name.
     * @param name The name of the variable.
     */
    public Variable(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if(!(obj instanceof Variable)) {
            return false;
        }

        Variable v = (Variable) obj;

        return this.name.equals(v.name);
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Apply a substitution to this variable.
     *
     * @param s The substitution to be applied
     * @return A new Term with the substitution applied.
     */
    @Override
    public Term applySubstitution(Substitution s) {
        if (s.getIn().equals(this)) {
            return s.getOut().getFresh();
        }
        return new Variable(this.name);
    }

    /**
     * Get the name of this variable.
     *
     * @return The name of this variable.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get a fresh Variable that is equal to this Variable.
     *
     * @return This Variable as a new object.
     */
    @Override
    public Variable getFresh() {
        return new Variable(this.name);
    }
}
