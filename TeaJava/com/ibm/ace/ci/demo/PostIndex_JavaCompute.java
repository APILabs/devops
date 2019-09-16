package com.ibm.ace.ci.demo;

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

public class PostIndex_JavaCompute extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		//MbOutputTerminal alt = getOutputTerminal("alternate");

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
	        //MbElement inputLE = outAssembly.getMessage().getRootElement();
	        //teaName = (String)(inputLE.getFirstElementByPath("HTTP.Input.Path").getLastChild().getValue());
	        MbElement inputRoot = outAssembly.getMessage().getRootElement();
	        teaName = (String)(inputRoot.getFirstElementByPath("JSON/Data/name").getValue());
	        // This is an example only, and is oversimplified to minimise database
	        // setup; most real database solutions would have tables pre-configured 
	        // with a unique index on name, and possibly an auto-incrementing id column.
	        stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
	        							ResultSet.CONCUR_READ_ONLY);
	        ResultSet rs = stmt.executeQuery("SELECT id from Tea where name='"+teaName+"'");
	        if ( rs.first() )
	        {
	        	// Already exists
	        	throw new Exception("Tea "+teaName+" already exists");
	        }
	        // Unsafe - example only
	        int newIndex = 0;
	        rs = stmt.executeQuery("SELECT max(id) from Tea");
	        if ( rs.first() )
	        {
	        	newIndex = rs.getInt(1) + 1;
	        }
	        stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
	        							ResultSet.CONCUR_READ_ONLY);
	        stmt.executeUpdate("INSERT INTO Tea (id, name) VALUES ("+newIndex+", '"+teaName+"')");

	        MbElement rootElem = outAssembly.getMessage().getRootElement();
	        rootElem.getFirstElementByPath("JSON").delete();
	        rootElem.createElementAsLastChild("HTTPReplyHeader").createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "Location", "/tea/v1/"+newIndex);
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
