/**
 * java -Djavax.net.ssl.trustStore=trustedcerts -Djavax.net.ssl.trustStorePassword=123456 -Djava.rmi.server.hostname=localhost Client room1
 */

import java.io.*;
import java.net.*;

import java.rmi.*;
import java.rmi.server.*;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Server extends UnicastRemoteObject /*implements Quiz*/ {
    private static final long serialVersionUID = 1L;


    final static int port = 1099;

    boolean gameStart = false;
    String answer = null;

    SSLServerSocket serverSocket = null;
    SSLSocket socket = null;

    static Game game;
    private UserMap userMap;

    public Server() throws RemoteException {
        super();

        userMap = new UserMap();
        game = new GameImpl();
        start();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: Classname ServiceName");
            System.exit(1);
        }

        System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
        System.setProperty("java.rmi.server.hostname", "localhost");
        System.setProperty("javax.net.debug", "ssl");

        String mServiceName = args[0];
        System.out.println("started at localhost and use default port(1099), Service name " +mServiceName);

        try {
            Server server = new Server();
            Naming.rebind("rmi://localhost:1099/" + mServiceName, game);

        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(1111);
            System.out.println("SSLSOCKET");
            Thread thread = new ServerThread();
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 모든 클라이언트에 메시지를 전송한다.
     * @param message
     */
    void sendToAll(String message) {
        Iterator it = userMap.getMap().keySet().iterator();

        while (it.hasNext()) {
            try {
                PrintWriter out = (PrintWriter) userMap.getMap().get(it.next()).pw;
                out.println(message);
                out.flush();
            } catch (Exception e) {
            }
        }
    }

    /**
     * UserInfo.host == true 인 클라이언트에 메시지를 전송한다.
     *
     *
     * @param msg
     */
    void sendToHost(String msg) {
        Iterator it = userMap.getMap().keySet().iterator();

        while (it.hasNext()) {
            try {
                User user = userMap.getMap().get(it.next());
                if (user.host) {
                    PrintWriter out = user.pw;
                    out.println(msg);
                    out.flush();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * UserInfo.turn == true 인 클라이언트에 메시지를 전송한다.
     *
     *
     * @param msg
     */
    void sendToTurn(String msg) {
        Iterator it = userMap.getMap().keySet().iterator();

        while (it.hasNext()) {
            try {
                User user = userMap.getMap().get(it.next());
                if (user.turn) {
                    PrintWriter out = user.pw;
                    out.println(msg);
                    out.flush();
                }
            } catch (Exception e) {
            }
        }
    } // sendToOne()

    /**
     * user.turn = true로 설정한다.
     *
     * @param user
     */
    void setTurn(User user) {
        Iterator it = userMap.getMap().keySet().iterator();
        while (it.hasNext()) {
            try {
                User tmp = (User) it.next();
                if (tmp == user) {
                    user.turn = true;
                } else {
                    user.turn = false;
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * user.turn = true로 설정하고
     * 게임을 시작한다.
     *
     * @param user
     */
    void gameStart(User user) {

    }

    /**
     * 게임 시작 버튼을 누르면 시작되는 쓰레드
     * 대기하며 접속한 클라이언트를 연결하는 SeverReceiver 쓰레드를 실행한다.
     *
     */
    class ServerThread extends Thread {
        @Override
        public void run() {
            System.out.println("SERVERTHREAD RUN");
            while (true) {
                try {
                    socket = (SSLSocket) serverSocket.accept();
                    System.out.println("SERVERSOCKET ACCEPT");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "에서접속하였습니다.\n");
                ServerReceiver thread = new ServerReceiver(socket);
                thread.start();
            }

        }
    }

    /**
     * 클라이언트가 접속하면 클라이언트와 연결되어 listen하는 쓰레드
     */
    class ServerReceiver extends Thread {
        SSLSocket socket;/*
        DataInputStream in;
        DataOutputStream out;*/
        private BufferedReader in = null;
        private PrintWriter out = null;

        ServerReceiver(SSLSocket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
            }
        }

        /**
         * 프로토콜에 따라서
         * 메시지에 따른 이벤트를 처리한다.
         * 사용자가 접속을 종료하면 그에 따라 발생하는 이벤트를 처리한다.
         */
        @Override
        public void run() {
            System.out.println("SERVERRECEIVER RUN");
            String message = "";
            String line = null;
            StringTokenizer st = null;

            // 방장의 닉네임
            String name = "";

            int num = 0;
            try {
                 // 클라이언트에서 전송하는 메시지를 받는다.

                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                    sendToAll("hello ");
                    sendToAll(line);
                }
            } catch (IOException e) {
            }
            // 클라이언트에서 접속을 종료한 경우
            finally {
                sendToAll("100#" + name + "님이 나가셨습니다.");
                User exitUser = userMap.getUser(name);
                userMap.remove(name);

                // 방장이 나갈경우 나머지 사람 중 한명이 방장이 된다.
                if (exitUser.host) {
                    if (userMap.clients.size() >= 1) {
//                        userMap.getRandomUser().host = true;
                    }
                }
                System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "에서 접속을 종료하였습니다.\n");
                System.out.println("현재 서버접속자 수는 " + userMap.clients.size() + "입니다.\n");


            }
        } // run

        /**
         * 사용자가 전송한 msg를 파싱하고, 정답이라면 true, 틀렸다면 false 리턴
         * @param msg
         * @return
         */
        public boolean isAnswer(String msg) {
            String[] submit = msg.split("]", 2);
            return (submit[1].equalsIgnoreCase(answer));
        }

        /**
         * msg를 파싱하여 username을 리턴
         * @param msg
         * @return
         */
        public String getUser(String msg) {
            return msg.substring(1, msg.indexOf(']'));
        }


    } // ReceiverThread
}