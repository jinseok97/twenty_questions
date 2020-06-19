import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * 서버와의 통신을 주로 담당한다.
 * 서버와 SSLSocket으로 연결하고, 정해진 프로토콜 번호에 따라 통신한다.
 */
public class Client {
    // GUI 클래스와 연결
    ClientWindow clientWindow;

    // RMI Object
    Game game;
    PrintWriter out;

    // 이름
    String username;

    // GUI 갱신 용도
    ArrayList<String> userList;
    boolean turn = false;
    boolean host = false;
    boolean isEnd = false;

    /**
     * GUI 클래스와 연결하고, lookup하여 RMI Object를 가져온다.
     * @param cw
     */
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

    /**
     * 서버와 연결하는 메소드
     *
     * SSLSocketFactory를 이용하여 소켓을 생성하고 서버와 연결한다.
     * 서버로부터 메시지를 수신하기 위한 스레드를 시작한다.
     *
     * @param serverIP
     * @param port
     * @param username
     */
    public void start(String serverIP, int port, String username) {
        try {
            // RMI Object의 addUser() 메소드 호출, 유저 이름 등록
            game.addUser(username);

            // 소켓을 생성하고 연결을 요청함
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverIP, port);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            // 서버로부터 메시지를 전송받기 위한 스레드 생성, 시작
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

    /**
     * 서버로부터 메시지를 전송 받는 스레드
     */
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

        /**
         * 메시지에 따른 이벤트를 처리한다.
         */
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
                                // 차례인 사람의 질문 출력, 출제자에게는 답변 창 팝업
                                clientWindow.addChat(msg);
                                if (host)
                                    clientWindow.answerQuestion();
                                clientWindow.update();
                                break;

                            case 25:
                                // 출제자의 답변
                                clientWindow.addChat(msg);
                                clientWindow.update();
                                break;

                            case 40:
                                // 게임 시작 메세지
                                clientWindow.addChat("게임을 시작합니다. '" + msg + "'님이 출제자입니다!");
                                clientWindow.update();
                                break;

                            case 41:
                                // 내 차례를 알림
                                turn = true;
                                clientWindow.update();
                                break;

                            case 98:
                                isEnd = true;
                                turn = false;
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

