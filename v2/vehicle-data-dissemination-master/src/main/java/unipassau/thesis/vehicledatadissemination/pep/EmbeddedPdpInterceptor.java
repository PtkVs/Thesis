package unipassau.thesis.vehicledatadissemination.pep;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.DecisionType;
import org.ow2.authzforce.core.pdp.api.*;
import org.ow2.authzforce.core.pdp.api.value.*;
import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.ow2.authzforce.xacml.identifiers.XacmlAttributeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unipassau.thesis.vehicledatadissemination.demo.Bob;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

import static org.ow2.authzforce.xacml.identifiers.XacmlAttributeCategory.*;

public class EmbeddedPdpInterceptor {
    private static  final Logger LOG = LoggerFactory
            .getLogger(EmbeddedPdpInterceptor.class);

    private final BasePdpEngine pdp;

    public EmbeddedPdpInterceptor(final BasePdpEngine pdp)
    {
        this.pdp = pdp;
    }

    public boolean authorize(Principal principal, String ressourceID, String actionID) throws Exception
    {
        final DecisionRequest request = createRequest(principal, ressourceID, actionID);
        LOG.info("XACML Request: {}", request);


        // Evaluate the request
        final DecisionResult result = pdp.evaluate(request);
        System.out.println("myresult");
        System.out.println(result);

        if (result == null)
        {
            return false;
        }


        LOG.info("XACML authorization response: {}", result);
        LOG.info("XACML authorization Decision: {}", result.getDecision());

        return result.getDecision() == DecisionType.PERMIT;
    }

    private DecisionRequest createRequest(Principal principal, String ressourceID, String actionID)
    {

        final DecisionRequestBuilder<?> requestBuilder = pdp.newRequestBuilder(3, 3);

        // Add subject ID attribute (access-subject category), no issuer, string value "john"
        final AttributeFqn subjectIdAttributeId = AttributeFqns.newInstance(XACML_1_0_ACCESS_SUBJECT.value(), Optional.empty(), XacmlAttributeId.XACML_1_0_SUBJECT_ID.value());
        final AttributeBag<?> subjectIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue(principal.getName()));
        requestBuilder.putNamedAttributeIfAbsent(subjectIdAttributeId, subjectIdAttributeValues);


        // Add resource ID attribute (resource category), no issuer, string value "/some/resource/location"
        final AttributeFqn resourceIdAttributeId = AttributeFqns.newInstance(XACML_3_0_RESOURCE.value(), Optional.empty(), XacmlAttributeId.XACML_1_0_RESOURCE_ID.value());
        final AttributeBag<?> resourceIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue(ressourceID));
        requestBuilder.putNamedAttributeIfAbsent(resourceIdAttributeId, resourceIdAttributeValues);

        // Add action ID attribute (action category), no issuer, string value "GET"
        final AttributeFqn actionIdAttributeId = AttributeFqns.newInstance(XACML_3_0_ACTION.value(), Optional.empty(), XacmlAttributeId.XACML_1_0_ACTION_ID.value());
        final AttributeBag<?> actionIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue(actionID));
        requestBuilder.putNamedAttributeIfAbsent(actionIdAttributeId, actionIdAttributeValues);


        // When there is no more attributes, we build the request.
        return requestBuilder.build(false);
    }



}

/*
1.Constructor:
-The class has a constructor that takes an instance of BasePdpEngine as a parameter. This engine is likely responsible for making policy
 decisions based on XACML (eXtensible Access Control Markup Language) policies.

2.authorize Method:
-This method takes a Principal (user), a resource ID, and an action ID as parameters and performs an authorization check.
-It creates a DecisionRequest using the createRequest method and evaluates it using the pdp.evaluate method.
-The result of the evaluation is logged, and the method returns true if the decision is PERMIT, indicating that the user is authorized.

3.createRequest Method:
-This method creates a DecisionRequest by adding attributes related to the subject (user), resource, and action to a
 DecisionRequestBuilder.

4.Attributes:
-The class uses the following attributes:
        -pdp: An instance of BasePdpEngine, which is likely responsible for making policy decisions based on XACML policies.
        -LOG: An instance of Logger, which is used for logging.

-Subject ID, resource ID, and action ID are added as attributes to the XACML request.

5.Logging:
-The class uses SLF4J for logging, and it logs information about the XACML request, authorization response, and decision.

**Summary**:
The EmbeddedPdpInterceptor class acts as a bridge between the application and the embedded PDP engine. It creates XACML requests based
on user, resource, and action attributes, evaluates these requests using the PDP engine, and logs the authorization results. The authorize
method returns true if the decision is PERMIT, indicating that the user is authorized to perform the specified action on the
specified resource.
*/
