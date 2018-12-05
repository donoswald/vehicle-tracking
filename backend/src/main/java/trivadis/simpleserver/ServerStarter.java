/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trivadis.simpleserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpServer;

import trivadis.handlers.InfoHandler;
import trivadis.handlers.VehicleHandler;
import trivadis.hyperledger.LedgerApi;

/**
 *
 * @author abs
 */
public class ServerStarter {

	private static final Logger LOG = Logger.getLogger(ServerStarter.class.getName());

	private static final LedgerApi api  =new LedgerApi();
	private static HttpServer server;

	public static void main(final String[] args) {
		int port = 9090;

		LOG.info("Starting Program");

		if (args != null && args.length > 0) {
			port = Integer.valueOf(args[0]);
		}
		try {
			final ExecutorService executor = Executors.newFixedThreadPool(5);
			server = HttpServer.create(new InetSocketAddress(port), 0);
			server.setExecutor(executor);

			server.createContext("/info", new InfoHandler()).setAuthenticator(new BasicAuthenticator("INFO") {

				@Override
				public boolean checkCredentials(String username, String password) {
					if ("admin".equals(username) && "admin123".equals(password))
						return true;
					return false;
				}
			});
			server.createContext("/" + VehicleHandler.HANDLER_URL, new VehicleHandler(api))
					.setAuthenticator(new VehicleBasicAuthenficator("VEHICLES"));

			server.start();
			LOG.log(Level.INFO, "Server is ready to serve requests on port: {0}", port);

		} catch (IOException ex) {
			LOG.info("Problem occured " + ex);
		}

	}

}
