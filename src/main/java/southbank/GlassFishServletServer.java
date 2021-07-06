package southbank;

import java.io.File;
import java.util.Map;

import org.glassfish.embeddable.*;

class GlassFishServletServer implements ServletServer {
	private GlassFish server;

	public String getName() {
		return "GlassFish";
	}

	public void start(Map<String, String> contexts, int httpPort, int httpsPort, String keystoreFile, String keystorePass)
		throws Exception {

		if (server != null) {
			throw new IllegalStateException("Web server is already running.");
		}

		// https://wikis.oracle.com/display/GlassFish/3.1EmbeddedOnePager#3.1EmbeddedOnePager-4.1.Details%3A
		GlassFishProperties gfProps = new GlassFishProperties();
		if (httpPort > 0) {
			gfProps.setPort("http-listener", httpPort);
		}
		if (httpsPort > 0) {
			gfProps.setPort("https-listener", httpsPort);
		}

		server = GlassFishRuntime.bootstrap().newGlassFish(gfProps);
		server.start();

		for (String contextPath : contexts.keySet()) {
			String warPath = contexts.get(contextPath);

			File war = new File(warPath);
			Deployer deployer = server.getDeployer();
			deployer.deploy(war, "--name="+war.getName(), "--contextroot="+contextPath, "--force=true");
		}
	}

	public void stop() throws Exception {
		if (server == null) {
			throw new IllegalStateException("Web server is not running.");
		}

		server.stop();
		server.dispose();

		server = null;
	}
}
