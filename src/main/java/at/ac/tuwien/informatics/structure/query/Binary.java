package at.ac.tuwien.informatics.structure.query;

import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import java.util.Set;

public interface Binary extends RewritableAtom {

    Term getLeft();

    Term getRight();

    Binary replaceTerms(Term left, Term right);
}
