package mayfieldis.fhir.hl7v2.Processor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.util.Terser;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class HL7v2A05toFHIRBundle implements Processor {

    private static final Logger log = LoggerFactory.getLogger(HL7v2A05toFHIRBundle.class);

    Terser terser = null;


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

        FhirContext ctx = FhirContext.forDstu3();

        Bundle bundle = new Bundle();
        String hl7v2Message = (String) exchange.getIn().getBody();

        // TODO Insert conversion of Hl7v2 to Bundle here
        System.out.println(hl7v2Message);

        String Response = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);

        exchange.getIn().setHeader(Exchange.HTTP_PATH,"/Bundle");
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
        exchange.getIn().setHeader("Content-Type", "application/fhir+json");
        exchange.getIn().setBody(Response);
    }

}
