package at.ac.tuwien.informatics.reformulation;

import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.query.*;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.*;

/**
 * An implementation of a rewriter for XI-restricted queries.
 * The assumption for all methods is that the query is Xi-restricted, and the TBox is Xi-compliant.
 */
public class RewriterImpl implements Rewriter {

    private int variable_counter = 0;

    /**
     * Given a Xi-restricted query q, rewrite q into a set of queries such that the evaluation over the data returns
     * all the certain answers in the KB.
     *
     * @param q The input query.
     * @param o The Xi-compliant ontology.
     * @return Set of queries.
     */
    @Override
    public Set<RewritableQuery> rewrite(InputQuery q, Ontology o) {
        Set<RewritableQuery> Q = new HashSet<>();
        Q.add(tau(saturatePaths(q, o)));
        Set<RewritableQuery> Qp = null;
        /*

        while(!Q.equals(Qp)) {
            Qp = new HashSet<>(Q);
            for (RewritableQuery qp: Qp) {
                // (a) apply axioms, if possible
                for (RewritableAtom a: qp.getBody()) {
                    for (OWLAxiom I: o.getOntology().getAxioms()) {
                        if(a.applicable(o, I)) {
                            Q.add(replace(qp, a, o, I));
                        }
                    }
                }

                // (b) unify atoms, if possible
                for (RewritableAtom a1: qp.getBody()) {
                    for (RewritableAtom a2 : qp.getBody()) {
                        Q.add(tau(reduce(qp, a1, a2)));
                    }
                }
                // TODO path rewritings
            }
        }
         */
        return Qp;
    }

    /**
     * Given a Xi-restricted query q, apply disjunction to the path atoms occurring in the query.
     * Then, split multi-element path atoms into single path atoms.
     *
     * @param q The input query.
     * @param o The Xi-compliant ontology.
     * @return A Xi-restricted query q.
     */
    @Override
    public RewritableQuery saturatePaths(InputQuery q, Ontology o) {
        // iterate over atoms, apply role inclusion if it's a path atom and split
        // transform roles into single length single path atoms
        // otherwise, just add to query
        Set<RewritableAtom> body = new HashSet<>();
        for (Atom a : q.getBody()) {
            if (a instanceof Conceptname) { // Concept name
                body.add((Conceptname) a);
            }
            else if (a instanceof Roles) { // Role atom
                Roles b = (Roles) a;
                b.saturate(o); // exhaustively apply subrole/inverse axioms
                body.add(b);
            }
            else { // Path
                Path b = (Path) a;
                // saturate each element of the path
                b.saturate(o);
                // get elements and split into single path atoms, add each to the query
                List<PathElement> elements = b.getElements();
                Iterator<PathElement> it = elements.listIterator();
                Term left = b.getLeft().getFresh();
                PathElement element = it.next();
                while(it.hasNext()) {
                    Variable right = new Variable("v" + ++variable_counter);
                    body.add(element.toBinary(left, right));
                    element = it.next();
                    left = new Variable("v" + variable_counter);
                }
                body.add(element.toBinary(left, b.getRight().getFresh()));
            }
        }

        List<Variable> head = new LinkedList<>(q.getHead());
        return new RewritableQuery(head, body);
    }

    /**
     * Given a Xi-restricted query q, mark all unbound variables as such.
     * Unbound variables are denoted by {@link UnboundVariable} objects.
     *
     * @param q The input query
     * @return A Xi-restricted query q.
     */
    @Override
    public RewritableQuery tau(RewritableQuery q) {
        // map of variables and the number of atoms they occur in
        Map<Variable, Integer> variableCount = new HashMap<>();
        // first pass: get number of terms each variable occurs in
        for (RewritableAtom a : q.getBody()) {
            if (a instanceof Conceptname) { // concept name
                Conceptname b = (Conceptname) a;
                if (b.getTerm() instanceof Variable) {
                    Integer count = variableCount.getOrDefault((Variable) b.getTerm(), 0);
                    variableCount.put((Variable) b.getTerm(), count + 1);
                }
            }
            if (a instanceof Binary) { // single path atom - includes roles
                Binary b = (Binary) a;
                if (b.getLeft() instanceof Variable) {
                    Integer count = variableCount.getOrDefault((Variable) b.getLeft(), 0);
                    variableCount.put((Variable) b.getLeft(), count + 1);
                }
                if (b.getRight() instanceof Variable) {
                    Integer count = variableCount.getOrDefault((Variable) b.getRight(), 0);
                    variableCount.put((Variable) b.getRight(), count + 1);
                }
            }
        }
        // second pass: replace all entries that are not constants or answer variables with unbound variables
        Set<RewritableAtom> body = new HashSet<>();
        for (RewritableAtom a : q.getBody()) {
            if (a instanceof Conceptname) { // concept name
                Conceptname b = (Conceptname) a;
                Term t = b.getTerm().getFresh();
                if (b.getTerm() instanceof Variable) {  // contains variable
                    if ((variableCount.get((Variable) b.getTerm()) == 1) &&
                            !q.getHead().contains((Variable) b.getTerm())) { // unbound variable
                        t = new UnboundVariable(b.getTerm().getName());  // replace term
                    }
                }
                body.add(new Conceptname(b.getName(), t)); // add to new query
            }
            if (a instanceof Binary) { // roles, arb.length atoms
                Binary b = (Binary) a;
                Term left = b.getLeft().getFresh();
                Term right = b.getRight().getFresh();
                if (b.getLeft() instanceof Variable) {
                    if ((variableCount.get((Variable) b.getLeft()) == 1) &&
                            !q.getHead().contains((Variable) b.getLeft())) { // unbound variable
                        left = new UnboundVariable(b.getLeft().getName()); // replace term
                    }
                }
                if (b.getRight() instanceof Variable) {
                    if ((variableCount.get((Variable) b.getRight()) == 1) &&
                            !q.getHead().contains((Variable) b.getRight())) { // unbound variable
                        right = new UnboundVariable(b.getRight().getName()); // replace term
                    }
                }
                body.add(b.replaceTerms(left, right));
            }
        }
        // return query with unbound variables marked as such
        return new RewritableQuery(new LinkedList<>(q.getHead()), body);
    }

    /**
     * Given a Xi-restricted query q, and two binary atoms in q, return the result of concatenating them.
     * Precondition: they can be concatenated i.e., they have a nonempty intersection of roles, terms in the correct
     * places and one of them is of arbitrary length.
     *
     * @param q  Xi-restricted query.
     * @param a1 A binary atom.
     * @param a2 An arbitrary length atom.
     * @return A Xi-restricted query q'.
     */
    @Override
    public RewritableQuery concatenate(RewritableQuery q, Binary a1, ArbitraryLengthAtom a2) {
        return null;
    }

    /**
     * Given a Xi-restricted query q, and two binary atoms in q, return the result of merging them.
     * Precondition: they can be merged i.e., they have a nonempty intersection of roles.
     *
     * @param q  Xi-restricted query.
     * @param a1 A single path atom.
     * @param a2 A single path atom.
     * @return A Xi-restricted query q'.
     */
    @Override
    public RewritableQuery merge(RewritableQuery q, Binary a1, Binary a2) {
        return null;
    }

    /**
     * Given a Xi-restricted query q, and an arbitrary path atom in q.
     * If the arbitrary length path atom can be dropped, then return the result of dropping it.
     * Note that the query body can _not_ be empty.
     *
     * @param q Xi-restricted query.
     * @param a An arbitrary length single path atom.
     * @return A Xi-restricted query q'.
     */
    @Override
    public RewritableQuery drop(RewritableQuery q, ArbitraryLengthAtom a) {
        // remember: no empty query body!
        return q;
    }

    /**
     * Given a Xi-restricted query q, and two atoms in q.
     * If the atoms can be unified, apply the most general unifier to q.
     *
     * @param q  Xi-restricted query.
     * @param a1 A rewritable atom in the query.
     * @param a2 A rewritable atom in the query.
     * @return A Xi-restricted query q'.
     */
    @Override
    public RewritableQuery reduce(RewritableQuery q, RewritableAtom a1, RewritableAtom a2) {
        /*
        if (a1 instanceof Conceptname && a2 instanceof Conceptname) {
            Conceptname b1 = (Conceptname) a1;
            Conceptname b2 = (Conceptname) a2;
            if (b1.getName().equals(b2.getName())) {
                // compute unifier, return result of applying the unifier to q
                Unifier unifier = new Unifier(Collections.singletonList(b1.getTerm().getFresh()),
                        Collections.singletonList(b2.getTerm().getFresh()));
                return unifier.apply(q);
            }
        } else if (a1 instanceof SingleLengthSinglePathAtom && a2 instanceof SingleLengthSinglePathAtom) {
            SingleLengthSinglePathAtom b1 = (SingleLengthSinglePathAtom) a1;
            SingleLengthSinglePathAtom b2 = (SingleLengthSinglePathAtom) a2;
            if (b1.getRolenames().equals(b2.getRolenames())) {
                // compute unifier, return result of applying the unifier to q
                Unifier unifier = new Unifier(Arrays.asList(b1.getLeft().getFresh(), b1.getRight().getFresh()),
                        Arrays.asList(b2.getLeft().getFresh(), b2.getRight().getFresh()));
                return unifier.apply(q);
            }
        } else if (a1 instanceof ArbitraryLengthSinglePathAtom && a2 instanceof ArbitraryLengthSinglePathAtom) {
            ArbitraryLengthSinglePathAtom b1 = (ArbitraryLengthSinglePathAtom) a1;
            ArbitraryLengthSinglePathAtom b2 = (ArbitraryLengthSinglePathAtom) a2;
            if (b1.getRolenames().equals(b2.getRolenames())) {
                // compute unifier, return result of applying the unifier to q
                Unifier unifier = new Unifier(Arrays.asList(b1.getLeft().getFresh(), b1.getRight().getFresh()),
                        Arrays.asList(b2.getLeft().getFresh(), b2.getRight().getFresh()));
                return unifier.apply(q);
            }
        }
         */
        return q;
    }

    /**
     * Replace atom in query.
     * Assumes that the axiom is applicable to the atom.
     *
     * @param q Xi-restricted query.
     * @param a A rewritable atom in the query.
     * @param o The ontology wrapper object.
     * @param I An OWL QL (DL-Lite) Axiom
     * @return A Xi-restricted query q'.
     */
    @Override
    public RewritableQuery replace(RewritableQuery q, RewritableAtom a, Ontology o, OWLAxiom I) {
        /*
        List<Variable> head = new LinkedList<>(q.getHead());
        Set<RewritableAtom> body = new HashSet<>(q.getBody());
        body.remove(a);
        body.add(a.apply(o, I, this));
        return new RewritableQuery(head, body);
         */
        return null;
    }

    @Override
    public String getFreshVariableName() {
        return "v" + ++this.variable_counter;
    }
}
