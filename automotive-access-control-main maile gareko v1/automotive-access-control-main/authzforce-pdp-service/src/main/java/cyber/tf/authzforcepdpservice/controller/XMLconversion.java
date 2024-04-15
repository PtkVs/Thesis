package cyber.tf.authzforcepdpservice.controller;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.Request;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Response;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import java.io.File;

public class XMLconversion {







        public static void main(String[] args) throws JAXBException {

            JAXBContext context = JAXBContext.newInstance(Fruit.class);
           String  requestString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                   "<fruit id=\"1\">\n" +
                   "   <n>Banana</n>\n" +
                   "   <price>9.99</price>\n" +
                   "</fruit>";
            Fruit request = (Fruit) context.createUnmarshaller().unmarshal(new StringReader(requestString));
            System.out.println(request.id);

    }
}
