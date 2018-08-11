package sav.tcp;

import java.io.IOException;
import java.net.Socket;

public class TcpConnector {
	private int tcpPort;
	private Socket server;

	public TcpConnector(int tcpPort) {
		this.tcpPort = tcpPort;
	}

	public Socket connect() throws Exception {
		while (true) {
			server = new Socket("localhost", tcpPort);
			// TODO: set timeout!
			break;
		}
		return server;
	}

	public void close() {
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
