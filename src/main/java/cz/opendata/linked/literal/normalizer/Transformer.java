package cz.opendata.linked.literal.normalizer;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsTransformer;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

@AsTransformer
public class Transformer extends ConfigurableBase<TransformerConfig>
		implements ConfigDialogProvider<TransformerConfig> {

    private static final Logger LOG = LoggerFactory.getLogger(Transformer.class);


    @InputDataUnit
	public RDFDataUnit rdfInput;
	
	@OutputDataUnit
	public RDFDataUnit rdfOutput;
	
	public Transformer() {
		super(TransformerConfig.class);
	}

	@Override
	public AbstractConfigDialog<TransformerConfig> getConfigurationDialog() {
		return new TransformerDialog();
	}

        // TODO 2: Implement the method execute being called when the DPU is launched
	@Override
	public void execute(DPUContext context)
			throws DPUException,
				DataUnitException {

        rdfInput.copyAllDataToTargetDataUnit(rdfOutput);

        String query =
                "DELETE { ?s ?p ?o . }\n" +
                "INSERT { ?s ?p ?replacement . }\n" +
                "WHERE {\n";

        Iterator<String> it = config.getToMatch().iterator();
        while(it.hasNext()) {
            String val = it.next();

            query +=
                    "{\n" +
                    "?s ?p ?o . \n" +
                    "FILTER(isLiteral(?o))\n";
            if (config.isRegexp()) {
                query += "FILTER(REGEX(?o, '" + val + "'))\n" +
                        "BIND(REPLACE(?o, '^(.*)" + val + "(.*)$', '$1" + config.getReplacement() + "$2') AS ?replacement)\n";
            } else {
                query += "FILTER(?o = '" + val + "')\n" +
                        "BIND('" + config.getReplacement() + "' AS ?replacement)\n";
            }
            query += "}\n";

            if (it.hasNext()) {
                query += "UNION\n";
            }
        }
        query += "}";
        System.out.println(query);
        rdfOutput.executeSPARQLUpdateQuery(query);
		// DPU's configuration is accessible under 'this.config'
                // DPU's context is accessible under 'context'
                // DPU's data units are accessible under 'rdfInput' and 'rdfOutput'

        //get triples matching list

	}
	
}
