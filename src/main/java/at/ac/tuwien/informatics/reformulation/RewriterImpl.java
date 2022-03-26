package at.ac.tuwien.informatics.reformulation;

import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.query.*;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of a rewriter for XI-restricted queries.
 */
public class RewriterImpl implements Rewriter {
    // TODO add map of variables?, or add it to the query?


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
        // idea: iterate over atoms, apply role inclusion if it's a path atom
        // otherwise, just add to query
        Set<RewritableAtom> body = new HashSet<>();
        for (Atom a : q.getBody()) {
            if (a instanceof Conceptname) { // Concept name
                body.add((Conceptname) a);
            }
            else if (a instanceof Role) { // Role atom
                body.add((Role) a);
            }
            else { // Path
                body.addAll(((Path) a).saturate(o));
            }
        }
        // TODO for correctness, splitting can't be done individually, but only here
        Set<Variable> head = new HashSet<>(q.getHead());
        return new RewritableQuery(head, body);
    }

    private RewritableQuery splitPaths(RewritableQuery q) {
        // for each path, split it up
        // use counter to add new bound variables
        // unbound variables will be denoted '_'
        return q;
    }

    /**
     * Given a Xi-restricted query q, mark all unbound variables as such.
     *
     * @param q The input query
     * @return A Xi-restricted query q.
     */
    @Override
    public RewritableQuery tau(RewritableQuery q) {
        return null;
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
        return null;
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
        return null;
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
        return null;
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
        return null;
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
        return null;
    }
}
