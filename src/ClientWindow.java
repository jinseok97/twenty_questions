import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class ClientWindow {
    static MyJFrame myJFrame = new MyJFrame();
    Client client = new Client(this);
    int port = 1111;

    private static final String password = "123456";

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore", "trustedcerts");
        System.setProperty("javax.net.ssl.trustStorePassword", password);
        System.setProperty("javax.net.debug", "ssl");

        myJFrame.setVisible(true);
    }

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

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void send() {
        String msg = "$10$" +myJFrame.tfChatInput.getText();
        client.sendToServer(msg);
        myJFrame.tfChatInput.setText("");
    }
    public void startGame() {
        try {
            client.game.setIsStart();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void askQuestion() {
    }
    public void pass() {
    }
    public void button4() {
    }

    public void passTurn() {
        String[] userList = client.userList.toArray(new String[client.userList.size()]);
        myJFrame.listUser.setListData(userList);
    }

    public void addChat(String msg) {
        myJFrame.taIncoming.append(msg + "\n");
    }

    public void update() {
        try {
            if (client.game.isRunning()) {
                myJFrame.btStart.setEnabled(false);
                myJFrame.btPassTurn.setEnabled(true);
                myJFrame.btQuestion.setEnabled(true);
                myJFrame.btButton4.setEnabled(true);
            } else {
                myJFrame.btStart.setEnabled(true);
                myJFrame.btPassTurn.setEnabled(false);
                myJFrame.btQuestion.setEnabled(false);
                myJFrame.btButton4.setEnabled(false);
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

    JButton btStart, btQuestion, btPassTurn, btButton4;

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
                clientWindow.passTurn();
            }
        });
        panelMain.add(tfName);

        btSaveName = new JButton("저장");
        btSaveName.setFont(new Font("굴림", Font.PLAIN, 12));
        btSaveName.setBounds(180, 10, 65, 23);
        btSaveName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.saveName();
                clientWindow.passTurn();
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
        btStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.startGame();
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
                clientWindow.passTurn();
            }
        });
        panelMain.add(btQuestion);

        btPassTurn = new JButton("패스하기");
        btPassTurn.setFont(new Font("굴림", Font.PLAIN, 12));
        btPassTurn.setBounds(296, 250, 98, 34);
        btPassTurn.setEnabled(false);
        btPassTurn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.pass();
                clientWindow.passTurn();
            }
        });
        panelMain.add(btPassTurn);

        btButton4 = new JButton("질문 목록");
        btButton4.setFont(new Font("굴림", Font.PLAIN, 12));
        btButton4.setBounds(296, 285, 98, 34);
        btButton4.setEnabled(false);
        btButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.button4();
                clientWindow.passTurn();
            }
        });
        panelMain.add(btButton4);

        /** 대화 입력창 - 전송 버튼 */
        tfChatInput = new JTextField();
        tfChatInput.setBounds(6, 340, 288, 20);
        tfChatInput.setColumns(10);
        tfChatInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientWindow.send();
                clientWindow.passTurn();
            }
        });
        panelMain.add(tfChatInput);

        btSend = new JButton("전송");
        btSend.setFont(new Font("굴림", Font.PLAIN, 12));
        btSend.setBounds(294, 339, 103, 23);
        btSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.send();
                clientWindow.passTurn();
            }
        });
        panelMain.add(btSend);
    }
}
