package de.thi.informatik.edi.simple.websocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.glassfish.tyrus.server.Server;

public class Application {

	public static void main(String[] args) {
		Server server = new Server("localhost", 12345, "/", new HashMap<>(), WebSocketServer.class);
		try {
			server.start();
			System.out.println("--- server is running");
			System.out.println("--- press any key to stop the server");
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			bufferRead.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.stop();
		}
	}
}