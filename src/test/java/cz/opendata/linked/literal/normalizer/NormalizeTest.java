package cz.opendata.linked.literal.normalizer;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.rio.RDFFormat;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class NormalizeTest {

    public static final String OUTPUT_DIR = "w:\\dip\\tmp\\test-output\\";

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

        config.setCondition("?s ?p ?o .");
        config.setTripleToDelete("?s ?p ?o .");
    }

    @Test
    public void wholeStringsTest() throws Exception {
        // setup transformation
        String replacement = "CZK";
        List<String> list = new LinkedList<>();
        list.add("czk");
        list.add("CZK");
        list.add("kč");
        list.add("Kč");
        System.out.println(config.toString());

        config.setRegexp(false);
        runTest(replacement, list, "whole-strings", "whole-strings", 4);
    }

    @Test
    public void regexpTest() throws Exception {
        // setup transformation
        String replacement = "s. r. o.";
        List<String> list = new LinkedList<>();
        list.add("s\\\\.r\\\\.o\\\\.");
        list.add("spol\\\\. s r\\\\. o\\\\.");
        list.add("spol\\\\. s r\\\\.o\\\\.");

        config.setRegexp(true);
        runTest(replacement, list, "regexp", "regexp", 0, 4);
    }

    @Test
    public void simpleCaseSensitiveTest() throws Exception {
        // setup transformation
        String replacement = "replaced";

        List<String> list = new LinkedList<>();
        list.add("lowercase");

        config.setRegexp(false);
        config.setCaseSensitive(true);

        runTest(replacement, list, "regexp-case", "simple-case-sensitive", 1);
    }

    @Test
    public void simpleCaseInsensitiveTest() throws Exception {
        // setup transformation
        String replacement = "replaced";

        List<String> list = new LinkedList<>();
        list.add("lowercase");

        config.setRegexp(false);
        config.setCaseSensitive(false);

        runTest(replacement, list, "regexp-case", "simple-case-insensitive", 2);
    }

    @Test
    public void largerCaseInsensitiveTest() throws Exception {
        // setup transformation
        String replacement = "replaced";

        List<String> list = new LinkedList<>();
        list.add("lowercase");
        list.add("other");

        config.setRegexp(false);
        config.setCaseSensitive(false);

        runTest(replacement, list, "case-alternative", "larger-case-insensitive", 4);
    }

    @Test
    public void largerCaseInsensitiveLangTest() throws Exception {
        // setup transformation
        String replacement = "replaced";

        List<String> list = new LinkedList<>();
        list.add("lowercase");
        list.add("other");

        config.setRegexp(false);
        config.setCaseSensitive(false);
        config.setLanguage("en");

        runTest(replacement, list, "case-alternative-langs", "larger-case-insensitive-lang", 2);
    }

    @Test
    public void regexCaseSensitiveTest() throws Exception {
        // setup transformation
        String replacement = "replaced";
        List<String> list = new LinkedList<>();
        list.add("lowercase");

        config.setRegexp(true);
        config.setCaseSensitive(true);

        runTest(replacement, list, "regexp-case", "regexp-case-sensitive", 1);
    }

    @Test
    public void regexCaseInsensitiveTest() throws Exception {
        // setup transformation
        String replacement = "replaced";
        List<String> list = new LinkedList<>();
        list.add("lowercase");

        config.setRegexp(true);
        config.setCaseSensitive(false);

        runTest(replacement, list, "regexp-case", "regexp-case-insensitive", 2);
    }

    @Test
    public void simpleConditionTest() throws Exception {
        // setup transformation
        String replacement = "replaced";
        List<String> list = new LinkedList<>();
        list.add("to replace");

        String triple = "?s <http://purl.org/dc/terms/title> ?o";
        config.setTripleToDelete(triple);
        config.setCondition(triple);
        config.setRegexp(false);

        runTest(replacement, list, "simple-condition", "simple-condition", 1);
    }

    @Test
    public void complexConditionTest() throws Exception {
        // setup transformation
        String replacement = "replaced";
        List<String> list = new LinkedList<>();
        list.add("to replace");

        String triple = "?s <http://purl.org/dc/terms/title> ?o";
        config.setTripleToDelete(triple);
        config.setCondition(triple + " ;" +
                "a <http://normalizer.literal.linked.opendata.cz/test/Business> .");
        config.setRegexp(false);

        runTest(replacement, list, "complex-condition", "complex-condition", 1);
    }

    @Test
    public void languageNotSetSimpleTest() throws Exception {
        // setup transformation
        String replacement = "replaced";
        List<String> list = new LinkedList<>();
        list.add("to replace");

        String triple = "?s ?p ?o";
        config.setTripleToDelete(triple);
        config.setCondition(triple);
        config.setLanguage("");
        config.setRegexp(false);

        runTest(replacement, list, "languages", "languages-simple", 1);
    }

    @Test
    public void languageSetSimpleTest() throws Exception {
        // setup transformation
        String replacement = "replaced";
        List<String> list = new LinkedList<>();
        list.add("to replace");

        String triple = "?s ?p ?o";
        config.setTripleToDelete(triple);
        config.setCondition(triple);
        config.setLanguage("en");
        config.setRegexp(false);

        runTest(replacement, list, "languages", "languages-set-simple", 1);
    }

    @Test
    public void languageNotSetRegexpTest() throws Exception {
        // setup transformation
        String replacement = "replaced";
        List<String> list = new LinkedList<>();
        list.add("to replace");

        String triple = "?s ?p ?o";
        config.setTripleToDelete(triple);
        config.setCondition(triple);
        config.setLanguage("");
        config.setRegexp(true);

        runTest(replacement, list, "languages", "languages-regexp", 1);
    }

    @Test
    public void languageSetRegexpTest() throws Exception {
        // setup transformation
        String replacement = "replaced";
        List<String> list = new LinkedList<>();
        list.add("to replace");

        String triple = "?s ?p ?o";
        config.setTripleToDelete(triple);
        config.setCondition(triple);
        config.setLanguage("cs");
        config.setRegexp(true);

        runTest(replacement, list, "languages", "languages-set-regexp", 1);
    }

    private void runTest(String replacement, List<String> matches, String inputFile, String outputFile, int expectedExactCount) {
        runTest(replacement, matches, inputFile, outputFile, expectedExactCount, 0);
    }

    private void runTest(String replacement, List<String> matches, String inputFile, String outputFile, int expectedExactCount, int expectedPartialCount) {
        // setup transformation
        config.setReplacement(replacement);
        config.setToMatch(matches);


        // setup data units
        try {
            transformer.configureDirectly(config);
            input = env.createRdfInputFromResource("input", false, inputFile + ".ttl", RDFFormat.TURTLE);
            normalized = env.createRdfOutput("output", false);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        assertTrue(input.getTripleCount() > 0);

        // run and assert
        try {
            System.out.println("Running test " + inputFile + " => " + outputFile);
            env.run(transformer);

            printResultToFile(outputFile);

            if (expectedExactCount > 0) {
                expectExactMatches(replacement, expectedExactCount);
            }
            if (expectedPartialCount > 0) {
                expectPartialMatches(replacement, expectedPartialCount);
            }


            assertTrue(input.getTripleCount() == normalized.getTripleCount());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            env.release();
        }
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

    private void expectPartialMatches(String replacement, int expectedMatches) {
        int matches = 0;
        List<Statement> triples = normalized.getTriples();
        for (int i = 0; i < triples.size(); i++) {
            if (triples.get(i).getObject().stringValue().contains(replacement)) {
                matches += 1;
            }
        }
        assertTrue(matches == expectedMatches);
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

    private void expectLanguageMatches(String lang, int expectedMatches) {
        int matches = 0;
        List<Statement> triples = normalized.getTriples();
        for (int i = 0; i < triples.size(); i++) {
            System.out.println(triples.get(i).getObject());
            System.out.println(triples.get(i).getObject().stringValue());
            if (((LiteralImpl) triples.get(i).getObject()).getLanguage().equals(lang)) {
                matches += 1;
            }
        }
        assertTrue(matches == expectedMatches);
    }
}