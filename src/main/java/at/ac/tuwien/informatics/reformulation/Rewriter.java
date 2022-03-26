package at.ac.tuwien.informatics.reformulation;

import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.query.*;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.Set;

/**
 * An interface describing the functions a rewriter must implement.
 */
public interface Rewriter {

    /**
     * Given a Xi-restricted query q, rewrite q into a set of queries such that the evaluation over the data returns
     * all the certain answers in the KB.
     *
     * @param q The input query.
     * @param o The ontology.
     * @return Set of queries.
     */
    Set<RewritableQuery> rewrite(InputQuery q, Ontology o);

    /**
     * Given a Xi-restricted query q, apply disjunction to the path atoms occurring in the query.
     * Then, split multi-element path atoms into single path atoms.
     *
     * @param q The input query.
     * @param o The ontology.
     * @return A Xi-restricted query q.
     */
    RewritableQuery saturatePaths(InputQuery q, Ontology o);

    /**
     * Given a Xi-restricted query q, mark all unbound variables as such.
     *
     * @param q The input query
     * @return A Xi-restricted query q.
     */
    RewritableQuery tau(RewritableQuery q);

    /**
     * Given a Xi-restricted query q, and two path atoms in q, return the result of concatenating them.
     * Precondition: they can be concatenated.
     *
     * @param q Xi-restricted query.
     * @param a1 A single path atom.
     * @param a2 A single path atom.
     * @return A Xi-restricted query q'.
     */
    RewritableQuery concatenate(RewritableQuery q, SinglePathAtom a1, SinglePathAtom a2);

    /**
     * Given a Xi-restricted query q, and two path atoms in q, return the result of merging them.
     * Precondition: they can be merged.
     *
     * @param q Xi-restricted query.
     * @param a1 A single path atom.
     * @param a2 A single path atom.
     * @return A Xi-restricted query q'.
     */
    RewritableQuery merge(RewritableQuery q, SinglePathAtom a1, SinglePathAtom a2);

    /**
     * Given a Xi-restricted query q, and an arbitrary path atom in q.
     * If the arbitrary length path atom can be dropped, then return the result of dropping it.
     *
     * @param q Xi-restricted query.
     * @param a An arbitrary length single path atom.
     * @return A Xi-restricted query q'.
     */
    RewritableQuery drop(RewritableQuery q, ArbitraryLengthSinglePathAtom a);

    /**
     * Given a Xi-restricted query q, and two atoms in q.
     * If the atoms can be unified, apply the most general unifier to q.
     *
     * @param q Xi-restricted query.
     * @param a1 A rewritable atom in the query.
     * @param a2 A rewritable atom in the query.
     * @return A Xi-restricted query q'.
     */
    RewritableQuery reduce(RewritableQuery q, RewritableAtom a1, RewritableAtom a2);

    /**
     * Replace atom in query
     *
     * @param q Xi-restricted query.
     * @param a A rewritable atom in the query.
     * @param I An OWL QL (DL-Lite) Axiom
     * @return A Xi-restricted query q'.
     */
    RewritableQuery replace(RewritableQuery q, RewritableAtom a, OWLAxiom I);
}