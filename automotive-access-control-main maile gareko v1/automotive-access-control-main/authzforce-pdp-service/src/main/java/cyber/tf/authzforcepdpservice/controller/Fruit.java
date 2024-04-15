package cyber.tf.authzforcepdpservice.controller;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Request;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.*;
import java.io.StringReader;


@XmlRootElement
// order of the fields in XML
// @XmlType(propOrder = {"price", "name"})
    @XmlAccessorType(XmlAccessType.FIELD)
    public class Fruit {

        @XmlAttribute
        int id;

        @XmlElement(name = "n")
        String name;

        String price;

        // getter, setter and toString...
    }


    class ptk{
        public static void main(String[] args) throws JAXBException {
            String a =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<fruit id=\"1\">\n" +
                    "   <n>Banana</n>\n" +
                    "   <price>9.99</price>\n" +
                    "</fruit>";
            JAXBContext context = JAXBContext.newInstance(Fruit.class);
            Fruit request = (Fruit) context.createUnmarshaller().unmarshal(new StringReader(a));
            System.out.println(request.name);

        }
    }

