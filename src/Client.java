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

    // GUI 업데이트 용도
    ArrayList<String> userList;
    boolean turn = false;
    boolean host = false;

    public Client(ClientWindow cw) {
        try {
            // GUI 구현 클래스와 연결
            clientWindow = cw;
            // RMI Object를 가져옴
            game = (Game) Naming.lookup("rmi://localhost:1099/room1");
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public void start(String serverIP, int port, String username) {
        try {
            // 소켓을 생성하고 연결을 요청함
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverIP, port);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            new Thread(new ChatClientReceiver(socket)).start();
            // 서버에 클라이언트 정보 생성과 연결을 요청
            sendToServer("$01$" + username);
        } catch (Exception ce) {
            ce.printStackTrace();
        }

        this.username = username;
    }

    /**
     * 서버로 메세지를 전송한다.
     * @param msg
     */
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
                                clientWindow.update();
                                break;

                            case 10:
                                // 일반 채팅
                                clientWindow.addChat(msg);
                                clientWindow.update();
                                break;

                            case 20:
                                clientWindow.addChat(msg);
                                if (host)
                                    clientWindow.answerQuestion();
                                clientWindow.update();
                                break;

                            case 25:
                                clientWindow.addChat(msg);
                                clientWindow.update();
                                break;

                            case 40:
                                // 게임 시작 메세지
                                clientWindow.addChat("게임을 시작합니다. '" + msg + "'님이 출제자입니다!");
                                clientWindow.update();
                                break;

                            case 41:
                                // 내 차례
                                turn = true;
                                clientWindow.addChat("내 차례입니다!");
                                clientWindow.update();
                                break;

                            case 99:
                                // 접속 종료
                                userList = game.getUserList();
                                clientWindow.addChat("'" + msg + "'님이 나가셨습니다.");
                                clientWindow.update();
                                break;
                        }
                    }

                    System.out.println(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

