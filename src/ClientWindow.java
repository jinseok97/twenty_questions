import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ClientWindow {
    static MyJFrame myJFrame = new MyJFrame();
    Client client = new Client(this);

    // 포트 번호
    int port = 1111;

    private static final String password = "123456";

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore", "trustedcerts");
        System.setProperty("javax.net.ssl.trustStorePassword", password);
        System.setProperty("javax.net.debug", "ssl");

        myJFrame.setVisible(true);
    }

    /**
     *  유저의 이름을 입력하고 서버와 연결을 시작하는 함수
     *  중복 검사와 자리수 검사를 시행한다.
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

            client.game.addUser(myJFrame.tfName.getText());
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
     *  UI 하단 일반 대화창의 내용을 전송하는 함수
     *  서버에게 일반대화 프로토콜 번호와 함께 그 내용을 보낸다.
     */
    public void send() {
        String msg = "$10$" +myJFrame.tfChatInput.getText();
        client.sendToServer(msg);
        myJFrame.tfChatInput.setText("");
    }

    /**
     *  게임 시작을 누른 유저가 host가 되며, 최종 정답을 입력받고 서버에게 게임시작 프로토콜 번호와 함께 그 내용을 보낸다.
     *  현재 게임 정보를 담고 있는 RMI Object를 실행한다.
     */
    public void gameStart() {
        try {
            String answer;
            if ((answer = JOptionPane.showInputDialog(null, "최종 정답을 입력해주세요.", "정답 입력", JOptionPane.OK_CANCEL_OPTION)) == null)
                return;
            String msg = "$40$" + answer;
            client.sendToServer(msg);
            client.host = true;
            client.game.Start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *  host에게 질문을 전송하게 되며, 일반 대화와는 별도이다.
     *  질문 프로토콜 번호와 함께 그 내용을 전송하고, 질문 내용은 RMI Object에 저장한다.
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
     *  host가 질문을 받았을 때 그에 대한 답변을 전송하는 함수
     *  답변 프로토콜 번호와 함께 그 내용을 전송하고, 답변 내용은 RMI Object에 저장한다.
     */
    public void answerQuestion() {
        try {
            String answer;
            if ((answer = JOptionPane.showInputDialog(null,
                    client.game.getLastQuestion(), "대답 입력", JOptionPane.OK_OPTION)) == null)
                return;
            String msg = "$25$" + answer;
            client.game.addAnswer(msg);
            client.sendToServer(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *  현재까지 나온 질문과 답변 목록을 출력한다.
     *  RMI Object에 저장된 질문과 답변 정보를 사용한다.
     */
    public void showQuestions() {
        try {
            myJFrame.taIncoming.append("----현재까지의 질문과 답변 목록입니다----\n");
            ArrayList<String> result = client.game.showQuestions();
            for(String each : result) {
                myJFrame.taIncoming.append(each + "\n");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *  다음 타자에게 턴을 넘긴다.
     */
    public void passTurn() {
        client.turn = false;
        client.sendToServer("$49$");

    }

    /**
     * 서버로부터 전송받은 내용을 대화창 textarea에 올린다.
     * @param msg
     */
    public void addChat(String msg) {
        myJFrame.taIncoming.append(msg + "\n");
    }

    /**
     * UI를 최신 정보로 갱신한다.
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
            if (client.host) {
                myJFrame.btQuestion.setEnabled(false);
                myJFrame.btPassTurn.setEnabled(false);
            }
            if (client.turn) {
                myJFrame.btQuestion.setEnabled(true);
                myJFrame.btPassTurn.setEnabled(true);
            } else {
                myJFrame.btQuestion.setEnabled(false);
                myJFrame.btPassTurn.setEnabled(false);
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
    JButton btSaveName, btSend;

    JButton btStart, btQuestion, btPassTurn, btShow;

    JTextArea taIncoming;

    DefaultListModel userModel;
    JList<String> listUser;

    public MyJFrame() { // 전체적인 GUI 설정
        this.setResizable(false);
        setTitle("스무고개 게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 400);

        //====== Main Panel ======//
        panelMain = new JPanel();
        panelMain.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(panelMain);
        panelMain.setLayout(null);

        /** 사용자 이름 - 입력 - 저장 */
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

        /** 호스트 - 유저리스트 */
        tfHost = new JTextField();
        tfHost.setBounds(296, 40, 98, 21);
        tfHost.setEditable(false);
        tfHost.setColumns(10);
        panelMain.add(tfHost);

        userModel = new DefaultListModel();
        listUser = new JList<String>();
        listUser.setModel(userModel);
        listUser.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        listUser.setBounds(300, 70, 90, 100);
        listUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane userScroll = new JScrollPane(listUser);
        userScroll.setBounds(300, 70, 90, 100);
        userScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelMain.add(userScroll);

        /** 게임시작 - 패스 - 질문 - 목록 버튼 */
        btStart = new JButton("게임 시작하기");
        btStart.setFont(new Font("굴림", Font.PLAIN, 12));
        btStart.setBounds(296, 180, 98, 34);
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
        btQuestion.setBounds(296, 215, 98, 34);
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
        btPassTurn.setBounds(296, 250, 98, 34);
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
        btShow.setBounds(296, 285, 98, 34);
        btShow.setEnabled(false);
        btShow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.showQuestions();
                clientWindow.update();
            }
        });
        panelMain.add(btShow);

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
