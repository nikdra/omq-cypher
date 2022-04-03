package at.ac.tuwien.informatics.translation;

import at.ac.tuwien.informatics.structure.query.RewritableQuery;
import at.ac.tuwien.informatics.structure.query.Variable;

import java.util.List;
import java.util.Set;

/**
 * This interface represents translators for our query language to a graph query language
 */
public interface Translator {

    /**
     * Given a list of answer variables and a set of queries, return a string that represents a query over
     * the sources.
     * It can be that due to unification the answer variables in the set of queries have been renamed.
     * Therefore, to ensure consistency of the names of the answer variables, a "ground truth" set of variables
     * might be needed.
     *
     * @param answerVars The answer variables for the query.
     * @param queries The set of queries that should be translated to a query over the sources.
     * @return String representation of the query.
     */
    String translate(List<Variable> answerVars, Set<RewritableQuery> queries);

}
