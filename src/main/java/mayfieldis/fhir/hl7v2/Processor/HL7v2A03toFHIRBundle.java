package mayfieldis.fhir.hl7v2.Processor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.v24.message.ADT_A03;
import ca.uhn.hl7v2.model.v24.message.ADT_A05;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.XMLParser;
import ca.uhn.hl7v2.util.Terser;
import com.openMap1.mapper.FHIRTransforms.V2Transform;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.dstu3.model.BaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;


public class HL7v2A03toFHIRBundle implements Processor {

    private static final Logger log = LoggerFactory.getLogger(HL7v2A03toFHIRBundle.class);

    private HapiContext hapiContext;

    public HL7v2A03toFHIRBundle(HapiContext hapiContext) {
        this.hapiContext = hapiContext;
    }


    @Override
    public void process(Exchange exchange) throws Exception {

        Object body = exchange.getIn().getBody();

        log.debug("In A03 "+exchange.getIn().getHeader("CamelHL7TriggerEvent"));


        String response=  null;
        String hl7v2XMLMessage = null;


        if (body instanceof ADT_A03) {
            ADT_A03 v24message = (ADT_A03) body;

            XMLParser xmlParser = new DefaultXMLParser();
            //encode message in XML
            hl7v2XMLMessage = xmlParser.encode(v24message);

            //print XML-encoded message to standard out
            System.out.println(hl7v2XMLMessage);
        }



        if (hl7v2XMLMessage !=null) {
            Document doc = convertStringToDocument(hl7v2XMLMessage);

            // make an instance to run the transform
            V2Transform transformer = new V2Transform();

            // run the transform, and trace the HAPI class of the result
            BaseResource result = transformer.transform(doc);
            System.out.println("Type of result: " + result.getClass().getName());

            FhirContext ctx = FhirContext.forDstu3();

            response = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(result);

        }

        exchange.getIn().setHeader(Exchange.HTTP_PATH,"/Bundle");
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
        exchange.getIn().setHeader("Content-Type", "application/fhir+json");
        exchange.getIn().setBody(response);
    }

    private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
