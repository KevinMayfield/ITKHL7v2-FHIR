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


import org.apache.camel.CamelContext;
import org.apache.camel.component.hl7.HL7MLLPCodec;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.nio.file.*;

//CHECKSTYLE:OFF

@SpringBootApplication
public class CamelApplication {

    /**
     * A main method to start this application.
     */
    public static void main(String[] args) {
        System.getProperties().put( "server.port", 8083 );
        SpringApplication.run(CamelApplication.class, args);
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {


        ClassLoader classLoader = getContextClassLoader();



        try {
            File file = new File(classLoader.getResource("HL7v2").getFile());

            for (File fileD : file.listFiles()) {
                System.out.println(fileD.getName());

                Path FROM = Paths.get(fileD.getAbsolutePath());

                Path TO = Paths.get("/HL7v2/In/" + fileD.getName());
                //overwrite existing file, if exists
                CopyOption[] options = new CopyOption[]{
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.COPY_ATTRIBUTES
                };
                Files.copy(FROM, TO, options);
                System.out.println("Copied -" + fileD.getAbsolutePath()+ " to "+TO.getFileName());

            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }


    @Bean
    public CamelContext context() {
        CamelContext camelContext;
        SimpleRegistry reg = new SimpleRegistry();
        reg.put("hl7codec", new HL7MLLPCodec());
        camelContext = new DefaultCamelContext(reg);
        return camelContext;

    }

}
//CHECKSTYLE:ON
