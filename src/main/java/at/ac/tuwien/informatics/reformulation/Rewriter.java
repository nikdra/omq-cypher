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
     * Given a Xi-restricted query q, and two binary atoms in q, return the result of concatenating them, assuming
     * they can be concatenated i.e., they have a nonempty intersection of roles, terms in the correct
     * places and one of them is of arbitrary length.
     * Note that even in the presence of inverses in {@link Roles}, there is only one possible way of concatenating the
     * atom with an arbitrary length atom.
     *
     * @param q  Xi-restricted query.
     * @param a1 A binary atom.
     * @param a2 An arbitrary length atom.
     * @return A Xi-restricted query q'.
     */
    RewritableQuery concatenate(RewritableQuery q, Binary a1, ArbitraryLengthAtom a2);

    /**
     * Given a Xi-restricted query q, and two binary atoms in q, return the result of merging them, assuming
     * they can be merged i.e., they have a nonempty intersection of roles.
     * Note that in the case of {@link Roles}, it can happen that there are two different results for merging
     * because of the inverse roles occurring in the roles.
     *
     * @param q Xi-restricted query.
     * @param a1 A single path atom.
     * @param a2 A single path atom.
     * @return A set of Xi-restricted queries Q' (can be empty).
     */
    Set<RewritableQuery> merge(RewritableQuery q, Binary a1, Binary a2);

    /**
     * Given a Xi-restricted query q, and an arbitrary path atom in q.
     * If the arbitrary length path atom can be dropped, then return the result of dropping it.
     *
     * @param q Xi-restricted query.
     * @param a An arbitrary length single path atom.
     * @return A Xi-restricted query q'.
     */
    RewritableQuery drop(RewritableQuery q, ArbitraryLengthAtom a);

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
     * @param o The ontology wrapper object.
     * @param I An OWL QL (DL-Lite) Axiom
     * @return A Xi-restricted query q'.
     */
    RewritableQuery replace(RewritableQuery q, RewritableAtom a, Ontology o, OWLAxiom I);

    /**
     * Get a fresh variable name, which has not occurred in any query yet.
     *
     * @return new Variable name as a String.
     */
    String getFreshVariableName();
}
