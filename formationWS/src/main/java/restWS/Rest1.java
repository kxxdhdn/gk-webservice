package restWS;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.*;

@WebService(targetNamespace = "http://restWS/", portName = "Rest1Port", serviceName = "Rest1Service")
@Path("/")
public class Rest1 {
	
	@WebMethod(operationName = "direBonjour", action = "urn:DireBonjour")
	@POST
	@Path(value = "/bonjour")
	@Produces("text/plain")
	public String direBonjour (@WebParam(name = "arg0") String name) {
		
		return "Bonjour Mr "+name;
	}

}
