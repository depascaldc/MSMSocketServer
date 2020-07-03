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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public class PropertiesManager {

	private File propertiesFile;

	private Properties properties;

	public PropertiesManager(File propertiesFile) {
		properties = new Properties();
		if (!propertiesFile.exists()) {
			try {
				MSMSocketServer.saveFile(propertiesFile, getDefaultProperties());
				properties.load(new FileReader(propertiesFile));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				properties.load(new FileReader(propertiesFile));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private InputStream getDefaultProperties() throws IOException {
		// TODO placeholder for auth at fist save
		InputStream inputStream = MSMSocketServer.class.getResourceAsStream("/server.properties");
		String text = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
		return new ByteArrayInputStream(text.getBytes(Charset.forName("UTF-8")));
	}

	public Properties getProperties() {
		return properties;
	}

	public File getPropertiesFile() {
		return propertiesFile;
	}

}
