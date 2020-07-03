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
package de.depascaldc.managing.socket.server.endponts;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.EncodeException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import de.depascaldc.managing.socket.logger.Logger;
import de.depascaldc.managing.socket.main.MSMSocketServer;
import de.depascaldc.managing.socket.server.encoding.Message;
import de.depascaldc.managing.socket.server.encoding.MessageEncoder;

@WebSocket(maxIdleTime = 1000*60*10) // // TODO: clients must ping with auth each 10 mins otherwise close session
public class MessagingEndpoint {

	private static final Logger log = MSMSocketServer.getLogger();
	private Session session;
	private static Set<Session> sessions = new CopyOnWriteArraySet<>();

	private static MessageEncoder mE = new MessageEncoder();
	
	@OnWebSocketClose
	public void onClose(int statusCode, String reason) throws IOException, EncodeException {
		// TODO: check AUTH
		log.info("SocketServer.onClose::" + session.getRemoteAddress());
		sessions.remove(session);
		Message message = new Message();
		message.setFrom(session.getRemoteAddress().getHostName());
		message.setContent("Disconnected!");
		broadcast(message);
	}

	@OnWebSocketError
	public void onError(Throwable t) {
		log.error("SocketServer.onError::" + t.getMessage());
		Message message = new Message();
		message.setFrom("SERVER THREAD");
		message.setContent(t.getMessage());
	}

	@OnWebSocketConnect
	public void onConnect(Session session) throws IOException, EncodeException {
		// TODO: check AUTH
		log.info("SocketServer.onConnect::" + session.getRemoteAddress());
		this.session = session;
		sessions.add(session);
		Message message = new Message();
		message.setFrom(session.getRemoteAddress().toString());
		message.setContent("Connected!");
		broadcast(message);
	}

	@OnWebSocketMessage
	public void onMessage(String message) throws IOException, EncodeException {
		// TODO: check AUTH
		log.out("SocketServer.onMessage::From=" + session.getRemoteAddress() + " Message=" + message);
		Message message2 = new Message();
		message2.setFrom(session.getRemoteAddress().toString());
		message2.setContent(message);
		broadcast(message2);
	}

	public static void broadcast(Message message) throws IOException, EncodeException {
		String jisson = mE.encode(message);
		sessions.forEach(endpoint -> {
			synchronized (endpoint) {
				try {
					endpoint.getRemote().sendString(jisson);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
