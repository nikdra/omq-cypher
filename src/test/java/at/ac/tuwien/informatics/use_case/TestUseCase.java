package at.ac.tuwien.informatics.use_case;

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
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUseCase {

    @Test
    public void testUseCaseQueries() throws OWLOntologyCreationException, NotOWL2QLException, IOException {
        String[] q1 = single_query("q(x):-pedestrian(x)", "/use_case/q1");
        String[] q2 = single_query("q(x,xp):-pedestrian(x),LAST_ANNOTATION(x,y),OF(y,z),sample(z)," +
                "NEXT/NEXT*(z,zp),OF(yp,zp),FIRST_ANNOTATION(xp,yp),pedestrian(xp)", "/use_case/q2");
        String[] q3 = single_query("q(z,zp):-pedestrian(x),LAST_ANNOTATION(x,y),OF(y,z),sample(z)," +
                "NEXT/NEXT*(z,zp),OF(yp,zp),FIRST_ANNOTATION(xp,yp),pedestrian(xp)", "/use_case/q3");
        String[] q4 = single_query("q(x):-pedestrian(x),OF(y,x),HAS(y,z),pedestrian_moving(z),NEXT/NEXT*(y,yp)," +
                "HAS(yp,zp),pedestrian_stationary(zp)", "/use_case/q4");
        String[] q5 = single_query("q(x):-pedestrian(x),OF(y,x),HAS(y,z),pedestrian_stationary(z)," +
                "NEXT/NEXT*(y,yp),HAS(yp,zp),pedestrian_moving(zp)", "/use_case/q5");

        List<String[]> data = new LinkedList<>(Arrays.asList(q1, q2, q3, q4, q5));

        File csvOutputFile = new File("src/test/resources/use_case/results.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            data.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
        assertTrue(csvOutputFile.exists());
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public String[] single_query(String query, String filepath) throws OWLOntologyCreationException, NotOWL2QLException,
            IOException {
        File resourcesDirectory = new File("src/test/resources");
        Ontology o = new Ontology(resourcesDirectory.getAbsolutePath() + "/vvr.owl");
        CharStream cs = CharStreams.fromString(query);
        QLexer lexer = new QLexer(cs);
        QParser parser = new QParser(new CommonTokenStream(lexer));
        Rewriter rewriter = new RewriterImpl();
        Translator translator = new CypherTranslator();

        ParseTree tree = parser.query();

        InputQuery q = (InputQuery) new InputQueryBuilder(o).visit(tree);

        long startTime = System.nanoTime();

        // rewrite queries

        Set<RewritableQuery> rewritten_queries = rewriter.rewrite(q, o);

        long endTime = System.nanoTime();

        // duration of rewriting in ms
        long duration = (endTime - startTime) / 1000000;

        // translate to query over the sources
        String translatedQuery = translator.translate(q.getHead(), rewritten_queries);

        // write query to file
        FileWriter fileWriter = new FileWriter(resourcesDirectory.getAbsolutePath() + filepath);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(translatedQuery);
        printWriter.close();

        // return duration and number of queries in UC2RPQ
        return new String[]{Long.toString(duration), Integer.toString(rewritten_queries.size())};
    }
}
