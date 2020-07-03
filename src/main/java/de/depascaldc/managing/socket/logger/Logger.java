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
package de.depascaldc.managing.socket.logger;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.depascaldc.managing.socket.logger.LogUtil.LogType;
import de.depascaldc.managing.socket.main.MSMSocketServer;

public class Logger {
	private String dataPath;

	private boolean isTestRun = false;

	public static List<String> TESTS_CACHED_LOG = new ArrayList<String>();

	public Logger(boolean isTestRun) {
		this.isTestRun = isTestRun;
		info(ConsoleColors.CYAN_BOLD_BRIGHT + "=========================================================");
		info(ConsoleColors.CYAN_BOLD_BRIGHT + "=============== Logger - TESTS SocketServer =============");
		info(ConsoleColors.CYAN_BOLD_BRIGHT + "=============== Copyright © 2020 depascaldc =============");
		info(ConsoleColors.CYAN_BOLD_BRIGHT + "================== All Rights Reserved! =================");
		info(ConsoleColors.CYAN_BOLD_BRIGHT + "=========================================================");
	}

	public Logger(String PATH) {
		this.dataPath = new File(PATH).getAbsolutePath() + "/";
		if (!new File(dataPath + "logs/").exists()) {
			new File(dataPath + "logs/").mkdirs();
		}
		info(ConsoleColors.CYAN_BOLD_BRIGHT + "==========================================================");
		info(ConsoleColors.CYAN_BOLD_BRIGHT + "================ SocketServer by depascaldc ==============");
		info(ConsoleColors.CYAN_BOLD_BRIGHT + "=============== Copyright © 2020 depascaldc ==============");
		info(ConsoleColors.CYAN_BOLD_BRIGHT + "================== All Rights Reserved! ==================");
		info(ConsoleColors.CYAN_BOLD_BRIGHT + "==========================================================");
	}

	public void log(String message) {
		System.out.println(saveline(LogUtil.translate(LogType.LOG, message)));
	}

	public void out(String message) {
		String message_final = ConsoleColors.WHITE + "[OUT] > " + ConsoleColors.RESET + message + ConsoleColors.RESET;
		if (isTestRun) {
			TESTS_CACHED_LOG.add(ConsoleColors.stripColors(message_final));
		}
		System.out.println(message_final);
	}

	public void info(String message) {
		System.out.println(saveline(LogUtil.translate(LogType.INFO, message)));
	}

	public void debug(String message) {
		boolean show = true;
		if (MSMSocketServer.getProperties() != null) {
			if (MSMSocketServer.getProperties().getProperty("loglevel") != null) {
				int lv = Integer.valueOf(MSMSocketServer.getProperties().getProperty("loglevel"));
				if (lv < 0)
					show = false;
			}
		}
		if (show) {
			System.out.println(saveline(LogUtil.translate(LogType.DEBUG, message)));
		}
	}

	public void warn(String message) {
		System.out.print(saveline(LogUtil.translate(LogType.WARN, message)));
		System.out.println();
	}

	public void error(String message) {
		System.out.println(saveline(LogUtil.translate(LogType.ERROR, message)));
	}

	public void log(String message, Exception exception) {
		System.out.println(saveline(LogUtil.translate(LogType.LOG, message)));
		if (exception.getMessage() != null) {
			System.out.println(saveline(LogUtil.translate(LogType.LOG, exception.getMessage())));
		}
		if (exception.getStackTrace() != null) {
			int i = 0;
			for (StackTraceElement element : exception.getStackTrace()) {
				if (i > 1 && isTestRun) {
					break;
				} else {
					if (i > 19) {
						break;
					}
				}
				i++;
				System.out.println(saveline(LogUtil.translate(LogType.LOG, LogUtil.transLateStackElement(element))));
			}
		}
	}

	public void info(String message, Exception exception) {
		System.out.println(saveline(LogUtil.translate(LogType.INFO, message)));
		if (exception.getMessage() != null) {
			System.out.println(saveline(LogUtil.translate(LogType.INFO, exception.getMessage())));
		}
		if (exception.getStackTrace() != null) {
			int i = 0;
			for (StackTraceElement element : exception.getStackTrace()) {
				if (i > 1 && isTestRun) {
					break;
				} else {
					if (i > 19) {
						break;
					}
				}
				i++;
				System.out.println(saveline(LogUtil.translate(LogType.INFO, LogUtil.transLateStackElement(element))));
			}
		}
	}

	public void debug(String message, Exception exception) {
		boolean show = true;
		if (MSMSocketServer.getProperties() != null) {
			if (MSMSocketServer.getProperties().getProperty("loglevel") != null) {
				int lv = Integer.valueOf(MSMSocketServer.getProperties().getProperty("loglevel"));
				if (lv < 0)
					show = false;
			}
		}
		if (show) {
			System.out.println(saveline(LogUtil.translate(LogType.DEBUG, message)));
			if (exception.getMessage() != null) {
				System.out.println(saveline(LogUtil.translate(LogType.DEBUG, exception.getMessage())));
			}
			if (exception.getStackTrace() != null) {
				int i = 0;
				for (StackTraceElement element : exception.getStackTrace()) {
					if (i > 1 && isTestRun) {
						break;
					} else {
						if (i > 19) {
							break;
						}
					}
					i++;
					System.out.println(
							saveline(LogUtil.translate(LogType.DEBUG, LogUtil.transLateStackElement(element))));
				}
			}
		}
	}

	public void warn(String message, Exception exception) {
		System.out.println(saveline(LogUtil.translate(LogType.WARN, message)));
		if (exception.getMessage() != null) {
			System.out.println(saveline(LogUtil.translate(LogType.WARN, exception.getMessage())));
		}
		if (exception.getStackTrace() != null) {
			int i = 0;
			for (StackTraceElement element : exception.getStackTrace()) {
				if (i > 1 && isTestRun) {
					break;
				} else {
					if (i > 19) {
						break;
					}
				}
				i++;
				System.out.println(saveline(LogUtil.translate(LogType.WARN, LogUtil.transLateStackElement(element))));
			}
		}
	}

	public void error(String message, Exception exception) {
		System.out.println(saveline(LogUtil.translate(LogType.ERROR, message)));
		if (exception.getMessage() != null) {
			System.out.println(saveline(LogUtil.translate(LogType.ERROR, exception.getMessage())));
		}
		if (exception.getStackTrace() != null) {
			int i = 0;
			for (StackTraceElement element : exception.getStackTrace()) {
				if (i > 1 && isTestRun) {
					break;
				} else {
					if (i > 19) {
						break;
					}
				}
				i++;
				System.out.println(saveline(LogUtil.translate(LogType.ERROR, LogUtil.transLateStackElement(element))));
			}
		}
	}

	private String saveline(String message) {
		if (!isTestRun) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			Calendar cal = Calendar.getInstance();
			try {
				File file = new File("managerlogs/log_" + dateFormat.format(cal.getTime()) + ".log");
				FileWriter fr = new FileWriter(file, true);
				fr.write(String.valueOf(ConsoleColors.stripColors(message) + "\n"));
				fr.close();
			} catch (Exception folder) {
			}
		} else {
			TESTS_CACHED_LOG.add(ConsoleColors.stripColors(message));
		}
		return message;
	}

}
