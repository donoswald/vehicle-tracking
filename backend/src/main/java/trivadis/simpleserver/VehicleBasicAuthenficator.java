package trivadis.simpleserver;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.sun.net.httpserver.BasicAuthenticator;
import trivadis.handlers.VehicleHandler;

public class VehicleBasicAuthenficator extends BasicAuthenticator {
	private static final Logger LOG = Logger.getLogger(VehicleBasicAuthenficator.class.getName());

	private static final Map<String, String> USERS = new HashMap<>() {
		{
			put(VehicleHandler.ORG1, "secret");
			put(VehicleHandler.ORG2, "secret");
		}
	};

	public VehicleBasicAuthenficator(String realm) {
		super(realm);
	}

	@Override
	public boolean checkCredentials(String username, String password) {
		LOG.info("Autories User " + username + " with password " + password);
		if (password != null && USERS.containsKey(username) && password.equals(USERS.get(username)))
			return true;
		return false;
	}

}
