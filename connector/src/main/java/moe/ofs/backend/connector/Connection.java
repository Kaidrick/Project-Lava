package moe.ofs.backend.connector;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Connection class represents a wrapper container of socket along with its input stream and output stream
 * A connection instance can be called to write or read from the socket, and can be used to close the socket
 */
public class Connection {
    private Socket socket;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private BufferedReader bufferedReader;

    public Connection(String address, int port, int timeout) throws IOException {

        this.socket = new Socket();
        socket.connect(new InetSocketAddress(address, port), timeout);

        socket.setKeepAlive(true);
        socket.setReuseAddress(true);
        socket.setTcpNoDelay(true);

        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());

        bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream, StandardCharsets.UTF_8));

        transmitAndReceive("\r\n");
    }

    public Connection(int port) throws IOException {
        this("localhost", port, 5000);
    }

    public Connection(int port, int timeout) throws IOException {
        this("localhost", port, timeout);
    }

    public Connection(String address, int port) throws IOException {
        this(address, port, 5000);
    }

    public String transmitAndReceive(String jsonString) throws IOException {
        dataOutputStream.write((jsonString + "\n").getBytes(StandardCharsets.UTF_8));
        dataOutputStream.flush();

        return bufferedReader.readLine();
    }

    public void close() throws IOException {
        dataInputStream.close();
        dataOutputStream.close();
        socket.close();
    }
}
