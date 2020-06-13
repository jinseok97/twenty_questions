/**
 * java -Djavax.net.ssl.trustStore=trustedcerts -Djavax.net.ssl.trustStorePassword=123456 Client room1 123456
 */

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    PrintWriter out;
    Game game;
    User user;

    // 이름
    String username;

    // 차례
    boolean turn = false;

    public Client() {
        try {
            game = (Game) Naming.lookup("rmi://localhost:1099/room1");
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public void start(String serverIP, int port, String username) {
        try {
            // 소켓을 생성하여 연결을 요청한다.
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverIP, port);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.println("$100" + username + "$");
            out.flush();

            new Thread(new ChatClientReceiver(socket)).start();

        } catch (Exception ce) {
            ce.printStackTrace();
        }

        user = new User(username, out);
    }

    public void sendMessage(String msg) {
        out.println(msg);
        out.flush();
    }
}

class ChatClientReceiver extends Thread {
    SSLSocket socket;
    BufferedReader in;

    ChatClientReceiver(SSLSocket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
        }
    } // 생성자


    @Override
    public void run() {
        while (in != null) {
            try {
                String msg = in.readLine();

                // ...
                System.out.println(msg);

            } catch (IOException e) {
            }
        }
    }
}