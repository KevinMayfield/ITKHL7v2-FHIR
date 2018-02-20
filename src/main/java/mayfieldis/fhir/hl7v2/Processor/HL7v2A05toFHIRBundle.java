package mayfieldis.fhir.hl7v2.Processor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.v24.message.ADT_A05;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.XMLParser;
import ca.uhn.hl7v2.util.Terser;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFile;
import org.apache.commons.io.IOUtils;
import org.hl7.fhir.dstu3.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;



public class HL7v2A05toFHIRBundle implements Processor {

    private static final Logger log = LoggerFactory.getLogger(HL7v2A05toFHIRBundle.class);

    Terser terser = null;
    private HapiContext hapiContext;

    public HL7v2A05toFHIRBundle(HapiContext hapiContext) {
        this.hapiContext = hapiContext;
    }

    private String terserGet(String query)
    {
        String result = "";
        try {
            result = terser.get(query);

        } catch(HL7Exception hl7ex) {
            System.out.println("hl7v2 Exception"+ hl7ex.getMessage());
        } catch(Exception ex) {
            System.out.println("Exception"+ ex.getMessage());
        }

        return result;
    }


    @Override
    public void process(Exchange exchange) throws Exception {


        System.out.println(exchange.getIn().getBody().getClass().toString());

        Object body = exchange.getIn().getBody();


        String hl7v2Message = null;

        /*
        REMOVE this next section
         */
        if (body instanceof GenericFile) {
            GenericFile<File> file = (GenericFile<File>) body;
            InputStream inputStream = new FileInputStream(file.getFile());
            hl7v2Message = IOUtils.toString(inputStream, "UTF-8");
        }

        if (body instanceof ADT_A05) {
            ADT_A05 v24message = (ADT_A05) body;

            XMLParser xmlParser = new DefaultXMLParser();
            //encode message in XML
            String ackMessageInXML = xmlParser.encode(v24message);

            //print XML-encoded message to standard out
            System.out.println(ackMessageInXML);
        }


        // TODO Insert conversion of Hl7v2 to Bundle here
        System.out.println(hl7v2Message);
        FhirContext ctx = FhirContext.forDstu3();
        Bundle bundle = new Bundle();

        String Response = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);


        exchange.getIn().setHeader(Exchange.HTTP_PATH,"/Bundle");
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
        exchange.getIn().setHeader("Content-Type", "application/fhir+json");
        exchange.getIn().setBody(Response);
    }

}
