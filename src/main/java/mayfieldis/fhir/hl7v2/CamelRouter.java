/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mayfieldis.fhir.hl7v2;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import mayfieldis.fhir.hl7v2.Processor.HL7v2A05toFHIRBundle;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hl7.HL7DataFormat;

import org.apache.camel.component.hl7.HL7MLLPCodec;
import org.springframework.stereotype.Component;
import static org.apache.camel.component.hl7.HL7.ack;


/**
 * A simple Camel route that triggers from a timer and calls a bean and prints to system out.
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
@Component
public class CamelRouter extends RouteBuilder {




    @Override
    public void configure() throws Exception {

        CamelContext camelContext = getContext();
        final org.apache.camel.impl.SimpleRegistry registry = new org.apache.camel.impl.SimpleRegistry();
        final org.apache.camel.impl.CompositeRegistry compositeRegistry = new org.apache.camel.impl.CompositeRegistry();
        compositeRegistry.addRegistry(camelContext.getRegistry());
        compositeRegistry.addRegistry(registry);
        ((org.apache.camel.impl.DefaultCamelContext) camelContext).setRegistry(compositeRegistry);
        registry.put("hl7codec", new HL7MLLPCodec());

        HapiContext hapiContext = new DefaultHapiContext();

        hapiContext.getParserConfiguration().setValidating(false);
        HL7DataFormat hl7 = new HL7DataFormat();
        HL7v2A05toFHIRBundle hl7v2A05toFHIRBundle = new HL7v2A05toFHIRBundle();

        hl7.setHapiContext(hapiContext);

        from("file:///HL7v2/In")
                .routeId("file")
                .to("direct:Output");


        from("mina2:tcp://0.0.0.0:8888?sync=true&disconnectOnNoReply=false&codec=#hl7codec")
                .routeId("HL7v2 TCP Feed")
                .unmarshal(hl7)
                .choice()
                    .when(header("CamelHL7MessageType").isEqualTo("ADT"))
                        .wireTap("direct:ADT")
                    .end()
                .end()
                .transform(ack());

        from("direct:ADT")
                .routeId("adtRoute")
                .marshal(hl7)
                .to("direct:Output");


        from("direct:Output")
                .routeId("output")
                .process(hl7v2A05toFHIRBundle)
                .to("file:///HL7v2/Out");

    }


}
