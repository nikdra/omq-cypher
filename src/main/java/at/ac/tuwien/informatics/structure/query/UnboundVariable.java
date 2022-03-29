package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.Substitution;

/**
 * This class represents an unbound variable.
 * Each unbound variable must be considered a different variable in the unification step.
 */
public class UnboundVariable implements Term {

    private final String name;

    public UnboundVariable(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "_";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof UnboundVariable)) {
            return false;
        }

        UnboundVariable v = (UnboundVariable) obj;

        return this.name.equals(v.name);

    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public Term applySubstitution(Substitution s) {
        if (s.getIn().equals(this)) {
            return new UnboundVariable(s.getOut().getName());
        }
        return new UnboundVariable(this.name);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Term getFresh() {
        return new UnboundVariable(this.name);
    }
}
