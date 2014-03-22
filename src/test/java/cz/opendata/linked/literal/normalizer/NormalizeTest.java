package cz.opendata.linked.literal.normalizer;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class NormalizeTest {

    public static final String OUTPUT_DIR = "w:\\dip\\tmp\\";

    private Transformer transformer;
    private TransformerConfig config;
    private TestEnvironment env;
    private RDFDataUnit input;
    private RDFDataUnit normalized;

    @Before
    public void prepare() {
        transformer = new Transformer();
        config = new TransformerConfig();
        env = TestEnvironment.create();
        input = null;
        normalized = null;
    }

    @Test
    public void wholeStringsTest() throws Exception {
        // setup transformation
        String replacement = "CZK";
        config.setReplacement(replacement);

        List<String> list = new LinkedList<>();
        list.add("czk");
        list.add("CZK");
        list.add("kč");
        list.add("Kč");
        config.setToMatch(list);

        transformer.configureDirectly(config);

        // setup data units
        loadDataFromFile("whole-strings");
        normalized = env.createRdfOutput("output", false);

        assertTrue(input.getTripleCount() > 0);


        try {
            env.run(transformer);

            expectExactMatches(replacement, 4);

            printResultToFile("whole-strings");

            assertTrue(input.getTripleCount() == normalized.getTripleCount());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            env.release();
        }
    }

    private void expectExactMatches(String replacement, int expectedMatches) {
        int matches = 0;
        List<Statement> triples = normalized.getTriples();
        for (int i = 0; i < triples.size(); i++) {
            if (triples.get(i).getObject().stringValue().equals(replacement)) {
                matches += 1;
            }
        }
        assertTrue(matches == expectedMatches);
    }

    private void printResultToFile(String file) {
        try {
            Random rg = new Random();
            normalized.loadToFile(OUTPUT_DIR + file + "-" + rg.nextInt() + ".ttl", RDFFormatType.TTL);
        } catch (CannotOverwriteFileException e) {
            e.printStackTrace();
        } catch (RDFException e) {
            e.printStackTrace();
        }
    }

    private void loadDataFromFile(String file) {
        try {
            input = env.createRdfInputFromResource(
                    "input", false,
                    file + ".ttl", RDFFormat.TURTLE);
        } catch (RDFException e) {
            e.printStackTrace();
        }
    }
}