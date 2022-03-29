package at.ac.tuwien.informatics.structure;

import at.ac.tuwien.informatics.structure.query.Term;

public class Substitution {
    private Term in;
    private Term out;

    public Substitution(Term in, Term out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Substitution)) {
            return false;
        }

        Substitution s = (Substitution) obj;

        return this.in.equals(s.in) && this.out.equals(s.out);
    }

    @Override
    public String toString() {
        return this.out.toString() + "/" + this.in.toString();
    }

    public Term getIn() {
        return in;
    }

    public void setIn(Term in) {
        this.in = in;
    }

    public Term getOut() {
        return out;
    }

    public void setOut(Term out) {
        this.out = out;
    }
}
