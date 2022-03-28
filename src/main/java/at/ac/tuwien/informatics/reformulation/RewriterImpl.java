package at.ac.tuwien.informatics.reformulation;

import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.query.*;
import com.google.errorprone.annotations.Var;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.*;

/**
 * An implementation of a rewriter for XI-restricted queries.
 */
public class RewriterImpl implements Rewriter {

    /**
     * Given a Xi-restricted query q, rewrite q into a set of queries such that the evaluation over the data returns
     * all the certain answers in the KB.
     *
     * @param q The input query.
     * @param o The ontology.
     * @return Set of queries.
     */
    @Override
    public Set<RewritableQuery> rewrite(InputQuery q, Ontology o) {
        Set<RewritableQuery> Q = new HashSet<>();
        Q.add(tau(saturatePaths(q, o)));
        Set<RewritableQuery> Qp = null;

        while(!Q.equals(Qp)) {
            Qp = new HashSet<>(Q);
            for (RewritableQuery qp: Qp) {
                // (a) apply axioms, if possible
                for (RewritableAtom a: qp.getBody()) {
                    for (OWLAxiom I: o.getOntology().getAxioms()) {
                        if (a.applicable(o, I)) {
                            Q.add(replace(qp, a, I));
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
        return Qp;
    }

    /**
     * Given a Xi-restricted query q, apply disjunction to the path atoms occurring in the query.
     * Then, split multi-element path atoms into single path atoms.
     *
     * @param q The input query.
     * @param o The ontology.
     * @return A Xi-restricted query q.
     */
    @Override
    public RewritableQuery saturatePaths(InputQuery q, Ontology o) {
        // iterate over atoms, apply role inclusion if it's a path atom and split
        // transform roles into single length single path atoms
        // otherwise, just add to query
        int variable_counter = 0;
        Set<RewritableAtom> body = new HashSet<>();
        for (Atom a : q.getBody()) {
            if (a instanceof Conceptname) { // Concept name
                body.add((Conceptname) a);
            }
            else if (a instanceof Role) { // Role atom
                body.add(((Role) a).toSingleLengthSinglePathAtom());  // transform to single length single path atom
            }
            else { // Path
                Path b = (Path) a;
                // saturate
                b.saturate(o);
                // get elements and split into single path atoms
                List<PathElement> elements = b.getElements();
                Iterator<PathElement> it = elements.listIterator();
                Term left = b.getLeft();
                PathElement element = it.next();
                while(it.hasNext()) {
                    Variable right = new Variable("v" + ++variable_counter);
                    body.add(element.toSinglePathAtom(left, right));
                    element = it.next();
                    left = new Variable("v" + variable_counter);
                }
                body.add(element.toSinglePathAtom(left, b.getRight()));
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
            if (a instanceof SinglePathAtom) { // single path atom - includes roles
                SinglePathAtom b = (SinglePathAtom) a;
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
                Term t = b.getTerm();
                if (b.getTerm() instanceof Variable) {  // contains variable
                    if ((variableCount.get((Variable) b.getTerm()) == 1) &&
                            !q.getHead().contains((Variable) b.getTerm())) { // unbound variable
                        t = new UnboundVariable();  // replace term
                    }
                }
                body.add(new Conceptname(b.getName(), t)); // add to new query
            }
            if (a instanceof SinglePathAtom) { // single path atom - includes roles
                SinglePathAtom b = (SinglePathAtom) a;
                Term left = b.getLeft();
                Term right = b.getRight();
                if (b.getLeft() instanceof Variable) {
                    if ((variableCount.get((Variable) b.getLeft()) == 1) &&
                            !q.getHead().contains((Variable) b.getLeft())) { // unbound variable
                        left = new UnboundVariable();
                    }
                }
                if (b.getRight() instanceof Variable) {
                    if ((variableCount.get((Variable) b.getRight()) == 1) &&
                            !q.getHead().contains((Variable) b.getRight())) { // unbound variable
                        right = new UnboundVariable();
                    }
                }
                body.add(b.replaceTerms(left, right));
            }
        }
        return new RewritableQuery(new LinkedList<>(q.getHead()), body);
    }

    /**
     * Given a Xi-restricted query q, and two path atoms in q, return the result of concatenating them.
     * Precondition: they can be concatenated.
     *
     * @param q  Xi-restricted query.
     * @param a1 A single path atom.
     * @param a2 A single path atom.
     * @return A Xi-restricted query q'.
     */
    @Override
    public RewritableQuery concatenate(RewritableQuery q, SinglePathAtom a1, SinglePathAtom a2) {
        return q;
    }

    /**
     * Given a Xi-restricted query q, and two path atoms in q, return the result of merging them.
     * Precondition: they can be merged.
     *
     * @param q  Xi-restricted query.
     * @param a1 A single path atom.
     * @param a2 A single path atom.
     * @return A Xi-restricted query q'.
     */
    @Override
    public RewritableQuery merge(RewritableQuery q, SinglePathAtom a1, SinglePathAtom a2) {
        return q;
    }

    /**
     * Given a Xi-restricted query q, and an arbitrary path atom in q.
     * If the arbitrary length path atom can be dropped, then return the result of dropping it.
     *
     * @param q Xi-restricted query.
     * @param a An arbitrary length single path atom.
     * @return A Xi-restricted query q'.
     */
    @Override
    public RewritableQuery drop(RewritableQuery q, ArbitraryLengthSinglePathAtom a) {
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
        return q;
    }

    /**
     * Replace atom in query
     *
     * @param q Xi-restricted query.
     * @param a A rewritable atom in the query.
     * @param I An OWL QL (DL-Lite) Axiom
     * @return A Xi-restricted query q'.
     */
    @Override
    public RewritableQuery replace(RewritableQuery q, RewritableAtom a, OWLAxiom I) {
        return q;
    }
}
