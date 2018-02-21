package com.openMap1.mapper.FHIRTransforms;

import java.util.List;
import java.util.Vector;
import java.util.Date;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.hl7.fhir.dstu3.model.BaseResource;

import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import com.openMap1.mapper.FHIRConversions.FHIRConverters;
import org.hl7.fhir.dstu3.model.Bundle;

public class V2Transform extends BaseTransformer {

// to check that the compiled class is the correct version
public String getGeneratedDateTime() {return("Thu Feb 15 16:43:27 GMT 2018");}

// to check that the compiled class is the correct version; change version when making hand edits
public String getVersion() {return("1");}


/**
* @param  sDoc the input document
* @ return the result of the transform
*/
public BaseResource transform(Document sDoc) throws Exception
{
   Element root = sDoc.getDocumentElement();
   return topRule(root);
}

//--------------------------------------------------------------------------------------
//                                  Bundle                                  
//--------------------------------------------------------------------------------------

/**
* @param sourceTop
*/
protected BaseResource topRule(Element sourceTop) throws Exception
{
    if (!("ADT_A05".equals(getName(sourceTop))))
        throw new Exception("Root element is not named 'ADT_A05'");
    Bundle target = new Bundle();

    List<Element> stack1 = push(sourceTop, new Vector<Element>());
    rBundle_Entry_Patien(stack1, target);
    return target;
}

//--------------------------------------------------------------------------------------
//                                  Patient                                  
//--------------------------------------------------------------------------------------

/**
* @param stack - source elements (0)ADT_A05; 
* @param target - reached by target path: Bundle
*/
protected void rBundle_Entry_Patien(List<Element> stack, Bundle target) throws Exception
{
    Element sourceTop = stack.get(0);
    for(Element sPID : namedChildElements(sourceTop,"PID"))
    {
        Bundle.BundleEntryComponent t_entry_Patient_Bund = new Bundle.BundleEntryComponent(); 
        target.addEntry(t_entry_Patient_Bund);
        Patient t_resource_Patient = new Patient();
        t_entry_Patient_Bund.setResource(t_resource_Patient);
        List<Element> stack1 = push(sPID,stack);
        pBirthDate(stack1, t_resource_Patient);
        pGender(stack1, t_resource_Patient);
        rName_HumanName(stack1, t_resource_Patient);
        rIdentifier_Identifi(stack1, t_resource_Patient);
    }
}

/**
* @param stack - source elements (0)ADT_A05; (1)PID; 
* @param t_resource_Patient - reached by target path: Bundle.entry.resource
*/
protected void pBirthDate(List<Element> stack, Patient t_resource_Patient) throws Exception
{
    Element sPID = stack.get(1);
    for(Element sPID_1 : namedChildElements(sPID,"PID.7"))
    {
        List<Element> stack1 = push(sPID_1,stack);

        Node sBirthDate = namedChildNode(sPID_1,"TS.1");
        //if (sBirthDate != null) t_resource_Patient.setBirthDate(new Date(FHIRConverters.date_V2_to_FHIR(null,getText(sBirthDate))));
    }
}

/**
* @param stack - source elements (0)ADT_A05; (1)PID; 
* @param t_resource_Patient - reached by target path: Bundle.entry.resource
*/
protected void pGender(List<Element> stack, Patient t_resource_Patient) throws Exception
{
    Element sPID = stack.get(1);
    for(Element sGender : namedChildElements(sPID,"PID.8"))
    {
        t_resource_Patient.setGender(AdministrativeGender.fromCode(patient_gender_conversion(getText(sGender))));
    }
}

protected String patient_gender_conversion(String val)
{
     if("M".equals(val)) return "male";
     if("F".equals(val)) return "female";
     return"";
}

/**
* @param stack - source elements (0)ADT_A05; (1)PID; 
* @param t_resource_Patient - reached by target path: Bundle.entry.resource
*/
protected void rName_HumanName(List<Element> stack, Patient t_resource_Patient) throws Exception
{
    Element sPID = stack.get(1);
    for(Element sPID_2 : namedChildElements(sPID,"PID.5"))
    {
        HumanName t_name_HumanName = new HumanName(); 
        t_resource_Patient.addName(t_name_HumanName);
        List<Element> stack1 = push(sPID_2,stack);

        Node sXPN = namedChildNode(sPID_2,"XPN.2");
        if (sXPN != null) t_name_HumanName.addGiven(getText(sXPN));

        Node sXPN_1 = namedChildNode(sPID_2,"XPN.1");
        if (sXPN_1 != null) t_name_HumanName.setFamily(getText(sXPN_1));
    }
}

/**
* @param stack - source elements (0)ADT_A05; (1)PID; 
* @param t_resource_Patient - reached by target path: Bundle.entry.resource
*/
protected void rIdentifier_Identifi(List<Element> stack, Patient t_resource_Patient) throws Exception
{
    Element sPID = stack.get(1);
    for(Element sPID_3 : namedChildElements(sPID,"PID.3"))
    {
        Identifier t_Identifier = new Identifier(); 
        t_resource_Patient.addIdentifier(t_Identifier);
        List<Element> stack1 = push(sPID_3,stack);

        Node sValue = namedChildNode(sPID_3,"CX.1");
        if (sValue != null) t_Identifier.setValue(getText(sValue));
        pSystem(stack1, t_Identifier);
    }
}

/**
* @param stack - source elements (0)ADT_A05; (1)PID; (2)PID_3; 
* @param t_Identifier - reached by target path: Bundle.entry.resource.identifier
*/
protected void pSystem(List<Element> stack, Identifier t_Identifier) throws Exception
{
    Element sPID_3 = stack.get(2);
    for(Element sCX : namedChildElements(sPID_3,"CX.4"))
    {
        List<Element> stack1 = push(sCX,stack);

        Node sSystem = namedChildNode(sCX,"HD.2");
        if (sSystem != null) t_Identifier.setSystem(getText(sSystem));
    }
}

}
