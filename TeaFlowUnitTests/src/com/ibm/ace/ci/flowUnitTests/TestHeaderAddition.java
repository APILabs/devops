package com.ibm.ace.ci.flowUnitTests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.xmlunit.builder.Input;
import org.xmlunit.matchers.*;
import org.xmlunit.xpath.JAXPXPathEngine;

import static org.xmlunit.matchers.HasXPathMatcher.hasXPath;

import com.ibm.broker.testsupport.MbTestHelper; // deployFile and deleteAll


public class TestHeaderAddition {
	@Test
	public void test() {
		String barDirectoryEnvVar = System.getenv("TEST_BAR_DIR");
		if ( barDirectoryEnvVar == null ) barDirectoryEnvVar = "C:\\Users\\TREVORDolby\\IBM\\ACET11\\workspace\\BARfiles\\";
   	    MbTestHelper.getInstance().deployFile(barDirectoryEnvVar+"Tea.bar");
   	    // From App Connect Callable Flow mocha test
   	    String cfInvokeString = "<message xmlns:iib='http://com.ibm.iib/lt/1.0' iib:parser='GENERICROOT'></message>";
	    String responseBody = MbTestHelper.getInstance().invokeNode("Tea", "TestScaffoldFlow", "AddHeader", cfInvokeString);
	    System.out.println("Return data |"+responseBody+"|");
	    //assertNotEquals(-1, responseBody.indexOf("TDDHeader"));
	    assertThat(responseBody, hasXPath("//TDD_Header"));
	    assertEquals("testvalue",
	             new JAXPXPathEngine().evaluate("//TDD_Header", Input.from(responseBody).build()));
	    //MbTestHelper.getInstance().deleteAll();
	}

}
