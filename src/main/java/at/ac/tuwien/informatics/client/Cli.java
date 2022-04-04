package at.ac.tuwien.informatics.client;

import at.ac.tuwien.informatics.generated.QLexer;
import at.ac.tuwien.informatics.generated.QParser;
import at.ac.tuwien.informatics.reformulation.Rewriter;
import at.ac.tuwien.informatics.reformulation.RewriterImpl;
import at.ac.tuwien.informatics.structure.InputQueryBuilder;
import at.ac.tuwien.informatics.structure.Ontology;
import at.ac.tuwien.informatics.structure.exception.NotOWL2QLException;
import at.ac.tuwien.informatics.structure.query.InputQuery;
import at.ac.tuwien.informatics.structure.query.RewritableQuery;
import at.ac.tuwien.informatics.translation.CypherTranslator;
import at.ac.tuwien.informatics.translation.Translator;
import com.fasterxml.jackson.core.util.BufferRecycler;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

public class Cli {

    public static void main(String[] args) throws IOException {
        Ontology ontology;
        Translator translator = new CypherTranslator();
        Rewriter rewriter = new RewriterImpl();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // read in ontology
        System.out.println("please enter the path to the ontology file you want to work with");
        String ontology_path = br.readLine();
        try {
            ontology = new Ontology(ontology_path);
        } catch (OWLOntologyCreationException e) {
            System.out.println("Something went wrong loading the ontology");
            System.out.println(e.getMessage());
            return;
        } catch (NotOWL2QLException e) {
            System.out.println("The given ontology was not in OWL2 QL");
            return;
        }

        // read query
        System.out.println("Please enter the query you want to rewrite with the ontology");
        String queryString = br.readLine();

        // parse query
        CharStream cs = CharStreams.fromString(queryString);
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));

        ParseTree tree = parser.query();

        if (parser.query().exception != null) {
            System.out.println("Your input query was invalid (Probably an extra whitespace).");
        }

        InputQuery q = (InputQuery) new InputQueryBuilder(ontology).visit(tree);

        // rewrite query
        Set<RewritableQuery> rewritten_queries = rewriter.rewrite(q, ontology);

        // print rewritten queries
        rewritten_queries.forEach(System.out::println);

        // translate to query over the sources
        String translatedQuery = translator.translate(q.getHead(), rewritten_queries);

        // copy query to clipboard
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection strSel = new StringSelection(translatedQuery);
        clipboard.setContents(strSel, null);

        // "done" message
        System.out.println("Your query has been rewritten. It has been copied to your system clipboard.");
    }
}
