import java.io.*;
import java.net.*;

import java.rmi.*;
import java.rmi.server.*;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 * 서버 역할을 하는 클래스
 */
public class Server extends UnicastRemoteObject {
    // serialVersionUID
    private static final long serialVersionUID = 1L;

    // 포트 번호
    final static int port = 1099;

    // SSLSocket
    SSLServerSocket serverSocket = null;
    SSLSocket socket = null;

    // RMI Object
    static Game game;

    // 유저 정보 관리 클래스
    private UserMap userMap;

    /**
     * UserMap과 GameImpl 클래스를 새로 생성한다.
     *
     * @throws RemoteException
     */
    public Server() throws RemoteException {
        super();
        userMap = new UserMap();
        game = new GameImpl();
        start();
    }

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
        System.setProperty("java.rmi.server.hostname", "localhost");
        System.setProperty("javax.net.debug", "ssl");

        String mServiceName = "room1";
        System.out.println("started at localhost and use default port(1099), Service name " +mServiceName);

        try {
            Server server = new Server();

            // RMI 객체 바인딩
            Naming.rebind("rmi://localhost:1099/" + mServiceName, game);
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * SSLSocketFactory를 이용해 서버 소켓을 생성한다.
     * 또한 서버 스레드를 생성하고, 시작한다.
     */
    public void start() {
        try {
            // 소켓을 생성한다.
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(1111);

            // 접속하는 클라이언트를 연결하는 스레드를 시작한다.
            new ServerThread().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 모든 유저에게 메시지를 전송한다.
     *
     * @param message
     */
    void sendToAll(String message) {
        for (String username : userMap.getMap().keySet()) {
            try {
                PrintWriter out = userMap.getMap().get(username).pw;
                out.println(message);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 현재 차례인 유저에게 메시지를 전송한다.
     *
     * @param msg
     */
    void sendToTurn(String msg) {
        User turnUser = userMap.getTurn();
        turnUser.pw.println(msg);
        turnUser.pw.flush();
    }

    /**
     * 랜덤 user의 turn = true로 설정하고 게임을 시작한다.
     * RMI Object에 시작된 게임의 상태를 저장한다.
     *
     * @param user
     */
    void gameStart(User user, String answer) {
        try {
            user.host = true;
            game.Start(user.name, answer);
            userMap.setRandomTurn();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * 게임이 끝났을 때 호출되는 함수
     */
    void gameEnd() {
        try {
            game.End();
            sendToAll("#98#");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 서버 실행과 함께 시작되는 스레드
     *
     * 접속한 클라이언트를 연결하기 위해 대기한다.
     * 클라이언트가 연결 되면 SeverReceiver 쓰레드를 실행한다.
     */
    class ServerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    socket = (SSLSocket) serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("[" + socket.getInetAddress() + " :" + socket.getPort() + "]" + "과 연결됨.\n");
                new ServerReceiver(socket).start();
            }
        }
    }

    /**
     * 클라이언트와 연결되어 메시지를 수신한다.
     */
    class ServerReceiver extends Thread {
        private final SSLSocket socket;
        private BufferedReader in = null;
        private PrintWriter out = null;
        private String name = "";

        ServerReceiver(SSLSocket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 메시지에 따른 이벤트를 처리한다.
         */
        @Override
        public void run() {
            String line = null;
            int num = 0;
            String msg = "";
            try {
                 // 클라이언트에서 전송하는 메시지를 받는다.
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("$")) {
                        num = Integer.parseInt(line.substring(1, 3));
                        msg = line.substring(4);
                        switch (num) {
                            case 01:
                                // 유저 연결
                                name = msg;
                                userMap.add(name, out);
                                sendToAll("#01#" + name);
                                break;

                            case 10:
                                // 일반 채팅
                                sendToAll("#10#" + name + " : " + msg);
                                break;

                            case 20:
                                // 질문
                                sendToAll("#20#[질문] " + name + " : " + msg);
                                break;

                            case 25:
                                // 답변
                                sendToAll("#25#[답변] " + name + " : " + msg);
                                userMap.setNextTurn();
                                sendToTurn("#41#");
                                if (game.isRunning())
                                    // 스무고개를 넘었을 경우 게임 종료
                                    if (game.showQuestions().size() == 20) {
                                        sendToAll("#10#정답을 맞추지 못하고 게임이 끝났습니다.");
                                        gameEnd();
                                    }
                                break;

                            case 29:
                                // 정답 처리
                                User winner = userMap.getTurn();
                                sendToAll("#10#'" + winner.name + "'님이 정답을 맞췄습니다!");
                                sendToAll("#10#정답은 '" + game.getFinalAnswer() +"' 입니다. 게임을 종료합니다.");
                                gameEnd();
                                break;

                            case 40:
                               // 게임 시작
                                gameStart(userMap.getUser(name), msg);
                                sendToAll("#40#" + name);
                                sendToTurn("#41#");
                                break;

                            case 49:
                                // 패스 처리
                                userMap.setNextTurn();
                                sendToTurn("#41#");
                                break;

                            default:
                                System.err.println("Exception");
                        }
                    } else {
                        System.out.println("Exception");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}