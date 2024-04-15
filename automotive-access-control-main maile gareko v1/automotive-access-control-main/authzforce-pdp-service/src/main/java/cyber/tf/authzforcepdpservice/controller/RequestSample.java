package cyber.tf.authzforcepdpservice.controller;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attributes;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MultiRequests;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Request;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestDefaults;
import org.jvnet.jaxb2_commons.lang.ToString2;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy2;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;






@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "RequestType",
        propOrder = {"requestDefaults", "attributes", "multiRequests"}
)
@XmlRootElement(
        name = "Request"
)
public class RequestSample implements Serializable, ToString2 {
    private static final long serialVersionUID = 1L;
    @XmlElement(
            name = "RequestDefaults"
    )
    public RequestDefaults requestDefaults;
    @XmlElement(
            name = "Attributes",
            required = true
    )
    public List<Attributes> attributes;
    @XmlElement(
            name = "MultiRequests"
    )
    public MultiRequests multiRequests;
    @XmlAttribute(
            name = "ReturnPolicyIdList",
            required = true
    )
    public boolean returnPolicyIdList;
    @XmlAttribute(
            name = "CombinedDecision",
            required = true
    )
    public boolean combinedDecision;

    @Override
    public StringBuilder append(ObjectLocator objectLocator, StringBuilder stringBuilder, ToStringStrategy2 toStringStrategy2) {
        return null;
    }

    @Override
    public StringBuilder appendFields(ObjectLocator objectLocator, StringBuilder stringBuilder, ToStringStrategy2 toStringStrategy2) {
        return null;
    }

    public RequestDefaults getRequestDefaults() {
        return requestDefaults;
    }

    public void setRequestDefaults(RequestDefaults requestDefaults) {
        this.requestDefaults = requestDefaults;
    }

    public List<Attributes> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attributes> attributes) {
        this.attributes = attributes;
    }

    public MultiRequests getMultiRequests() {
        return multiRequests;
    }

    public void setMultiRequests(MultiRequests multiRequests) {
        this.multiRequests = multiRequests;
    }

    public boolean isReturnPolicyIdList() {
        return returnPolicyIdList;
    }

    public void setReturnPolicyIdList(boolean returnPolicyIdList) {
        this.returnPolicyIdList = returnPolicyIdList;
    }

    public boolean isCombinedDecision() {
        return combinedDecision;
    }

    public void setCombinedDecision(boolean combinedDecision) {
        this.combinedDecision = combinedDecision;
    }

}


