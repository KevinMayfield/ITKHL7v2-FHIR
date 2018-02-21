package com.openMap1.mapper.FHIRTransforms;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility methods called by generated Java transforms
 */

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;


public class BaseTransformer {
	
	/**
	 * push a new element onto the stack of source nodes
	 * @param el
	 * @param stack
	 * @return
	 */
	public static List<Element> push(Element el, List<Element> stack)
	{
		Vector<Element> newStack  = new Vector<Element>();		
		for (Element e: stack) newStack.add(e);
		newStack.add(el);			
		return newStack;
	}
	
	
    /**
     * return the Vector of child Elements with given local name
     * @param el
     * @param name
     * @return
     */
    public static Vector<Element> namedChildElements(Element el, String lName)
    {
    	Vector<Element> nc = new Vector<Element>();
    	for (Iterator<Element> it = childElements(el).iterator(); it.hasNext();)
    	{
    		Element en = it.next();
    		if (getName(en).equals(lName)) nc.addElement(en);
    	}
    	return nc;
    }
    
    /**
     * 
     * @param el
     * @param lName
     * @return
     */
    public static Node namedChildNode(Element el, String lName)
    {
    	if (namedChildElements(el,lName).size() > 0) return namedChildElements(el,lName).get(0);
    	else return el.getAttributeNode(lName);
    }


    /**
     * Vector of child elements of an element
     */
    public static Vector<Element> childElements(Element el) {
        Vector<Element> res = new Vector<Element>();
        NodeList nodes = el.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node nd = nodes.item(i);
            if (nd instanceof Element) {
                Element eg = (Element) nd;
                res.add(eg);
            }
        }
        return res;
    }

    /**
	 * get the name of an XML element, with the namespace prefix stripped off
	 * @param el
	 * @return
	 */
	public static String getName(Node el)
	{
		String locName = "";
		StringTokenizer st = new StringTokenizer(el.getNodeName(),":");
		while (st.hasMoreTokens()) locName = st.nextToken();
		return locName;
	}
	
    /**
     * get the  text string in an element (eg interspersed between child elements), 
     * or "" if there is none or if the Element is null.
     * Tries to ignore white space text; but does not succeed.
     */
    public static String getText(Node nd) {
        String res = "";
        if ((nd != null) && (nd  instanceof Element)) try 
        {
        	Element el = (Element)nd;
        	el.normalize(); // does not help recognise white space
        	NodeList nodes = el.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) 
            	if (nodes.item(i) instanceof Text)
            	{
            		Text text = (Text)nodes.item(i);
            		// this filter seems to make no difference
            		if (!text.isElementContentWhitespace())  
            		{
            			String tData = text.getData();
            			// this seems to be an effective way to catch pure white space
            			StringTokenizer nonWhiteSpace = new StringTokenizer(tData,"\n \t");
            			if (nonWhiteSpace.countTokens() > 0 )res = res + tData;
            		}
            	}
        }
        catch (Exception e) {System.out.println("Text failure: " + e.getMessage());}
        if ((nd != null) && (nd  instanceof Attr)) 
        {
        	Attr att = (Attr)nd;
        	res = att.getTextContent();
        }
        return res;
    }

	
	
	/**
	 * 
	 * @param el an Element
	 * @param path an XPath from the element (should  be unique)
	 * @param value the value at the end of the path that results in 'true'
	 * @return
	 */
    public boolean valueTest(Element el, String path, String value, String test)
	{
    	boolean res = false;
		Element current = el;
		String testValue = null;
		
		// set testValue non-null only if you can follow the path uniquely
		StringTokenizer steps = new StringTokenizer(path,"/");
		while (steps.hasMoreTokens())
		{
			String step = steps.nextToken();
			if (current != null)
			{
				if (step.startsWith("@")) testValue = current.getAttribute(step.substring(1)); // may be "" if there is no attribute
				else 
				{
					Vector<Element> children = namedChildElements(current,step);
					current = null;
					// terminate & return false if any step delivers more than one element, or no elements
					if (children.size() == 1) current = children.get(0);
					if ((!steps.hasMoreTokens()) && (current != null))  testValue = getText(current);
				}
			}
		}
		
		if (testValue != null) 
		{
			if ("=".equals(test)) res = (value.equals(testValue));
			// the test type 'contains' really means 'exists', but there is no 'exists ' test in the model for value conditon tests
			if ("contains".equals(test)) res = true; // thenode esitst; it does not matter what its text content is, e.g ""
		}
		
		return res;
	}
	
    /**
     * 
     * @param el
     * @param path
     * @param el2
     * @param path2
     * @return true if following path from el gives the same unique value as following path2 from el2
     */
    public boolean crossTest(Element el, String path, Element el2, String path2)
	{
    	boolean res = false;
		Element current = el;
		String testValue = null;
		String testValue2 = null;
		
		// set testValue non-null only if you can follow the path uniquely
		StringTokenizer steps = new StringTokenizer(path,"/");
		while (steps.hasMoreTokens())
		{
			String step = steps.nextToken();
			if (current != null)
			{
				if (step.startsWith("@")) testValue = current.getAttribute(step.substring(1)); // may be "" if there is no attribute
				else 
				{
					Vector<Element> children = namedChildElements(current,step);
					current = null;
					// terminate & return false if any step delivers more than one element, or no elements
					if (children.size() == 1) current = children.get(0);
					if ((!steps.hasMoreTokens()) && (current != null))  testValue = getText(current);
				}
			}
		}
		
		// set testValue2 non-null only if you can follow the path path2 uniquely
		steps = new StringTokenizer(path2,"/");
		current = el2;
		while (steps.hasMoreTokens())
		{
			String step = steps.nextToken();
			if (current != null)
			{
				if (step.startsWith("@")) testValue2 = current.getAttribute(step.substring(1)); // may be "" if there is no attribute
				else 
				{
					Vector<Element> children = namedChildElements(current,step);
					current = null;
					// terminate & return false if any step delivers more than one element, or no elements
					if (children.size() == 1) current = children.get(0);
					if ((!steps.hasMoreTokens()) && (current != null))  testValue2 = getText(current);
				}
			}
		}

		
		if ((testValue != null) && (testValue2 != null)) res = (testValue2.equals(testValue));
		
		return res;
	}


    public static Document makeOutDoc() throws Exception
    {
    	Document outDoc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            outDoc = db.newDocument();
        }
        catch (ParserConfigurationException pce) {
            throw new Exception("Parser Config exception: " + pce.getMessage());
        }
        return outDoc;
    }


	
    /**
     * create a new element in no namespace with no text content.
     * write an error message and return null if any exception
     *
     * @param name String: name of the element
     * @return Element: the element
     */
    public static Element newElement(Document  outDoc, String name) 
    throws Exception {
        Element ei = null;
        try {
            ei = (Element) outDoc.createElement(name);
        }
        catch (Exception e) {
            throw new Exception ("Exception creating element '" + name
                    + "'. " + e.getMessage());
        }
        return ei;
    }

    /**
     * create a new element in no namespace with no text content.
     * write an error message and return null if any exception
     *
     * @param name String: name of the element
     * @param text String: the text content
     * @return Element: the element
     */
    public static Element textElement(Document outDoc, String name, String text) 
    throws Exception {
        Text t;
        Element en = null;
        try {
            en = newElement(outDoc,name);
            t = outDoc.createTextNode(text);
            en.appendChild(t);
        }
        catch (Exception e) {
      	  throw new Exception ("Exception creating text-filled element '" + name
                    + "'. " + e.getMessage());
        }
        return en;
    }
    
    /**
     * add nodes on a path below an Element leading to a node (Element or Attribute) containing a fixed value
     * @param el
     * @param path
     * @param value
     */
    public void addFixedValue(Element el, String path, String value) throws Exception
    {
    	Document doc = el.getOwnerDocument();
    	Element current = el;
    	StringTokenizer steps = new StringTokenizer(path, "/");
    	while (steps.hasMoreTokens())
    	{
    		String step = steps.nextToken();
    		boolean last = (!steps.hasMoreTokens());
    		
    		if (last)
    		{
    			if (step.startsWith("@")) current.setAttribute(step.substring(1), value);
    			else current.appendChild(textElement(doc, step,value));
    		}
    		
    		else if (!last)
    		{
    			// find or make a child element with the right name
    			Element nextEl = null;
    			Node next = namedChildNode(current,step);
    			if (next == null) nextEl = newElement(doc,step);
    			else nextEl = (Element)next;
    			
    			current.appendChild(nextEl);
    			current = nextEl;
    		}
    	}
    }
	
	/**
	 * return the Document of an XML file in a given inputStream
	 * @param location
	 * @return Element the root element, or null if there is any problem
	 */
	public static Document getDocument(InputStream fi) throws Exception
	{
		Document doc = null;
        try {
            DocumentBuilderFactory builderFac = DocumentBuilderFactory.newInstance();
            builderFac.setNamespaceAware(true);
            doc = builderFac.newDocumentBuilder().parse(fi);
        }
        catch (SAXException ex) {notify("Input Stream",ex);}
        catch (FileNotFoundException ex) {notify("Input Stream",ex);}
        catch (IOException ex) {notify("Input Stream",ex);}
        catch (ParserConfigurationException ex) {notify("Input Stream",ex);}
		return doc;
	}
	

	private static void notify(String location, Exception ex) throws Exception
	{
		throw new Exception ("Exception getting XML root element from "
				+ location + "; " + ex.getMessage());
	}
	
}
