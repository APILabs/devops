package com.ibm.ace.ci.flowComponentTests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ibm.broker.testsupport.MbTestHelper; // deployFile and deleteAll

public class TestJDBCConnection {

	@Test
	public void test() {
          String barDirectoryEnvVar = System.getenv("TEST_BAR_DIR");
          if ( barDirectoryEnvVar == null ) barDirectoryEnvVar = "C:\\Users\\TREVORDolby\\IBM\\ACET11\\workspace\\BARfiles\\";
          MbTestHelper.getInstance().deployFile(barDirectoryEnvVar+"Tea.bar");
          // From App Connect Callable Flow mocha test
          String cfLEString = "<message xmlns:iib='http://com.ibm.iib/lt/1.0' iib:parser='GENERICROOT'><HTTP><Input><Path><Segment iib:valueType='CHARACTER'>1</Segment></Path></Input></HTTP></message>";
          String cfInvokeString = "{\"test\": \"value\"}";
          String responseBody = MbTestHelper.getInstance().invokeFlow("Tea", "CallGetIndexJCN", cfInvokeString, cfLEString);
          System.out.println("Return data |"+responseBody+"|");
          assertNotNull(responseBody);
          //MbTestHelper.getInstance().deleteAll();
	}

}
