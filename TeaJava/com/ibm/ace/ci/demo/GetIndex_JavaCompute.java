package com.ibm.ace.ci.demo;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbNode.JDBC_TransactionType;

public class GetIndex_JavaCompute extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
	        Connection conn = getJDBCType4Connection("DB2JDBC", JDBC_TransactionType.MB_TRANSACTION_AUTO);

	        // Example of using the Connection to create a java.sql.Statement  
	        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
	        									  ResultSet.CONCUR_READ_ONLY);
	        // This would normally be done externally, but we do it here for convenience
	        try {
	        	stmt.executeUpdate("CREATE TABLE Tea(id INTEGER, name VARCHAR(128))");
	        } catch ( java.lang.Throwable jlt ) {
	        	//jlt.printStackTrace();
	        }
	        
	        String teaName = null;
	        MbElement inputLE = outAssembly.getLocalEnvironment().getRootElement();
	        String teaIndex = (String)(inputLE.getFirstElementByPath("HTTP/Input/Path").getLastChild().getValue());
	        
	        stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
	        							ResultSet.CONCUR_READ_ONLY);
	        ResultSet rs = stmt.executeQuery("SELECT name from Tea where id='"+teaIndex+"'");
	        if ( rs.first() )
	        {
	        	teaName = rs.getString(1);
	        }
	        MbElement rootElem = outAssembly.getMessage().getRootElement();


	        MbElement httpHeader = rootElem.createElementAsLastChild("HTTPReplyHeader").
	        		createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "Server_Hostname", 
	        					InetAddress.getLocalHost().getHostName());
	        
	        MbElement jsonData = rootElem.createElementAsLastChild("JSON").
	        			 		 createElementAsFirstChild(MbElement.TYPE_NAME);
	        jsonData.setName("Data");
	        jsonData.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "id", teaIndex);
	        jsonData.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "name", teaName);
	        

			// End of user code
			// ----------------------------------------------------------
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}

}
