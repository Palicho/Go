package tp.client;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class ServerTest {
    @Test
    public void testMultiplayerInit() throws IOException {
        Socket socket = new Socket("localhost", 9100);
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out.println("START 2");
        String result = in.readLine();

        assertEquals("WAITING", result);
    }

    @Test
    public void testSingleplayerInit() throws IOException {
        Socket socket = new Socket("localhost", 9100);
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out.println("START 1");
        String result = in.readLine();

        assertEquals("READY B", result);
    }
}
