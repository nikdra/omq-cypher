package at.ac.tuwien.informatics.reformulation;

import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.Unifier;
import at.ac.tuwien.informatics.structure.query.*;
import com.google.errorprone.annotations.Var;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import java.util.*;
import java.util.stream.Collectors;

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


        while(!Q.equals(Qp)) {
            Qp = new HashSet<>(Q);
            for (RewritableQuery qp: Qp) {
                // (a) apply axioms, if possible
                for (RewritableAtom a: qp.getBody()) {
                    for (OWLAxiom I: o.getOntology().getAxioms()) {
                        if(a.applicable(I)) {
                            Q.add(tau(replace(qp, a, o, I)));
                        }
                    }
                }

                // (b) unify atoms, if possible
                for (RewritableAtom a1: qp.getBody()) {
                    for (RewritableAtom a2 : qp.getBody()) {
                        Q.add(tau(reduce(qp, a1, a2)));
                    }
                }

                // (c) concatenate, if possible
                for (RewritableAtom a1: qp.getBody()) {
                    for (RewritableAtom a2: qp.getBody()) {
                        if(a1 instanceof Binary && a2 instanceof ArbitraryLengthAtom && !a1.equals(a2)) {
                            Q.add(tau(concatenate(qp, (Binary) a1, (ArbitraryLengthAtom) a2)));
                        }
                    }
                }

                // (d) merge atoms, if possible
                for (RewritableAtom a1: qp.getBody()) {
                    for (RewritableAtom a2: qp.getBody()) {
                        if(a1 instanceof Binary && a2 instanceof Binary) {
                            Q.addAll(merge(qp, (Binary) a1, (Binary) a2).stream()
                                    .map(this::tau)
                                    .collect(Collectors.toSet()));
                        }
                    }
                }

                // (e) drop atoms, if possible
                for (RewritableAtom a1: qp.getBody()) {
                    if (a1 instanceof ArbitraryLengthAtom) {
                        Q.add(tau(drop(qp, (ArbitraryLengthAtom) a1)));
                    }
                }
            }
        }

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
    @Override
    public RewritableQuery concatenate(RewritableQuery q, Binary a1, ArbitraryLengthAtom a2) {
        // small side-not: an unbound variable can become bound here, so we need to create new variables
        RewritableQuery qp;
        Binary r1;
        Binary r2;
        if (a1 instanceof Roles) {
            if (a2.getRoles().containsAll(a1.getRoles())) {  // roles of a1 subset of roles of a2
                // check vars
                if (a1.getLeft().equals(a2.getLeft())) {  // append to front
                    // create a copy of the query
                    qp = new RewritableQuery(new LinkedList<>(q.getHead()), new HashSet<>(q.getBody()));
                    // remove atoms
                    qp.getBody().remove(a1);
                    qp.getBody().remove(a2);
                    // generate new atoms
                    r1 = new Roles(a1.getRoles(), new Variable(a1.getLeft().getName()),
                            new Variable(a1.getRight().getName()));
                    r2 = new ArbitraryLengthAtom(a2.getRoles(), new Variable(a1.getRight().getName()),
                            new Variable(a2.getRight().getName()));
                    // add atoms to query body
                    qp.getBody().add(r1);
                    qp.getBody().add(r2);
                    // return result
                    return qp;
                } else if (a1.getRight().equals(a2.getRight())) {  // append to back
                    // create a copy of the query
                    qp = new RewritableQuery(new LinkedList<>(q.getHead()), new HashSet<>(q.getBody()));
                    // remove atoms
                    qp.getBody().remove(a1);
                    qp.getBody().remove(a2);
                    // generate new atoms
                    r1 = new Roles(a1.getRoles(), new Variable(a1.getLeft().getName()),
                            new Variable(a1.getRight().getName()));
                    r2 = new ArbitraryLengthAtom(a2.getRoles(), new Variable(a2.getLeft().getName()),
                            new Variable(a1.getLeft().getName()));
                    // add atoms to query body
                    qp.getBody().add(r1);
                    qp.getBody().add(r2);
                    // return result
                    return qp;
                }
            } else {
                a1 = ((Roles) a1).getInverse();  // check the inverse
                if (a2.getRoles().containsAll(a1.getRoles())) {
                    // check vars
                    if (a1.getLeft().equals(a2.getLeft())) {  // append to front
                        // create a copy of the query
                        qp = new RewritableQuery(new LinkedList<>(q.getHead()), new HashSet<>(q.getBody()));
                        // remove atoms
                        qp.getBody().remove(a1);
                        qp.getBody().remove(a2);
                        // generate new atoms
                        r1 = new Roles(a1.getRoles(), new Variable(a1.getLeft().getName()),
                                new Variable(a1.getRight().getName()));
                        r2 = new ArbitraryLengthAtom(a2.getRoles(), new Variable(a1.getRight().getName()),
                                new Variable(a2.getRight().getName()));
                        // add atoms to query body
                        qp.getBody().add(r1);
                        qp.getBody().add(r2);
                        // return result
                        return qp;
                    } else if (a1.getRight().equals(a2.getRight())) {  // append to back
                        // create a copy of the query
                        qp = new RewritableQuery(new LinkedList<>(q.getHead()), new HashSet<>(q.getBody()));
                        // remove atoms
                        qp.getBody().remove(a1);
                        qp.getBody().remove(a2);
                        // generate new atoms
                        r1 = new Roles(a1.getRoles(), new Variable(a1.getLeft().getName()),
                                new Variable(a1.getRight().getName()));
                        r2 = new ArbitraryLengthAtom(a2.getRoles(), new Variable(a2.getLeft().getName()),
                                new Variable(a1.getLeft().getName()));
                        // add atoms to query body
                        qp.getBody().add(r1);
                        qp.getBody().add(r2);
                        // return result
                        return qp;
                    }
                }
            }
        } else { // a1 is also an arbitrary length atom
            if (a2.getRoles().containsAll(a1.getRoles())) { // roles of a1 subset of roles of a2
                // check vars
                if (a1.getLeft().equals(a2.getLeft())) {  // append to front
                    // create a copy of the query
                    qp = new RewritableQuery(new LinkedList<>(q.getHead()), new HashSet<>(q.getBody()));
                    // remove atoms
                    qp.getBody().remove(a1);
                    qp.getBody().remove(a2);
                    // generate new atoms
                    r1 = new ArbitraryLengthAtom(a1.getRoles(), new Variable(a1.getLeft().getName()),
                            new Variable(a1.getRight().getName()));
                    r2 = new ArbitraryLengthAtom(a2.getRoles(), new Variable(a1.getRight().getName()),
                            new Variable(a2.getRight().getName()));
                    // add atoms to query body
                    qp.getBody().add(r1);
                    qp.getBody().add(r2);
                    // return result
                    return qp;
                } else if (a1.getRight().equals(a2.getRight())) {  // append to back
                    // create a copy of the query
                    qp = new RewritableQuery(new LinkedList<>(q.getHead()), new HashSet<>(q.getBody()));
                    // remove atoms
                    qp.getBody().remove(a1);
                    qp.getBody().remove(a2);
                    // generate new atoms
                    r1 = new ArbitraryLengthAtom(a1.getRoles(), new Variable(a1.getLeft().getName()),
                            new Variable(a1.getRight().getName()));
                    r2 = new ArbitraryLengthAtom(a2.getRoles(), new Variable(a2.getLeft().getName()),
                            new Variable(a1.getLeft().getName()));
                    // add atoms to query body
                    qp.getBody().add(r1);
                    qp.getBody().add(r2);
                    // return result
                    return qp;
                }
            }
        }
        // if no concat possible, return original query
        return q;
    }

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
    @Override
    public Set<RewritableQuery> merge(RewritableQuery q, Binary a1, Binary a2) {
        Set<OWLObjectPropertyExpression> intersection;
        Set<RewritableQuery> merges = new HashSet<>();
        Binary r1;
        Binary r2;
        Unifier unifier;
        if (a1 instanceof Roles) {
            // compute first intersection
            intersection = new HashSet<>(a1.getRoles());
            intersection.retainAll(a2.getRoles());
            if (intersection.size() > 0) {
                // do the terms of a1 and a2 unify?
                unifier = new Unifier(Arrays.asList(a1.getLeft(), a1.getRight()),
                        Arrays.asList(a2.getLeft(), a2.getRight()));
                if (unifier.getSubstitutions().size() > 0) {
                    // create a copy of the query
                    RewritableQuery qp = new RewritableQuery(new LinkedList<>(q.getHead()), new HashSet<>(q.getBody()));
                    // remove atoms
                    qp.getBody().remove(a1);
                    qp.getBody().remove(a2);
                    // generate new atoms
                    r1 = new Roles(intersection, a1.getLeft(), a1.getRight());
                    r2 = new Roles(intersection, a2.getLeft(), a2.getRight());
                    // add atoms to query body
                    qp.getBody().add(r1);
                    qp.getBody().add(r2);
                    // add the result of unification to the result
                    merges.add(unifier.apply(qp));
                }
            }
            // compute second intersection on the inverse of a1
            a1 = ((Roles) a1).getInverse();
            intersection = new HashSet<>(a1.getRoles());
            intersection.retainAll(a2.getRoles());
            if (intersection.size() > 0) {
                // do the terms of a1 and a2 unify?
                unifier = new Unifier(Arrays.asList(a1.getLeft(), a1.getRight()),
                        Arrays.asList(a2.getLeft(), a2.getRight()));
                if (unifier.getSubstitutions().size() > 0) {
                    // create a copy of the query
                    RewritableQuery qp = new RewritableQuery(new LinkedList<>(q.getHead()), new HashSet<>(q.getBody()));
                    // remove atoms
                    qp.getBody().remove(a1);
                    qp.getBody().remove(a2);
                    // generate new atoms
                    r1 = new Roles(intersection, a1.getLeft(), a1.getRight());
                    r2 = new Roles(intersection, a2.getLeft(), a2.getRight());
                    // add atoms to query body
                    qp.getBody().add(r1);
                    qp.getBody().add(r2);
                    // add the result of unification to the result
                    merges.add(unifier.apply(qp));
                }
            }
        } else {  // arbitary length atom - only directed roles
            intersection = new HashSet<>(a1.getRoles());
            intersection.retainAll(a2.getRoles());
            if (intersection.size() > 0) {
                // do the terms of a1 and a2 unify?
                unifier = new Unifier(Arrays.asList(a1.getLeft(), a1.getRight()),
                        Arrays.asList(a2.getLeft(), a2.getRight()));
                if (unifier.getSubstitutions().size() > 0) {
                    // create a copy of the query
                    RewritableQuery qp = new RewritableQuery(new LinkedList<>(q.getHead()), new HashSet<>(q.getBody()));
                    // remove atoms
                    qp.getBody().remove(a1);
                    qp.getBody().remove(a2);
                    // generate new atoms
                    if (a2 instanceof ArbitraryLengthAtom) {
                        r1 = new ArbitraryLengthAtom(intersection, a1.getLeft(), a1.getRight());
                        r2 = new ArbitraryLengthAtom(intersection, a2.getLeft(), a2.getRight());
                    } else {
                        r1 = new Roles(intersection, a1.getLeft(), a1.getRight());
                        r2 = new Roles(intersection, a2.getLeft(), a2.getRight());
                    }
                    // add atoms to query body
                    qp.getBody().add(r1);
                    qp.getBody().add(r2);

                    // add the result of unification to the result
                    merges.add(unifier.apply(qp));
                }
            }
        }
        return merges;
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
        if (q.getBody().size() > 1 &&
                (a.getLeft() instanceof UnboundVariable || a.getRight() instanceof UnboundVariable)) {
            // create copy of query
            RewritableQuery qp = new RewritableQuery(new LinkedList<>(q.getHead()), new HashSet<>(q.getBody()));
            // remove arb. length atom with unbound variable
            qp.getBody().remove(a);
            // return new query
            return qp;
        }
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
        if (a1 instanceof Conceptname && a2 instanceof Conceptname) {
            Conceptname b1 = (Conceptname) a1;
            Conceptname b2 = (Conceptname) a2;
            if (b1.getName().equals(b2.getName())) {
                // compute unifier, return result of applying the unifier to q
                Unifier unifier = new Unifier(Collections.singletonList(b1.getTerm()),
                        Collections.singletonList(b2.getTerm()));
                return unifier.apply(q);
            }
        } else if (a1 instanceof Roles && a2 instanceof Roles) {
            Roles b1 = (Roles) a1;
            Roles b2 = (Roles) a2;
            if (b1.getRoles().equals(b2.getRoles())) {
                // compute unifier, return result of applying the unifier to q
                Unifier unifier = new Unifier(Arrays.asList(b1.getLeft(), b1.getRight()),
                        Arrays.asList(b2.getLeft(), b2.getRight()));
                return unifier.apply(q);
            } else {
                // it could be that we can unify once we "invert" one of the role atoms
                // in this case, inverting means that we switch the left and right variable
                // and invert all the roles in the set of roles.
                Roles b3 = b1.getInverse();
                if (b3.getRoles().equals(b2.getRoles())) {
                    // no need to create a copy of the query, an inverse is the same as the original atom
                    // create a copy of the query
                    // RewritableQuery qp = new RewritableQuery(new LinkedList<>(q.getHead()), new HashSet<>(q.getBody()));
                    // remove b1
                    // qp.getBody().remove(b1);
                    // add b3
                    // qp.getBody().add(b3);
                    // compute unifier, return result of applying the unifier to q
                    Unifier unifier = new Unifier(Arrays.asList(b3.getLeft(), b3.getRight()),
                            Arrays.asList(b2.getLeft(), b2.getRight()));
                    return unifier.apply(q);
                }
            }
        } else if (a1 instanceof ArbitraryLengthAtom && a2 instanceof ArbitraryLengthAtom) {
            ArbitraryLengthAtom b1 = (ArbitraryLengthAtom) a1;
            ArbitraryLengthAtom b2 = (ArbitraryLengthAtom) a2;
            if (b1.getRoles().equals(b2.getRoles())) {
                // compute unifier, return result of applying the unifier to q
                Unifier unifier = new Unifier(Arrays.asList(b1.getLeft(), b1.getRight()),
                        Arrays.asList(b2.getLeft(), b2.getRight()));
                return unifier.apply(q);
            }
        }
        return q;
    }

    /**
     * Replace atom in query.
     * Assumes that the axiom is applicable to the atom.
     *
     * @param q Xi-restricted query.
     * @param a A rewritable atom in the query.
     * @param o The ontology wrapper object.
     * @param I An OWL QL (DL-Lite) Axiom.
     * @return A Xi-restricted query q'.
     */
    @Override
    public RewritableQuery replace(RewritableQuery q, RewritableAtom a, Ontology o, OWLAxiom I) {
        List<Variable> head = new LinkedList<>(q.getHead());
        Set<RewritableAtom> body = new HashSet<>(q.getBody());
        body.remove(a);
        body.add(a.apply(I, o, this));
        return new RewritableQuery(head, body);
    }

    @Override
    public String getFreshVariableName() {
        return "v" + ++this.variable_counter;
    }
}
