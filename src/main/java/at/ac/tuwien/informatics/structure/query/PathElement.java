package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.structure.Ontology;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
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
    protected Set<OWLObjectPropertyExpression> roles;

    /**
     * Initialize a new path element.
     *
     * @param roles the set (disjunction) of role names occuring in this path element.
     */
    public PathElement(Set<OWLObjectPropertyExpression> roles) {
        this.roles = roles;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.roles != null ? this.roles.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        String rolestring = this.roles.stream()
                .map(p -> p.getNamedProperty().getIRI().getFragment() + ((p instanceof OWLObjectInverseOf) ? "-" : ""))
                .collect(Collectors.joining("|"));
        if (this.roles.size() > 1) {
            rolestring = '(' + rolestring + ')';
        }
        return rolestring;
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
        // for _path elements_, saturation only for role names! (guaranteed no inverses).
        // exhaustively apply the subrole axioms
        while (!subroles.equals(this.roles)) {
            subroles = new HashSet<>(this.roles);
            // iterate over all the axioms for the roles that have r on the right side
            for (OWLObjectPropertyExpression r : subroles) {
                this.roles.addAll(o.getOntology().getObjectSubPropertyAxiomsForSuperProperty(r)
                        .stream()
                        .map(OWLSubObjectPropertyOfAxiom::getSubProperty)
                        .collect(Collectors.toSet()));
            }
        }
        this.roles = subroles;
    }

    /**
     * Convert this path element to a single path atom.
     *
     * @param left The left {@link Term}.
     * @param right The right {@link Term}.
     * @return This path element as a rewritable single path atom.
     */
    public abstract Binary toBinary(Term left, Term right);
}
