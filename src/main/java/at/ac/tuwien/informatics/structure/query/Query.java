package at.ac.tuwien.informatics.structure.query;

import java.util.HashSet;

/**
 * A class that represents a CRPQ.
 */
public class Query {

    /**
     * The set of answer variables in the head.
     */
    private final HashSet<Variable> head;
    /**
     * The set of atoms in the body.
     */
    private final HashSet<Atom> body;

    /**
     * Initialize a new query object with a head and body.
     * The set of atoms is interpreted as a conjunction thereof.
     * @param head A set of answer {@link Variable}.
     * @param body A set of atoms {@link Atom}
     */
    public Query(HashSet<Variable> head, HashSet<Atom> body) {
        this.head = head;
        this.body = body;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.head != null ? this.head.hashCode() : 0);
        hash = 53 * hash + (this.body != null ? this.body.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Query)) {
            return false;
        }

        Query q = (Query) obj;

        return this.body.equals(q.body) && this.head.equals(q.head);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Head:");
        for (Variable v : this.head) {
            str.append("\n\t");
            str.append(v.toString());
        }
        str.append("\nBody");
        for (Atom a : this.body) {
            str.append("\n");
            str.append(a.toString());
        }
        return str.toString();
    }
}
