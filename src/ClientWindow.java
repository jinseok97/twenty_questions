import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * ClientWindow는 사용자와의 상호작용을 담당한다.
 * 텍스트 입력, 버튼 클릭 등의 이벤트를 처리하고, 서버로부터 수신한 내용을 화면에 업데이트한다.
 * RMI Object 혹은 Client 객체를 통해서 서버와 통신한다.
 */
public class ClientWindow {
    static MyJFrame myJFrame = new MyJFrame();
    Client client = new Client(this);

    // 포트 번호
    int port = 1111;

    // 인증서 비밀번호
    private static final String password = "123456";

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore", "trustedcerts");
        System.setProperty("javax.net.ssl.trustStorePassword", password);
        System.setProperty("javax.net.debug", "ssl");

        myJFrame.setVisible(true);
    }

    /**
     *  유저의 이름을 입력하고 서버와 연결을 시작하는 메소드
     *
     *  RMI 객체의 메소드를 이용해 중복 검사와 자리수 검사를 시행한다.
     *  RMI 객체를 이용하기 때문에 서버와 소켓 생성, 연결이 불필요하다.
     */
    public void saveName(){
        try {
            // 이름이 입력되지 않았을 경우
            if (myJFrame.tfName.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "이름을 입력하세요!", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // 이름이 중복되거나 6자를 초과한 경우
            if (!client.game.checkNameFormat(myJFrame.tfName.getText())) {
                JOptionPane.showMessageDialog(null, "중복되거나 6자를 초과합니다!", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            client.start("localhost", port, myJFrame.tfName.getText());

            myJFrame.tfName.setEnabled(false);
            myJFrame.btSaveName.setEnabled(false);
            myJFrame.btSend.setEnabled(true);
            myJFrame.btStart.setEnabled(true);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *  UI 하단 일반 대화창의 내용을 전송하는 메소드
     *  서버에게 일반대화 프로토콜 번호($10$)와 함께 그 내용을 보낸다.
     */
    public void send() {
        String msg = "$10$" +myJFrame.tfChatInput.getText();
        client.sendToServer(msg);
        myJFrame.tfChatInput.setText("");
    }

    /**
     * 게임 시작을 누른 유저가 host가 되며,
     * 최종 정답을 입력받고 서버에게 게임시작 프로토콜 번호($40$)와 함께 그 내용을 보낸다.
     *
     * 단, RMI Object로 현재 유저 수에 접근하여 3인 이상일 때만 서버에 시작 메세지를 전송한다.
     */
    public void gameStart() {

        try {
            String answer;
            if (client.game.getUserList().size() > 2) {
                if ((answer = JOptionPane.showInputDialog(null,
                    "최종 정답을 입력해주세요.", "정답 입력", JOptionPane.OK_CANCEL_OPTION)) != null) {
                    String msg = "$40$" + answer;
                    client.sendToServer(msg);
                    client.host = true;
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "3인 이상이 필요합니다!", "알림", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 출제자에게 질문을 전송하며, 일반 대화와는 다르게 동작한다.
     * 질문 프로토콜 번호($20$)와 함께 그 내용을 전송하고, 질문 내용은 RMI Object에 저장한다.
     */
    public void askQuestion() {
        try {
            String question;
            if ((question = JOptionPane.showInputDialog(null, "당신의 질문은?", "질문 입력", JOptionPane.OK_CANCEL_OPTION)) == null)
                return;

            String msg = "$20$" + question;
            client.game.addQuestion(msg);
            client.sendToServer(msg);
            client.turn = false;

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * 출제자가 질문을 받았을 때 그에 대한 답변을 전송하는 함수이다.
     * 답변 프로토콜 번호($25$)와 함께 그 내용을 전송하고, 답변 내용은 RMI Object에 저장한다.
     */
    public void answerQuestion() {
        try {
            int res;
            String msg;
            String[] buttons = {"정답!", "아니오", "예"};
            do {
                // RMI Object에 저장된 마지막 질문을 가져온다.
                res = JOptionPane.showOptionDialog(null,
                        client.game.getLastQuestion(), "답변을 입력하세요", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, buttons, "예");
            } while (res == JOptionPane.CLOSED_OPTION);

            if (res == 0)   // 정답처리
                client.sendToServer("$29$");
            else {
                msg = "$25$" + buttons[res];
                client.game.addAnswer(msg);
                client.sendToServer(msg);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 현재까지 나온 질문과 답변 목록을 출력한다.
     * RMI Object에 저장된 질문과 답변 정보를 사용한다.
     * 소켓 통신이 아닌 RMI Object를 이용하여 로컬 메소드 호출하듯이 사용할 수 있다.
     */
    public void showQuestions() {
        try {
            myJFrame.taIncoming.append("\n----현재까지의 질문과 답변 목록입니다----\n");
            ArrayList<String> result = client.game.showQuestions();
            for(String each : result) {
                myJFrame.taIncoming.append(each + "\n");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 다음 타자에게 턴을 넘기도록 서버에 요청한다. ($49$)
     */
    public void passTurn() {
        client.turn = false;
        client.sendToServer("$49$");
    }

    /**
     * 서버로부터 전송받은 내용을 대화창 textArea에 올린다.
     *
     * @param msg
     */
    public void addChat(String msg) {
        myJFrame.taIncoming.append(msg + "\n");
    }

    /**
     * UI를 최신 정보로 갱신한다.
     * RMI Object에 명시된 게임 진행 여부를 알아온다.
     */
    public void update() {
        try {
            // 현재 접속 유저 리스트
            String[] userList = client.game.getUserList().toArray(new String[client.game.getUserList().size()]);
            myJFrame.listUser.setListData(userList);

            // 현재 게임의 호스트
            myJFrame.tfHost.setText(client.game.getHostname());

            // 게임 진행 여부에 따른 버튼 활성화
            if (client.game.isRunning()) {
                myJFrame.btStart.setEnabled(false);
                myJFrame.btPassTurn.setEnabled(true);
                myJFrame.btQuestion.setEnabled(true);
                myJFrame.btShow.setEnabled(true);
            } else {
                myJFrame.btStart.setEnabled(true);
                myJFrame.btPassTurn.setEnabled(false);
                myJFrame.btQuestion.setEnabled(false);
                myJFrame.btShow.setEnabled(false);
            }

            // 출제자, 차례 여부에 따른 버튼 활성화
            if (client.host) {
                myJFrame.btQuestion.setEnabled(false);
                myJFrame.btPassTurn.setEnabled(false);
                myJFrame.lbTurn.setText(client.game.getFinalAnswer());
            }
            else if (client.turn) {
                myJFrame.btQuestion.setEnabled(true);
                myJFrame.btPassTurn.setEnabled(true);
                myJFrame.lbTurn.setText("내 차례입니다!");
            } else {
                myJFrame.btQuestion.setEnabled(false);
                myJFrame.btPassTurn.setEnabled(false);
                myJFrame.lbTurn.setText("");
            }

            if (client.isEnd) {
                myJFrame.btStart.setEnabled(false);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}

class MyJFrame extends JFrame {
    ClientWindow clientWindow = new ClientWindow();

    JPanel panelMain;
    JTextField  tfName, tfChatInput, tfHost;
    JButton btSaveName, btSend, btStart, btQuestion, btPassTurn, btShow;
    JLabel lbTurn;

    JTextArea taIncoming;

    DefaultListModel userModel;
    JList<String> listUser;

    // 전체 GUI 초기 설정
    public MyJFrame() {
        this.setResizable(false);
        setTitle("스무고개 게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 400);

        // Main Panel
        panelMain = new JPanel();
        panelMain.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(panelMain);
        panelMain.setLayout(null);

        /** 사용자 이름 - 입력 영역 - 저장버튼 */
        JLabel lbName = new JLabel("사용자 이름");
        lbName.setFont(new Font("굴림", Font.PLAIN, 12));
        lbName.setBounds(15, 12, 70, 15);
        panelMain.add(lbName);

        tfName = new JTextField();
        tfName.setBounds(75, 10, 110, 21);
        tfName.setColumns(10);
        tfName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientWindow.saveName();
                clientWindow.update();
            }
        });
        panelMain.add(tfName);

        btSaveName = new JButton("저장");
        btSaveName.setFont(new Font("굴림", Font.PLAIN, 12));
        btSaveName.setBounds(180, 10, 65, 23);
        btSaveName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.saveName();
                clientWindow.update();
            }
        });
        panelMain.add(btSaveName);


        /** 채팅창 영역 */
        taIncoming = new JTextArea(15, 50);
        taIncoming.setBounds(10, 40, 280, 290);
        taIncoming.setLineWrap(true);
        taIncoming.setWrapStyleWord(true);
        taIncoming.setEditable(false);

        JScrollPane chatScroll = new JScrollPane(taIncoming);
        chatScroll.setBounds(10, 40, 280, 290);
        chatScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        chatScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panelMain.add(chatScroll);

        /** 출제자명, 유저 목록 */
        JLabel lbHost = new JLabel("출제자");
        lbHost.setFont(new Font("굴림", Font.PLAIN, 10));
        lbHost.setBounds(296, 30, 70, 15);
        panelMain.add(lbHost);

        tfHost = new JTextField();
        tfHost.setBounds(296, 42, 98, 21);
        tfHost.setEditable(false);
        tfHost.setColumns(10);
        panelMain.add(tfHost);

        // 유저목록
        JLabel lbUser = new JLabel("유저목록");
        lbUser.setFont(new Font("굴림", Font.PLAIN, 10));
        lbUser.setBounds(296, 62, 70, 15);
        panelMain.add(lbUser);

        userModel = new DefaultListModel();
        listUser = new JList<String>();
        listUser.setModel(userModel);
        listUser.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        listUser.setBounds(300, 75, 90, 93);
        listUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane userScroll = new JScrollPane(listUser);
        userScroll.setBounds(300, 75, 90, 93);
        userScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelMain.add(userScroll);


        /** 시작 - 질문 - 패스 - 목록 버튼 */
        btStart = new JButton("게임 시작하기");
        btStart.setFont(new Font("굴림", Font.PLAIN, 12));
        btStart.setBounds(296, 170, 98, 34);
        btStart.setEnabled(false);
        btStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.gameStart();
                clientWindow.update();
            }
        });
        panelMain.add(btStart);

        btQuestion = new JButton("질문하기");
        btQuestion.setFont(new Font("굴림", Font.PLAIN, 12));
        btQuestion.setBounds(296, 205, 98, 34);
        btQuestion.setEnabled(false);
        btQuestion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.askQuestion();
                clientWindow.update();
            }
        });
        panelMain.add(btQuestion);

        btPassTurn = new JButton("패스하기");
        btPassTurn.setFont(new Font("굴림", Font.PLAIN, 12));
        btPassTurn.setBounds(296, 240, 98, 34);
        btPassTurn.setEnabled(false);
        btPassTurn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.passTurn();
                clientWindow.update();
            }
        });
        panelMain.add(btPassTurn);

        btShow = new JButton("질문목록");
        btShow.setFont(new Font("굴림", Font.PLAIN, 12));
        btShow.setBounds(296, 275, 98, 34);
        btShow.setEnabled(false);
        btShow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.showQuestions();
                clientWindow.update();
            }
        });
        panelMain.add(btShow);

        // 턴 알림
        lbTurn = new JLabel();
        lbTurn.setFont(new Font("굴림", Font.PLAIN, 11));
        lbTurn.setBounds(296, 310, 100, 15);
        lbTurn.setHorizontalAlignment((int) CENTER_ALIGNMENT);
        panelMain.add(lbTurn);


        /** 대화 입력창 - 전송 버튼 */
        tfChatInput = new JTextField();
        tfChatInput.setBounds(6, 340, 288, 20);
        tfChatInput.setColumns(10);
        tfChatInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientWindow.send();
                clientWindow.update();
            }
        });
        panelMain.add(tfChatInput);

        btSend = new JButton("전송");
        btSend.setFont(new Font("굴림", Font.PLAIN, 12));
        btSend.setBounds(294, 339, 103, 23);
        btSend.setEnabled(false);
        btSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.send();
                clientWindow.update();
            }
        });
        panelMain.add(btSend);
    }
}
