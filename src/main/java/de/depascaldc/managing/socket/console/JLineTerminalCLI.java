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
package de.depascaldc.managing.socket.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.jline.builtins.Options.HelpException;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReader.Option;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.terminal.Cursor;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.DiffHelper.Operation;
import org.jline.utils.InfoCmp.Capability;

import de.depascaldc.managing.socket.logger.ConsoleColors;
import de.depascaldc.managing.socket.logger.Logger;
import de.depascaldc.managing.socket.server.encoding.Message;
import de.depascaldc.managing.socket.server.endponts.MessagingEndpoint;

public class JLineTerminalCLI {

	private static Logger log;

	private String prompt = ConsoleColors.WHITE + "console" + ConsoleColors.GREEN + "> " + ConsoleColors.RESET;
	private String rightPrompt = ConsoleColors.BLUE + "<" + ConsoleColors.RESET;
	private Character mask = null;
	private String trigger = null;
	private Completer completer = null;
	private Parser parser = null;
	private List<Consumer<LineReader>> callbacks = new ArrayList<>();
	private static Terminal terminal;
	private LineReader reader;

	public JLineTerminalCLI(Logger logger) throws IOException, InterruptedException {
		try {
			log = logger;
			Object[] map = new Object[256];
			for (int i = 0; i < 256; i++) {
				map[i] = Operation.INSERT;
			}
			TerminalBuilder builder = TerminalBuilder.builder();
			terminal = builder.build();
			completer = new ArgumentCompleter(new Completer() {
				@Override
				public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
					candidates.add(new Candidate("stop"));
					candidates.add(new Candidate("kill"));
					candidates.add(new Candidate("push"));
				}
			});
			reader = LineReaderBuilder.builder().terminal(terminal).completer(completer).parser(parser)
					.variable(LineReader.SECONDARY_PROMPT_PATTERN, rightPrompt).variable(LineReader.INDENTATION, 2)
					.option(Option.INSERT_BRACKET, true).build();
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
				reader.callWidget(LineReader.CLEAR);
				reader.callWidget(LineReader.REDRAW_LINE);
				reader.callWidget(LineReader.REDISPLAY);
				reader.getTerminal().writer().flush();
			}, 1, 1, TimeUnit.SECONDS);
			reader.setOpt(LineReader.Option.MOUSE);
			reader.getWidgets().put(LineReader.CALLBACK_INIT, () -> {
				terminal.trackMouse(Terminal.MouseTracking.Any);
				return true;
			});
			reader.getWidgets().put(LineReader.MOUSE, () -> {
				MouseEvent event = reader.readMouseEvent();
				StringBuilder tsb = new StringBuilder();
				Cursor cursor = terminal.getCursorPosition(c -> tsb.append((char) c));
				reader.runMacro(tsb.toString());
				String msg = "          " + event.toString();
				int w = terminal.getWidth();
				terminal.puts(Capability.cursor_address, 0, Math.max(0, w - msg.length()));
				terminal.writer().append(msg);
				terminal.puts(Capability.cursor_address, cursor.getY(), cursor.getX());
				terminal.flush();
				return true;
			});
			new JLTCLI_KeypressWidget(reader);
			callbacks.forEach(c -> c.accept(reader));
			if (!callbacks.isEmpty()) {
				Thread.sleep(2000);
			}

			while (true) {
				String line = null;
				try {
					line = reader.readLine(prompt, rightPrompt, (MaskingCallback) null, null);
					line = line.trim();
					terminal.flush();
					if (line != null) {
						runCommand(line);
					}
				} catch (Exception e) {
					try {
						HelpException.highlight(e.getMessage(), HelpException.defaultStyle()).print(terminal);
					} catch (Exception e2) {
					}
				}
			}
		} catch (Exception e) {
			logger.error("COULD NOT INIT TERMINAL...", e);
			System.exit(0);
		}
	}

	public static void runCommand(String command) {
		log.out(AttributedString.fromAnsi("\u001B[33m[CMD Ran]: \u001B[0m\"" + command + "\"").toAnsi(terminal));
		String label = "";
		if (command.startsWith("/")) {
			command = command.substring(1);
		}
		String[] args = StringUtils.split(command);
		label = args[0];
		args = StringUtils.split(command.replaceFirst(label, ""));
		if (command.equalsIgnoreCase("quit") || command.equalsIgnoreCase("exit") || command.equalsIgnoreCase("stop")) {
			System.exit(0);
			return;
		}
		if (command.startsWith("push")) {
			Message message = new Message();
			message.setFrom("Console");
			message.setTo("all");
			message.setContent(StringUtils.join(args));
			try {
				MessagingEndpoint.broadcast(message);
				log.out("PUSHED... " + message.getContent());
			} catch (Exception e) {
				log.out("COULD NOT PUSH MESSAGE Exception: " + e.getMessage());
			}
		}
	}

	public void close() {
		try {
			reader.getWidgets().clear();
		} catch (Exception e) {
		}
		try {
			terminal.close();
		} catch (Exception e) {
		}
	}

	class KeyPress {
		private final int keyCode;
		private final char keyChar;

		public KeyPress(int keyCode, char keyChar) {
			this.keyCode = keyCode;
			this.keyChar = keyChar;
		}

		public char getKeyChar() {
			return keyChar;
		}

		public int getKeyCode() {
			return keyCode;
		}
	}

	public Logger getLog() {
		return log;
	}

	public Character getMask() {
		return mask;
	}

	public String getTrigger() {
		return trigger;
	}

}
