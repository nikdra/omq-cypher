package at.ac.tuwien.informatics.translation;

import at.ac.tuwien.informatics.structure.query.*;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class translates a CRPQ to Cypher.
 * Attention: This translator is not supported by opencypher, only Neo4j's Cypher implementation.
 */
public class CypherTranslator implements Translator {

    /**
     * Given a list of answer variables and a set of queries, return a string that represents a query over
     * the sources.
     * It can be that due to unification the answer variables in the set of queries have been renamed.
     * Therefore, to ensure consistency of the names of the answer variables, a "ground truth" set of variables
     * might be needed.
     *
     * @param answerVars The answer variables for the query.
     * @param queries    The set of queries that should be translated to a query over the sources.
     * @return String representation of the query.
     */
    @Override
    public String translate(List<Variable> answerVars, Set<RewritableQuery> queries) {
        Set<String> queryStrings = queries.stream().map(q -> queryToCypher(answerVars, q)).collect(Collectors.toSet());
        return String.join("\nunion\n", queryStrings);
    }

    /**
     * Translate a single query to a Cypher query.
     *
     * @param answerVars The answer variables to the query.
     * @param q The query to be translated into Cypher
     * @return A string representation of the query in Cypher.
     */
    private String queryToCypher(List<Variable> answerVars, RewritableQuery q) {
        int variableCounter = 0;
        Set<String> matches = new HashSet<>();
        Set<String> dependencies = new HashSet<>();
        for (RewritableAtom atom : q.getBody()) {
            if (atom instanceof Conceptname) {
                String match = "match (" +
                        ((((Conceptname) atom).getTerm() instanceof UnboundVariable) ? "" :
                                ((Conceptname) atom).getTerm().toString()) +
                        ":" +
                        ((Conceptname) atom).getName().getIRI().getFragment() +
                        ")";
                matches.add(match);
            } else if (atom instanceof Roles) {
                // performance consideration: if no mixing of directions, make directed
                if (((Roles) atom).getRoles().stream().noneMatch(p -> p instanceof OWLObjectInverseOf)) {
                    // all directed
                    String match = "match (" +
                            ((Roles) atom).getLeft().getName() +
                            ")-[" +
                            ":" +
                            String.join("|", ((Roles) atom).getRoles().stream().map(
                                    p -> p.getNamedProperty().getIRI().getFragment()
                            ).collect(Collectors.toSet())) +
                            "]->(" +
                            ((Roles) atom).getRight().getName() +
                            ")";
                    matches.add(match);
                } else if (((Roles) atom).getRoles().stream().allMatch(p -> p instanceof OWLObjectInverseOf)) {
                    // all directed inverses
                    String match = "match (" +
                            ((Roles) atom).getLeft().getName() +
                            ")<-[" +
                            ":" +
                            String.join("|", ((Roles) atom).getRoles().stream().map(
                                    p -> p.getNamedProperty().getIRI().getFragment()
                            ).collect(Collectors.toSet())) +
                            "]-(" +
                            ((Roles) atom).getRight().getName() +
                            ")";
                    matches.add(match);
                } else {
                    // mixing of directions
                    String match = "match (" +
                            ((Roles) atom).getLeft().getName() +
                            ")-[r" +
                            ++variableCounter +
                            ":" +
                            String.join("|", ((Roles) atom).getRoles().stream().map(
                                    p -> p.getNamedProperty().getIRI().getFragment()
                            ).collect(Collectors.toSet())) +
                            "]-(" +
                            ((Roles) atom).getRight().getName() +
                            ")";
                    matches.add(match);
                    Set<String> atomdependencies = new HashSet<>();
                    for (OWLObjectPropertyExpression p : ((Roles) atom).getRoles()) {
                        String dependency = "(startnode(r" +
                                variableCounter +
                                ")=" +
                                ((p instanceof OWLObjectInverseOf) ? ((Roles) atom).getRight().getName() :
                                        ((Roles) atom).getLeft().getName()) +
                                " and type(r" +
                                variableCounter +
                                ")=\"" +
                                p.getNamedProperty().getIRI().getFragment() +
                                "\")";
                        atomdependencies.add(dependency);
                    }
                    dependencies.add("(" + String.join(" or ", atomdependencies) + ")");
                }
            } else { // Arbitrary length atom
                String match = "match (" +
                        ((ArbitraryLengthAtom) atom).getLeft().getName() +
                        ")-[" +
                        ":" +
                        String.join("|", ((ArbitraryLengthAtom) atom).getRoles().stream().map(
                                p -> p.getNamedProperty().getIRI().getFragment()
                        ).collect(Collectors.toSet())) +
                        "*0..]->(" +
                        ((ArbitraryLengthAtom) atom).getRight().getName() +
                        ")";
                matches.add(match);
            }
        }
        String returnClause = "return ";
        if (answerVars.size() == 0) {
            returnClause += "1";
        } else {
            returnClause += IntStream.range(0, Math.min(answerVars.size(), q.getHead().size()))
                    .mapToObj(i -> q.getHead().get(i).getName() + " as " + answerVars.get(i).getName())
                    .collect(Collectors.joining(", "));
        }
        return String.join("\n", matches) + "\n" +
                (dependencies.size() > 0 ? "where " + String.join(" and ", dependencies) + "\n" : "") +
                returnClause;
    }
}
