/**
 *   Copyright © 2020 | depascaldc | Discord: [depascaldc]#4093
 *   __  __                                                   _   
 *  |  \/  | __ _ _ __   __ _  __ _  ___ _ __ ___   ___ _ __ | |_ 
 *  | |\/| |/ _` | '_ \ / _` |/ _` |/ _ \ '_ ` _ \ / _ \ '_ \| __|
 *  | |  | | (_| | | | | (_| | (_| |  __/ | | | | |  __/ | | | |_ 
 *  |_|  |_|\__,_|_| |_|\__,_|\__, |\___|_| |_| |_|\___|_| |_|\__|
 *                           |___/                               
 * 
 *   Copyright © 2020 | depascaldc | Discord: [depascaldc]#4093
 *   
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   Copyright © 2020 | depascaldc | Discord: [depascaldc]#4093
 *   
 */
package de.depascaldc.managing.socket.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import de.depascaldc.managing.socket.console.JLineTerminalCLI;
import de.depascaldc.managing.socket.logger.Logger;
import de.depascaldc.managing.socket.server.WebsocketServer;

public enum MSMSocketServer {

	INSTANCE;

	private static File propertiesFile;
	private static Logger logger;
	private static Properties properties;
	private static PropertiesManager propertiesManager;
	private static WebsocketServer websocketServer;

	private static JLineTerminalCLI JLT;

	static boolean running;

	public static void initialize(String MAIN_PATH) {
		propertiesFile = new File(MAIN_PATH, "server.properties");
		propertiesManager = new PropertiesManager(propertiesFile);
		properties = propertiesManager.getProperties();
		logger = new Logger(MAIN_PATH);
		// TODO: example start.sh for infinite loop of program
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					getLogger().info("Shutting down executing shutdown hook ...");
					Thread.sleep(200);
					getLogger().info("Closing SocketServer ...");
					websocketServer.stopServer();
					Thread.sleep(200);
					getLogger().info("Shutdown Terminal... Remiving all Lisner");
					JLT.close();
					Thread.sleep(200);
					getLogger().info("Shutdownhook executed succesfully... Bye!");
				} catch (Exception e) {
				}
			}
		});
		websocketServer = new WebsocketServer();
		websocketServer.runServer();
		runAsync(new Runnable() {
			@Override
			public void run() {
				running = true;
				try {
					JLT = new JLineTerminalCLI(logger);
				} catch (Exception e) {
					logger.error("Terminal could not be loaded...", e);
				}
			}
		});
	}

	public static void saveFile(File file, InputStream input) throws IOException {
		String stringFromInputStream = IOUtils.toString(input, "UTF-8");
		Files.write(Paths.get(file.getPath()), stringFromInputStream.getBytes());
	}

	public static void runAsync(Runnable run) {
		new Thread(run).start();
	}

	public static Logger getLogger() {
		return logger;
	}

	public static File getPropertiesFile() {
		return propertiesFile;
	}

	public static Properties getProperties() {
		return properties;
	}

	public static boolean isRunning() {
		return running;
	}

}
