package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.reformulation.Rewriter;
import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.Substitution;
import org.semanticweb.owlapi.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A class that represents a role atom (R \cup ...)(x,y) in the query for some role names "R" (can be inverse!)
 * and variables "x","y".
 */
public class Roles implements Binary {

    /**
     * The names of the roles.
     */
    private final Set<OWLObjectPropertyExpression> roles;
    /**
     * The term on the left.
     */
    private final Term left;
    /**
     * The term on the right.
     */
    private final Term right;

    /**
     * Initialize a new Role object.
     * @param roles The roles/properties in this atom.
     * @param left The left {@link Term}.
     * @param right The right {@link Term}.
     */
    public Roles(Set<OWLObjectPropertyExpression> roles, Term left, Term right) {
        this.roles = roles;
        this.left = left;
        this.right = right;
    }

    @Override
    public int hashCode() {
        return this.hashCodePart() * this.getInverse().hashCodePart();
    }

    private int hashCodePart() {
        int hash = 3;
        hash = 53 * hash + (this.roles != null ? this.roles.hashCode() : 0);
        hash = 53 * hash + (this.left != null ? this.left.hashCode() : 0);
        hash = 53 * hash + (this.right != null ? this.right.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Roles)){
            return false;
        }

        Roles r = (Roles) obj;

        Roles inv = this.getInverse();

        return (this.roles.equals(r.roles) && this.left.toString().equals(r.left.toString())
                && this.right.toString().equals(r.right.toString())) || (inv.roles.equals(r.roles) &&
                inv.left.toString().equals(r.left.toString()) && inv.right.toString().equals(r.right.toString()));
    }

    @Override
    public String toString() {
        String rolestring = this.roles.stream()
                .map(p -> p.getNamedProperty().getIRI().getFragment() + ((p instanceof OWLObjectInverseOf) ? "-" : ""))
                .collect(Collectors.joining("|"));
        if (this.roles.size() > 1) {
            rolestring = '(' + rolestring + ')';
        }
        return rolestring + '(' + this.left.toString() + "," + this.right.toString() + ')';
    }

    /**
     * Return true if the atom can be replaced by another atom given an axiom.
     *
     * @param I The axiom to be applied.
     * @return True if the axiom is applicable, false otherwise.
     */
    @Override
    public boolean applicable(OWLAxiom I) {
        if (I instanceof OWLSubClassOfAxiom) {  // A \ISA \exists R
            OWLSubClassOfAxiom i = (OWLSubClassOfAxiom) I;
            if (i.getSuperClass() instanceof OWLObjectSomeValuesFrom) {
                if (this.right instanceof UnboundVariable && // A \ISA \exists R, R(x,_)
                        this.roles.contains(((OWLObjectSomeValuesFrom) i.getSuperClass()).getProperty())) {
                    return true;
                }
                // A \ISA \exists R, R-(_,x)
                return this.left instanceof UnboundVariable &&
                        this.roles.contains(((OWLObjectSomeValuesFrom) i.getSuperClass())
                                .getProperty().getInverseProperty());
            }
        } else if (I instanceof OWLObjectPropertyDomainAxiom) { // exists r \ISA \exists R
            // casting to get the domain and making sure it's \exists R
            OWLObjectPropertyDomainAxiom i = (OWLObjectPropertyDomainAxiom) I;
            OWLClassExpression ii = i.getDomain();
            if (ii instanceof OWLObjectSomeValuesFrom) {
                OWLObjectPropertyExpression property = ((OWLObjectSomeValuesFrom) ii).getProperty();
                if (this.right instanceof UnboundVariable && this.roles.contains(property)) {
                    return true;
                }
                return this.left instanceof UnboundVariable && this.roles.contains(property.getInverseProperty());
            }
        } else if (I instanceof OWLObjectPropertyRangeAxiom) {
            OWLObjectPropertyRangeAxiom i = (OWLObjectPropertyRangeAxiom) I;
            OWLClassExpression ii = i.getRange();
            if (ii instanceof OWLObjectSomeValuesFrom) {
                OWLObjectPropertyExpression property = ((OWLObjectSomeValuesFrom) ii).getProperty();
                if (this.right instanceof UnboundVariable && this.roles.contains(property)) {
                    return true;
                }
                return this.left instanceof UnboundVariable && this.roles.contains(property.getInverseProperty());
            }
        }
        return false;
    }

    /**
     * Apply a replacement by an axiom on this atom and return the new atom.
     * <p>
     * Precondition for correctness: applicable was called before.
     *
     * @param I The axiom to be applied.
     * @param o The ontology.
     * @param rewriter The rewriter that called this function.
     * @return The new atom.
     */
    @Override
    public RewritableAtom apply(OWLAxiom I, Ontology o, Rewriter rewriter) {
        // note: if both variables are unbound, we don't care which one we use to rewrite the atom
        if (I instanceof OWLSubClassOfAxiom) {  // A \ISA \exists R
            OWLSubClassOfAxiom i = (OWLSubClassOfAxiom) I;
            if (i.getSuperClass() instanceof OWLObjectSomeValuesFrom) {
                if (this.right instanceof UnboundVariable && // A \ISA \exists R, R(x,_)
                        this.roles.contains(((OWLObjectSomeValuesFrom) i.getSuperClass()).getProperty())) {
                    // return A(x)
                    return new Conceptname((OWLClass) i.getSubClass(), this.left.getFresh());
                }
                // A \ISA \exists R, R-(_,y)
                // return A(y)
                return new Conceptname((OWLClass) i.getSubClass(), this.right.getFresh());
            }
        } else if (I instanceof OWLObjectPropertyDomainAxiom) { // exists r \ISA \exists R
            Roles newatom;
            // casting to get the domain and making sure it's \exists R
            OWLObjectPropertyDomainAxiom i = (OWLObjectPropertyDomainAxiom) I;
            OWLClassExpression ii = i.getDomain();
            if (ii instanceof OWLObjectSomeValuesFrom) {
                OWLObjectPropertyExpression property = ((OWLObjectSomeValuesFrom) ii).getProperty();
                if (this.right instanceof UnboundVariable && this.roles.contains(property)) {
                    // R(x,_)
                    newatom = new Roles(new HashSet<>(
                            Collections.singleton(i.getProperty())),
                            this.left.getFresh(), this.right.getFresh());
                } else {
                    // R-(_,y)
                    newatom = new Roles(new HashSet<>(
                            Collections.singleton(i.getProperty().getInverseProperty())),
                            this.left.getFresh(), this.right.getFresh());
                }
                newatom.saturate(o);
                return newatom;
            }
        }
        // exists r- \ISA \exists R
        Roles newatom;
        OWLObjectPropertyRangeAxiom i = (OWLObjectPropertyRangeAxiom) I;
        OWLClassExpression ii = i.getRange();
        OWLObjectPropertyExpression property = ((OWLObjectSomeValuesFrom) ii).getProperty();
        if (this.right instanceof UnboundVariable && this.roles.contains(property)) {
            // R(x,_)
            newatom = new Roles(new HashSet<>(Collections.singleton(i.getProperty())), this.right.getFresh(),
                    this.left.getFresh());
        } else {
            // R-(_,y)
            newatom = new Roles(new HashSet<>(Collections.singleton(i.getProperty().getInverseProperty())),
                    this.right.getFresh(), this.left.getFresh());
        }
        newatom.saturate(o);
        return newatom;
    }

    /**
     * Apply a list of substitutions to the terms of an atom.
     *
     * @param substitutions A list of substitutions.
     * @return A new RewritableAtom with the substitutions applied to its terms.
     */
    @Override
    public RewritableAtom applySubstitution(List<Substitution> substitutions) {
        Term left = this.left.getFresh();
        Term right = this.right.getFresh();

        for (Substitution sub : substitutions) {
            left = left.applySubstitution(sub);
            right = right.applySubstitution(sub);
        }
        return new Roles(new HashSet<>(this.roles), left, right);
    }

    public void saturate(Ontology o) {
        Set<OWLObjectPropertyExpression> subroles = new HashSet<>();
        while (!subroles.equals(this.roles)) {
            subroles = new HashSet<>(this.roles);
            // iterate over all the axioms for the roles that have r or r- on the right side
            for (OWLObjectPropertyExpression r : subroles) {
                // R1 \ISA R
                this.roles.addAll(o.getOntology().getObjectSubPropertyAxiomsForSuperProperty(r)
                        .stream()
                        .map(OWLSubObjectPropertyOfAxiom::getSubProperty)
                        .collect(Collectors.toSet()));
                // R1 \ISA R-
                this.roles.addAll(o.getOntology().getObjectSubPropertyAxiomsForSuperProperty(r.getInverseProperty())
                        .stream()
                        .map(OWLSubObjectPropertyOfAxiom::getSubProperty)
                        .map(OWLObjectPropertyExpression::getInverseProperty)
                        .collect(Collectors.toSet()));
                // inverses
                // use the named property (in case of inverse), and add the inverse of the inverse in case
                // r is an inverse itself.
                this.roles.addAll(o.getOntology().getInverseObjectPropertyAxioms(r.getNamedProperty())
                        .stream()
                        .map(p -> p.getPropertiesMinus(r.getNamedProperty()))
                        .flatMap(Collection::stream)
                        .map(p -> (r instanceof OWLObjectInverseOf) ? p : p.getInverseProperty())
                        .collect(Collectors.toSet()));
            }
        }
    }

    @Override
    public Set<OWLObjectPropertyExpression> getRoles() {
        return this.roles;
    }

    @Override
    public Term getLeft() {
        return this.left.getFresh();
    }

    @Override
    public Term getRight() {
        return this.right.getFresh();
    }

    /**
     * Create a new role atom with the given terms.
     *
     * @param left The left {@link Term}.
     * @param right The right {@link Term}.
     * @return A new role atom, that can be rewritten.
     */
    @Override
    public Roles replaceTerms(Term left, Term right) {
        return new Roles(new HashSet<>(this.roles), left, right);
    }

    public Roles getInverse() {
        Set<OWLObjectPropertyExpression> inverses = this.roles.stream()
                .map(OWLObjectPropertyExpression::getInverseProperty)
                .collect(Collectors.toSet());
        return new Roles(inverses, this.right.getFresh(), this.left.getFresh());
    }
}
