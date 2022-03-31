package at.ac.tuwien.informatics.structure.query;

import at.ac.tuwien.informatics.reformulation.Rewriter;
import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.Substitution;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.semanticweb.owlapi.model.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * A class that represents a query atom of the form A(_) for a concept name A.
 */
public class Conceptname implements RewritableAtom {

    /**
     * The name of the concept.
     */
    private final OWLClass name;
    /**
     * The term in the atom.
     */
    private final Term term;

    /**
     * Initialize a new Conceptname object.
     * @param name The name of the concept.
     * @param term The {@link Term} in the atom.
     */
    public Conceptname(OWLClass name, Term term) {
        this.name = name;
        this.term = term;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if(!(obj instanceof Conceptname)) {
            return false;
        }

        Conceptname c = (Conceptname) obj;

        return this.name.equals(c.name) && this.term.equals(c.term);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.term != null ? this.term.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.name.getIRI().getFragment() + '(' + this.term.toString() + ')';
    }

    /**
     * Return true if the atom can be replaced by another atom given an axiom.
     *
     * @param I The axiom to be applied.
     * @return True if the axiom is applicable, false otherwise.
     */
    @Override
    public boolean applicable(OWLAxiom I) {
        if (I instanceof OWLSubClassOfAxiom) {
            return ((OWLSubClassOfAxiom) I).getSuperClass().equals(this.name);
        }
        if (I instanceof OWLObjectPropertyDomainAxiom) {
            return ((OWLObjectPropertyDomainAxiom) I).getDomain().equals(this.name);
        }
        if (I instanceof OWLObjectPropertyRangeAxiom) {
            return ((OWLObjectPropertyRangeAxiom) I).getRange().equals(this.name);
        }
        return false;
    }


    /**
     * Apply a replacement by an axiom on this atom and return the new atom.
     * Precondition for correctness: applicable was called before.
     *
     * @param I The axiom to be applied.
     * @param o The ontology.
     * @return The new atom.
     */
    @Override
    public RewritableAtom apply(OWLAxiom I, Ontology o, Rewriter rewriter) {
        if (I instanceof OWLSubClassOfAxiom) { // A_1 ISA A
            OWLSubClassOfAxiom b = (OWLSubClassOfAxiom) I;
            OWLClass subclass = (OWLClass) b.getSubClass();
            return new Conceptname(subclass, this.term.getFresh());
        }
        // domain/range axiom
        UnboundVariable v = new UnboundVariable(rewriter .getFreshVariableName());
        OWLObjectPropertyExpression property;
        if (I instanceof OWLObjectPropertyRangeAxiom) {  // exists r^- ISA A
            property = ((OWLObjectPropertyRangeAxiom) I).getProperty().getInverseProperty();
        } else {  // exists r ISA A
            property = ((OWLObjectPropertyDomainAxiom) I).getProperty();
        }
        Roles roles = new Roles(new HashSet<>(Collections.singleton(property)), this.term.getFresh(), v);
        roles.saturate(o);
        return roles;
    }

    /**
     * Get the Term of the concept name atom.
     * @return The term of this atom.
     */
    public Term getTerm() {
        return this.term.getFresh();
    }

    /**
     * Get the name of the concept in this atom.
     * @return The name of this atom.
     */
    public OWLClass getName() {
        return name;
    }

    /**
     * Apply a list of substitutions to the terms of this atom.
     *
     * @param substitutions A list of substitutions.
     * @return A new Conceptname with the substitutions applied to its terms.
     */
    @Override
    public Conceptname applySubstitution(List<Substitution> substitutions) {
        Term t = this.term.getFresh();
        for (Substitution sub : substitutions) {
            t = t.applySubstitution(sub);
        }
        return new Conceptname(this.name, t);
    }
}
