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

    @Override
    public Term applySubstitution(Substitution s) {
        if (s.getIn().equals(this)) {
            return s.getOut().getFresh();
        }
        return new Variable(this.name);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Variable getFresh() {
        return new Variable(this.name);
    }
}
