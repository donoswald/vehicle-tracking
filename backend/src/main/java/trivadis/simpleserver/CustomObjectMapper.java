package trivadis.simpleserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class CustomObjectMapper extends ObjectMapper{
	public CustomObjectMapper() {
		//Configure object mapper
		super.enable(SerializationFeature.INDENT_OUTPUT);
	}
}
