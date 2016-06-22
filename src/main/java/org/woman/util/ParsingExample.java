package org.woman.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.apache.tika.sax.xpath.Matcher;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.apache.tika.sax.xpath.XPathParser;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ParsingExample {

	public static void main(String[] args) throws Exception {
		example9();
	}

	//Working example
	public static void example9() throws Exception {
		    System.out.println("*** HTMLCLEANER ***");
			String input = Utils.getContentFromLink("http://www.global-womens-network.org/wiki/Rape_Crisis_Scotland,_Glasgow,_Scotland");
		    HtmlCleaner cleaner = new HtmlCleaner();
		    TagNode node = cleaner.clean(input);
		    System.out.println("H1: " + ((TagNode)(node.evaluateXPath("//table/tbody/tr[12]/td[2]/a[@class='external text']")[0])).getText());

	}
	public static void example8() throws Exception {
		URL oracle = new URL("http://www.global-womens-network.org/wiki/Rape_Crisis_Scotland,_Glasgow,_Scotland");
		URLConnection yc = oracle.openConnection();
		InputStream is = yc.getInputStream();
		is = oracle.openStream();
		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowWarnings(false);
		Document tidyDOM = tidy.parseDOM(is, null);
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xPath = xPathFactory.newXPath();
		String expression = "//table/tbody/tr[16]/td[2]/text()";
		XPathExpression xPathExpression = xPath.compile(expression);
		NodeList shows = (NodeList) xPathExpression.evaluate(tidyDOM, XPathConstants.NODESET);
		   
	}

	public static void example7() throws Exception {
		String input = Utils.getContentFromLink("http://www.global-womens-network.org/wiki/Rape_Crisis_Scotland,_Glasgow,_Scotland");

		TagNode tagNode = new HtmlCleaner().clean(input);
		Document doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);

		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList) xpath
				.evaluate(
						"/html[@class='client-js mac chrome chrome3 webkit webkit5']/body[@class='mediawiki ltr sitedir-ltr ns-0 ns-subject page-Rape_Crisis_Scotland_Glasgow_Scotland action-view skin-vector']/div[@id='content']/div[@id='bodyContent']/div[@class='mw-content-ltr']/table/tbody/tr[16]/td[2]/text()",
						doc, XPathConstants.NODESET);

		for (int i = 0; i < nodes.getLength(); i++) {
			System.out.println(nodes.item(i).getTextContent());
		}
	}

	public static void example6() throws Exception {

		String input = Utils.getContentFromLink("http://www.global-womens-network.org/wiki/Rape_Crisis_Scotland,_Glasgow,_Scotland");

		TagNode tagNode = new HtmlCleaner().clean(input);
		Document doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);

		XPath xpath = XPathFactory.newInstance().newXPath();
		String str = (String) xpath
				.evaluate(
						"/html[@class='client-js mac chrome chrome3 webkit webkit5']/body[@class='mediawiki ltr sitedir-ltr ns-0 ns-subject page-Rape_Crisis_Scotland_Glasgow_Scotland action-view skin-vector']/div[@id='content']/div[@id='bodyContent']/div[@class='mw-content-ltr']/table/tbody/tr[16]/td[2]",
						doc, XPathConstants.STRING);
		System.out.println("str : " + str);
	}

	public static void example5() throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();

		URL url = new URL("http://www.global-womens-network.org/wiki/Rape_Crisis_Scotland,_Glasgow,_Scotland");
		InputStream input = url.openStream();

		Document doc = builder.parse(input);

		List<String> list = new ArrayList<String>();
		try {
			XPathFactory xpathFactory = XPathFactory.newInstance();

			// Create XPath object
			XPath xpath = xpathFactory.newXPath();
			XPathExpression expr = xpath.compile("//table/tbody/tr[8]/td[1]/text()");
			// .compile("/html[@class='client-js mac chrome chrome3 webkit webkit5']/body[@class='mediawiki ltr sitedir-ltr ns-0 ns-subject page-Rape_Crisis_Scotland_Glasgow_Scotland action-view skin-vector']/div[@id='content']/div[@id='bodyContent']/div[@class='mw-content-ltr']/table/tbody/tr[11]/td[2]/a[@class='external text']/text()");
			NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++)
				list.add(nodes.item(i).getNodeValue());
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}

	public static void example4() throws IOException, SAXException, TikaException {

		URL url = new URL("http://www.global-womens-network.org/wiki/Rape_Crisis_Scotland,_Glasgow,_Scotland");
		InputStream input = url.openStream();

		XPathParser parser = new XPathParser("xhtml", "http://www.w3.org/1999/xhtml");
		// Matcher matcher = parser.parse("/xhtml:html/xhtml:body/descendant:node()");
		// Matcher matcher = parser.parse("/html/body//h1");
		// Matcher matcher =
		// parser.parse("/html[@class='client-js mac chrome chrome3 webkit webkit5']/body[@class='mediawiki ltr sitedir-ltr ns-0 ns-subject page-Rape_Crisis_Scotland_Glasgow_Scotland action-view skin-vector']/div[@id='content']/div[@id='bodyContent']/div[@class='mw-content-ltr']/table/tbody/tr[11]/td[2]/a[@class='external text']");
		Matcher matcher = parser
				.parse("/html[@class='client-js mac chrome chrome3 webkit webkit5']/body[@class='mediawiki ltr sitedir-ltr ns-0 ns-subject page-Rape_Crisis_Scotland_Glasgow_Scotland action-view skin-vector']/div[@id='content']/div[@id='bodyContent']/div[@class='mw-content-ltr']/table/tbody/tr[11]/td[2]/a[@class='external text']/text()");

		// matcher = parser.parse("/table/tbody/tr[16]/td[2]::node()");

		ContentHandler textHandler = new MatchingContentHandler(new BodyContentHandler(), matcher);
		Metadata metadata = new Metadata();
		ParseContext context = new ParseContext();
		new HtmlParser().parse(input, textHandler, metadata, context);
		System.out.println("content: " + textHandler.toString());

	}

	public static void example3() throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, SAXException,
			TikaException {

	}

	public static void example1() throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, SAXException,
			TikaException {
		URL url = new URL("http://www.global-womens-network.org/wiki/Rape_Crisis_Scotland,_Glasgow,_Scotland");
		InputStream input = url.openStream();
		String mimeType = new Tika().detect(input);
		Metadata metadata = new Metadata();
		metadata.set(Metadata.CONTENT_TYPE, mimeType);
		DOMResult result = new DOMResult();
		TransformerHandler transformerHandler = ((SAXTransformerFactory) SAXTransformerFactory.newInstance()).newTransformerHandler();
		transformerHandler.setResult(result);
		new HtmlParser().parse(input, transformerHandler, metadata, new ParseContext());

		System.out.println(result.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(1).getNodeName());

	}

	public static void example2() throws IOException, SAXException, TikaException {
		URL url = new URL("http://www.global-womens-network.org/wiki/Rape_Crisis_Scotland,_Glasgow,_Scotland");
		InputStream input = url.openStream();
		LinkContentHandler linkHandler = new LinkContentHandler();
		ContentHandler textHandler = new BodyContentHandler();
		ToHTMLContentHandler toHTMLHandler = new ToHTMLContentHandler();
		TeeContentHandler teeHandler = new TeeContentHandler(linkHandler, textHandler, toHTMLHandler);
		Metadata metadata = new Metadata();
		ParseContext parseContext = new ParseContext();
		HtmlParser parser = new HtmlParser();
		parser.parse(input, teeHandler, metadata, parseContext);
		System.out.println("title:\n" + metadata.get("title"));
		System.out.println("links:\n" + linkHandler.getLinks());
		System.out.println("text:\n" + textHandler.toString());
		System.out.println("html:\n" + toHTMLHandler.toString());

	}

}
