package at.ac.tuwien.informatics.structure.query;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class that represents a rewritable CRPQ.
 */
public class RewritableQuery implements Query {
    /**
     * The set of answer variables in the head.
     */
    private final List<Variable> head;
    /**
     * The set of atoms in the body.
     */
    private final Set<RewritableAtom> body;

    /**
     * Initialize a new query object with a head and body.
     * The set of atoms is interpreted as a conjunction thereof.
     * @param head A set of answer {@link Variable}.
     * @param body A set of atoms {@link RewritableAtom}
     */
    public RewritableQuery(List<Variable> head, Set<RewritableAtom> body) {
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

        if (!(obj instanceof RewritableQuery)) {
            return false;
        }

        RewritableQuery q = (RewritableQuery) obj;

        return this.body.equals(q.body) && this.head.equals(q.head);
    }

    @Override
    public String toString() {
        return "q(" +
                this.head.stream().map(Variable::toString).collect(Collectors.joining(",")) +
                "):-" +
                this.body.stream().map(RewritableAtom::toString).collect(Collectors.joining(","));
    }

    public List<Variable> getHead() {
        return head;
    }

    public Set<RewritableAtom> getBody() {
        return body;
    }
}
