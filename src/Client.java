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
import java.util.ArrayList;

public class Client {
    ClientWindow clientWindow;
    PrintWriter out;
    Game game;

    // 이름
    String username;
    ArrayList<String> userList;

    // 차례
    boolean turn = false;

    public Client(ClientWindow cw) {
        try {
            clientWindow = cw;
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

            new Thread(new ChatClientReceiver(socket)).start();
            sendToServer("$01$" + username);
        } catch (Exception ce) {
            ce.printStackTrace();
        }

        this.username = username;
    }

    public void sendToServer(String msg) {
        out.println(msg);
        out.flush();
    }

    class ChatClientReceiver extends Thread {
        SSLSocket socket;
        BufferedReader in;

        ChatClientReceiver(SSLSocket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            int num;
            String msg;
            while (in != null) {
                try {
                    msg = in.readLine();
                    if(msg.startsWith("#")) {
                        num = Integer.parseInt(msg.substring(1,3));
                        msg = msg.substring(4);

                        switch (num) {
                            case 01:
                                // 유저 연결
                                userList = game.getUserList();
                                clientWindow.addChat("'" + msg + "'님이 접속하였습니다.");
                                clientWindow.passTurn();
                                break;

                            case 10:
                                // 일반 채팅
                                clientWindow.addChat(msg);
                                break;

                            case 99:
                                // 접속 종료
                                userList = game.getUserList();
                                clientWindow.addChat("'" + msg + "'님이 나가셨습니다.");
                                clientWindow.passTurn();
                                break;

                            //...
                        }
                    }

                    // ...
                    System.out.println(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

