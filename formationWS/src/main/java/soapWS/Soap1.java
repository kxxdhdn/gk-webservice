package soapWS;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(targetNamespace = "http://soapWS/", portName = "Soap1Port", serviceName = "Soap1Service")
public class Soap1 {
	
@WebMethod(operationName = "direBonjour", action = "urn:DireBonjour")
public String direBonjour (@WebParam(name = "arg0") String name) {
		
		return "Bonjour Monsieur "+name;
	}

}
