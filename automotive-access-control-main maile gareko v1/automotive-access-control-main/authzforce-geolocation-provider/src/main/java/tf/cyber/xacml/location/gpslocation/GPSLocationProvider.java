package tf.cyber.xacml.location.gpslocation;

import de.securedimensions.geoxacml.datatype.GeometryValue;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.ow2.authzforce.core.pdp.api.*;
import org.ow2.authzforce.core.pdp.api.value.*;
import org.ow2.authzforce.xacml.identifiers.XacmlStatusCode;

import javax.xml.bind.annotation.XmlRegistry;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@XmlRegistry
public class GPSLocationProvider extends BaseNamedAttributeProvider {
    private final AttributeFqn gpsLocationFqn;
    private final AttributeDesignatorType supportedDesignatorType;
    private final Datatype<?> gpsLocationType = GeometryValue.DATATYPE;

    private final String gpsURL;

    private final HttpClient client;
    private final HttpRequest request;

    public final static String ID = "urn:tf:cyber:xacml:location:gpslocation";

    private GPSLocationProvider(String id, String gpsURL) throws IllegalArgumentException {
        super(ID);
        this.gpsURL = gpsURL;

        this.client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create(gpsURL))
                .build();

        this.gpsLocationFqn = AttributeFqns.newInstance("urn:oasis:names:tc:xacml:3" +
                                                                ".0:attribute-category:environment",
                                                        Optional.empty(),
                                                        "urn:tf:cyber:xacml:location:gpslocation");

        this.supportedDesignatorType = createAttributeDesignator(
                this.gpsLocationFqn,
                GeometryValue.DATATYPE
        );
    }

    @Override
    public Set<AttributeDesignatorType> getProvidedAttributes() {
        return Set.of(this.supportedDesignatorType);
    }

    @Override
    public <AV extends AttributeValue> AttributeBag<AV> get(AttributeFqn attributeFqn,
                                                            Datatype<AV> datatype,
                                                            EvaluationContext evaluationContext,
                                                            Optional<EvaluationContext> optional) throws IndeterminateEvaluationException {
        if (gpsLocationType.equals(datatype)) {
            // Fetch location information from microservice API
            HttpResponse<String> response;

            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                throw new IndeterminateEvaluationException("Failed to fetch GPS information!",
                                                           XacmlStatusCode.MISSING_ATTRIBUTE.value());
            }

            JSONObject responseJson = new JSONObject(new String(response.body()));

            List<GeometryValue> lst = new LinkedList<>();
            Point point = new GeometryFactory()
                    .createPoint(new Coordinate(responseJson.getDouble("latitude"),
                                                responseJson.getDouble("longitude")));

            point.setSRID(4326);
            GeometryValue sample = new GeometryValue(point);
            lst.add(sample);

            AttributeBag<AV> bag = (AttributeBag<AV>) Bags.newAttributeBag(GeometryValue.DATATYPE, lst,
                                                                           AttributeSources.PDP);

            return bag;
        }

        throw new IndeterminateEvaluationException("Requested datatype (" + datatype + ") != " +
                                                          "provided by " + this + " (" +
                                                           datatype + ")",
                                                  XacmlStatusCode.MISSING_ATTRIBUTE.value());
    }

    @Override
    public void close() throws IOException {
        // used for i.e. closing connection to remote server or other socket
        // can be empty for now
    }

    private static AttributeDesignatorType createAttributeDesignator(AttributeFqn attributeFqn,
                                                                     Datatype<?> attributeType) {
        return new AttributeDesignatorType(attributeFqn.getCategory(), attributeFqn.getId(),
                                           attributeType.getId(),
                                           attributeFqn.getIssuer().orElse(null), false);
    }

    public static class DependencyAwareAttributeProviderFactory implements DependencyAwareFactory {
        private final String providerId;
        private final String serialPath;

        private DependencyAwareAttributeProviderFactory(final String providerId,
                                                        final String serialPath) {
            this.providerId = providerId;
            this.serialPath = serialPath;
        }

        @Override
        public Set<AttributeDesignatorType> getDependencies() {
            // We do not have any dependencies.
            return null;
        }

        @Override
        public CloseableNamedAttributeProvider getInstance(AttributeValueFactoryRegistry attributeValueFactoryRegistry,
                                                           NamedAttributeProvider attributeProvider) {
            {
                return new GPSLocationProvider(this.providerId, this.serialPath);
            }
        }
    }

    public static class Factory extends CloseableNamedAttributeProvider.FactoryBuilder<GPSLocationProviderDescriptor> {
        @Override
        public Class<GPSLocationProviderDescriptor> getJaxbClass() {
            return GPSLocationProviderDescriptor.class;
        }

        @Override
        public CloseableNamedAttributeProvider.DependencyAwareFactory getInstance(GPSLocationProviderDescriptor conf, EnvironmentProperties environmentProperties) throws IllegalArgumentException {
            return new DependencyAwareAttributeProviderFactory(conf.getId(), conf.getGpsURL());
        }
    }
}

