package cz.opendata.linked.literal.normalizer;

import org.junit.Test;
import org.openrdf.rio.RDFFormat;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

public class NormalizeTest {

    @Test
    public void wholeStringsTest() throws Exception {
        // prepare dpu instance and configure it
        Transformer transformer = new Transformer();
        TransformerConfig config = new TransformerConfig();

//        config.setRewriteCache(false);

        transformer.configureDirectly(config);

        // prepare test environment, we use system tmp directory
        TestEnvironment env = TestEnvironment.create();
        // prepare input and output data units

        RDFDataUnit input = env.createRdfInputFromResource(
                "Triples with whole strings to be matched as objects", false,
                "whole-strings.ttl", RDFFormat.TURTLE);
        RDFDataUnit normalized = env.createRdfOutput("Normalized triples", false);

        //assertTrue(input.getTripleCount() > 0);
        try {
            // run the execution
            env.run(transformer);

            normalized.loadToFile("w:\\dip\\tmp\\test.ttl", RDFFormatType.TTL);

            // verify result
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // release resources
            env.release();
        }
    }
}