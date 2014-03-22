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

	@Override
	public void execute(DPUContext context)
			throws DPUException,
				DataUnitException {

        rdfInput.copyAllDataToTargetDataUnit(rdfOutput);

        String query = SparqlQueryBuilder.buildQueryFromConfig(config);

        rdfOutput.executeSPARQLUpdateQuery(query);

	}

}
