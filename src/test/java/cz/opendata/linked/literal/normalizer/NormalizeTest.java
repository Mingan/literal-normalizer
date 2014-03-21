package cz.opendata.linked.literal.normalizer;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import org.junit.Test;
import org.openrdf.rio.RDFFormat;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class NormalizeTest {

    @Test
    public void wholeStringsTest() throws Exception {
        // prepare dpu instance and configure it
        Transformer transformer = new Transformer();
        TransformerConfig config = new TransformerConfig();

        String replacement = "CZK";

        List<String> list = new LinkedList<>();
        list.add("czk");
        list.add("CZK");
        list.add("kč");
        list.add("Kč");
        config.setToMatch(list);
        config.setReplacement(replacement);
        transformer.configureDirectly(config);


        TestEnvironment env = TestEnvironment.create();


        RDFDataUnit input = null;
        RDFDataUnit normalized = null;
        try {
            input = env.createRdfInputFromResource(
                    "input", false,
                    "whole-strings.ttl", RDFFormat.TURTLE);
            normalized = env.createRdfOutput("output", false);
        } catch (RDFException e) {
            e.printStackTrace();
        }

        assertTrue(input.getTripleCount() > 0);
        try {
            // run the execution
            env.run(transformer);

            int matches = 0;
            int expectedMatches = 4;
            List<org.openrdf.model.Statement> triples = normalized.getTriples();
            for (int i = 0; i < triples.size(); i++) {
                System.out.println(triples.get(i).getObject().stringValue() + " " + replacement);
                System.out.println(triples.get(i).getObject().stringValue() == replacement.toString());
                if (triples.get(i).getObject().stringValue() == replacement) {
                    matches += 1;
                }
            }


            try {
                Random rg = new Random();
                normalized.loadToFile("w:\\dip\\tmp\\whole-strings-" + rg.nextInt() + ".ttl", RDFFormatType.TTL);
            } catch (CannotOverwriteFileException e) {
                e.printStackTrace();
            } catch (RDFException e) {
                e.printStackTrace();
            }

            System.out.println("Value matched " + matches + " of " + expectedMatches + " times");
            // verify result
            assertTrue(input.getTripleCount() == normalized.getTripleCount());
            assertTrue(matches == expectedMatches);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // release resources
            env.release();
        }
    }
}