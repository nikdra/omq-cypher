package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.Ontology;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An interface that represents an element of a path \rho(x,y).
 * Can be arbitrary length or single length.
 */
public abstract class PathElement {

    /**
     * The set (disjunction) of role names occurring in this path element.
     */
    protected Set<String> rolenames;

    /**
     * Initialize a new path element.
     *
     * @param rolenames the set (disjunction) of role names occuring in this path element.
     */
    public PathElement(Set<String> rolenames) {
        this.rolenames = rolenames;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.rolenames != null ? this.rolenames.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        if (this.rolenames.size() == 1) {
            return this.rolenames.iterator().next();
        } else {
            return "(" + String.join("|", this.rolenames) + ")";
        }
    }

    /**
     * Exhaustively apply the subrole axiom to this path element.
     * This means adding each implied subrole of each role in the set of rolenames to the set of rolenames.
     *
     * @param o The ontology.
     */
    public void saturate(Ontology o) {
        Set<OWLObjectPropertyExpression> subroles = new HashSet<>();
        // get the object property object for each role in this path element
        // note: all the object properties occurring in the query must be in the ontology signature
        Set<OWLObjectPropertyExpression> roles = this.rolenames.stream().map(r ->
                o.getPropertyMap().get(r)).collect(Collectors.toSet());
        // exhaustively apply the subrole axioms
        while (!subroles.equals(roles)) {
            subroles = new HashSet<>(roles);
            // iterate over all the axioms for the roles that have r on the right side
            for (OWLObjectPropertyExpression r : subroles) {
                Set<OWLSubObjectPropertyOfAxiom> ax = o.getOntology().getObjectSubPropertyAxiomsForSuperProperty(r);
                roles.addAll(ax.stream().map(OWLSubObjectPropertyOfAxiom::getSubProperty).collect(Collectors.toSet()));
            }
        }
        this.rolenames = subroles.stream()
                .map(p -> ((OWLObjectProperty) p).getIRI().getFragment())
                .collect(Collectors.toSet());
    }

    /**
     * Convert this path element to a single path atom.
     *
     * @param left The left {@link Term}.
     * @param right The right {@link Term}.
     * @return This path element as a rewritable single path atom.
     */
    public abstract SinglePathAtom toSinglePathAtom(Term left, Term right);
}
