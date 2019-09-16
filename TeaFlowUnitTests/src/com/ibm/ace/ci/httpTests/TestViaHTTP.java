package com.ibm.ace.ci.httpTests;


import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.xmlunit.builder.Input;
import org.xmlunit.matchers.*;
import org.xmlunit.xpath.JAXPXPathEngine;

import static org.xmlunit.matchers.HasXPathMatcher.hasXPath;

import com.ibm.broker.testsupport.MbTestHelper; // deployFile and deleteAll

public class TestViaHTTP {

	@Test
	public void test() throws IOException {
		String barDirectoryEnvVar = System.getenv("TEST_BAR_DIR");
		if ( barDirectoryEnvVar == null ) barDirectoryEnvVar = "C:\\Users\\TREVORDolby\\IBM\\ACET11\\workspace\\BARfiles\\";
		
		// For v10, this line would be replaced with either a pre-deployed BAR file (from a script 
		// outside JUnit), or else would need to make CMP calls to a previously set up broker.
   	    MbTestHelper.getInstance().deployFile(barDirectoryEnvVar+"TestScaffoldApplication.bar");
   	    
   	    try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   	    
   	    URL localHTTPURL = new URL("http://localhost:7800/test/scaffold/esql");
   	    HttpURLConnection localHTTPConnection = (HttpURLConnection)localHTTPURL.openConnection();
        int httpStatus = localHTTPConnection.getResponseCode();
        assertEquals(200, httpStatus);
        BufferedReader br = new BufferedReader(new InputStreamReader(localHTTPConnection.getInputStream()));
        StringBuffer responseBodyBuffer = new StringBuffer();
        String currentLine;
        while ((currentLine = br.readLine()) != null) 
        {
        	responseBodyBuffer.append(currentLine);
        } 
        br.close();
        String responseBody = responseBodyBuffer.toString();
	    System.out.println("Return data |"+responseBody+"|");
	    
	    // Note that we can't directly check the header by using XPath, as we're not looking at a 
	    // serialised tree of any kind. Check the actual HTTP heaer instead.
	    assertEquals("test_value", localHTTPConnection.getHeaderField("TDDHeader"));

	    //MbTestHelper.getInstance().deleteAll();
	}

}
