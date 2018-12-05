package trivadis.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import trivadis.simpleserver.CustomObjectMapper;

public class InfoHandler implements HttpHandler {
	private final CustomObjectMapper om= new CustomObjectMapper();

	

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		var theValues = new HashMap<String, Object>();
		theValues.put("Thread", Thread.currentThread().getName());
		theValues.put("Principal", exchange.getPrincipal());
		theValues.put("Attributes", exchange.getHttpContext().getAttributes());
		theValues.put("Headers", exchange.getRequestHeaders());
		theValues.put("Remote address", exchange.getRemoteAddress());
		theValues.put("Local address", exchange.getLocalAddress());
		
		theValues.put("Request URI", exchange.getRequestURI());
		String path = exchange.getRequestURI().getPath().replaceFirst(".*/([^/?]+).*", "$1");
		
		theValues.put("LastSegment", path);

		exchange.sendResponseHeaders(200, 0);// response code and length
		exchange.getResponseHeaders().add("Content-Type", "application/json");
		OutputStream out = exchange.getResponseBody();
		om.writeValue(out, theValues);
		out.close();

	}

}
