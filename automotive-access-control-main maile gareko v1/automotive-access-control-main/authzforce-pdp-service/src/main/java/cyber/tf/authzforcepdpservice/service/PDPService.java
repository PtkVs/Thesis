package cyber.tf.authzforcepdpservice.service;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.Request;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Response;
import org.json.JSONObject;
import org.ow2.authzforce.core.pdp.api.CloseablePdpEngine;
import org.ow2.authzforce.core.pdp.api.DecisionRequestPreprocessor;
import org.ow2.authzforce.core.pdp.api.DecisionResultPostprocessor;
import org.ow2.authzforce.core.pdp.api.PdpEngine;
import org.ow2.authzforce.core.pdp.api.io.BaseXacmlJaxbResultPostprocessor;
import org.ow2.authzforce.core.pdp.api.io.PdpEngineInoutAdapter;
import org.ow2.authzforce.core.pdp.api.value.AttributeValueFactoryRegistry;
import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration;
import org.ow2.authzforce.core.pdp.impl.io.PdpEngineAdapters;
import org.ow2.authzforce.core.pdp.impl.io.SingleDecisionXacmlJaxbRequestPreprocessor;
import org.ow2.authzforce.core.pdp.io.xacml.json.BaseXacmlJsonResultPostprocessor;
import org.ow2.authzforce.core.pdp.io.xacml.json.SingleDecisionXacmlJsonRequestPreprocessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class PDPService {
    private final Logger logger = LoggerFactory.getLogger(PDPService.class);

    private final CloseablePdpEngine engine;

    private final PdpEngineInoutAdapter<Request, Response> xmlAdapter;
    private final PdpEngineInoutAdapter<JSONObject, JSONObject> jsonAdapter;

    @Autowired
    public PDPService(PdpEngineConfiguration pdpEngineConfiguration) throws IOException {
        logger.info("Loading XACML PDP Engine.");
        engine = new BasePdpEngine(pdpEngineConfiguration);
        logger.info("Loaded XACML PDP Engine.");

        logger.info("Loading XACML XML Adapter.");
        this.xmlAdapter = PdpEngineAdapters.newInoutAdapter(Request.class,
                                                            Response.class,
                                                            engine,
                                                            pdpEngineConfiguration.getInOutProcChains(),
                                                            (extraPdpFeatures) -> SingleDecisionXacmlJaxbRequestPreprocessor.LaxVariantFactory.INSTANCE.getInstance(pdpEngineConfiguration.getAttributeValueFactoryRegistry(),
                                                                                                                                                                  pdpEngineConfiguration.isStrictAttributeIssuerMatchEnabled(),
                                                                                                                                                                  pdpEngineConfiguration.isXPathEnabled(),
                                                                                                                                                                  extraPdpFeatures),
                                                            () -> new BaseXacmlJaxbResultPostprocessor(pdpEngineConfiguration.getClientRequestErrorVerbosityLevel()));
        logger.info("Loaded XACML XML Adapter.");

        logger.info("Loading XACML JSON Adapter.");
        this.jsonAdapter = PdpEngineAdapters.newInoutAdapter(
                JSONObject.class,
                JSONObject.class,
                engine,
                pdpEngineConfiguration.getInOutProcChains(),
                (extraPdpFeatures) -> SingleDecisionXacmlJsonRequestPreprocessor.LaxVariantFactory.INSTANCE.getInstance(pdpEngineConfiguration.getAttributeValueFactoryRegistry(),
                                                                                                                      pdpEngineConfiguration.isStrictAttributeIssuerMatchEnabled(),
                                                                                                                      pdpEngineConfiguration.isXPathEnabled(),
                                                                                                                      extraPdpFeatures),
                () -> new BaseXacmlJsonResultPostprocessor(pdpEngineConfiguration.getClientRequestErrorVerbosityLevel()));
        logger.info("Loaded XACML JSON Adapter.");
    }

    public PdpEngineInoutAdapter<Request, Response> getXMLAdapter()
    {
        return xmlAdapter;
    }

    public PdpEngineInoutAdapter<JSONObject, JSONObject> getJSONAdapter()
    {
        return jsonAdapter;
    }
}
