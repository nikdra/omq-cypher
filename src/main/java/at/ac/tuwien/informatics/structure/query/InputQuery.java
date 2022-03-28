package at.ac.tuwien.informatics.structure.query;


import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class that represents a input CRPQ.
 */
public class InputQuery implements Query {

    /**
     * The set of answer variables in the head.
     */
    private final Set<Variable> head;
    /**
     * The set of atoms in the body.
     */
    private final Set<Atom> body;

    /**
     * Initialize a new query object with a head and body.
     * The set of atoms is interpreted as a conjunction thereof.
     * @param head A set of answer {@link Variable}.
     * @param body A set of atoms {@link Atom}
     */
    public InputQuery(Set<Variable> head, Set<Atom> body) {
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

        if (!(obj instanceof InputQuery)) {
            return false;
        }

        InputQuery q = (InputQuery) obj;

        return this.body.equals(q.body) && this.head.equals(q.head);
    }

    @Override
    public String toString() {
        return "q(" +
                this.head.stream().map(Variable::toString).collect(Collectors.joining(",")) +
                "):-" +
                this.body.stream().map(Atom::toString).collect(Collectors.joining(","));
    }

    public Set<Variable> getHead() {
        return head;
    }

    public Set<Atom> getBody() {
        return body;
    }
}
