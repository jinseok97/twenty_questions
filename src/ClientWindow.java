import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class ClientWindow {
    static MyJFrame myJFrame = new MyJFrame();
    Client client = new Client();
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
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        client.start("localhost", port, myJFrame.tfName.getText());

        myJFrame.tfName.setEnabled(false);
        myJFrame.btSaveName.setEnabled(false);
    }


    public void passTurn() {
    }

    public void send() {
        String msg = myJFrame.tfChat.getText();
        client.sendMessage(msg);

        myJFrame.tfChat.setText("");
    }
    public void button1() {
    }
    public void button2() {
    }
    public void button3() {
    }
    public void button4() {
    }

    public void update() {
    }
}

class MyJFrame extends JFrame {

    ClientWindow clientWindow = new ClientWindow();

    JPanel panelMain;
    JTextField tfName, tfChat, tfHost;
    JButton btSend, btSaveName, btPassTurn;
    JButton btExit;
    JButton btButton1, btButton2, btButton3, btButton4;


    DefaultListModel chatModel;
    DefaultListModel userModel;
    JList<String> listChat;
    JList<String> listUser;

    public MyJFrame() { // 전체적인 GUI 설정
        this.setResizable(false);
        setTitle("스무고개 게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 400);

        /*===== Main Panel =====*/
        panelMain = new JPanel();
        panelMain.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(panelMain);
        panelMain.setLayout(null);

        JLabel lbName = new JLabel("사용자 이름");
        lbName.setFont(new Font("굴림", Font.PLAIN, 12));
        lbName.setBounds(15, 12, 70, 15);
        panelMain.add(lbName);

        tfName = new JTextField();
        tfName.setBounds(75, 10, 110, 21);
        tfName.setColumns(10);
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


        btPassTurn = new JButton("패스");
        btPassTurn.setFont(new Font("굴림", Font.PLAIN, 12));
        btPassTurn.setEnabled(false);
        btPassTurn.setBounds(384, 382, 103, 23);
        btPassTurn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.passTurn();
                clientWindow.update();
            }
        });
        panelMain.add(btPassTurn);

        btExit = new JButton("종료");
        btExit.setFont(new Font("굴림", Font.PLAIN, 12));
        btExit.setBounds(384, 415, 103, 23);
        btExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });
        panelMain.add(btExit);

        chatModel = new DefaultListModel();
        listChat = new JList<String>();
        listChat.setModel(chatModel);
        listChat.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        listChat.setBounds(10, 40, 280, 290);
        listChat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane chatScroll = new JScrollPane(listChat);
        chatScroll.setBounds(10, 40, 280, 290);
        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelMain.add(chatScroll);

        tfHost = new JTextField();
        tfHost.setBounds(296, 40, 98, 21);
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

        btButton1 = new JButton("버튼1");
        btButton1.setFont(new Font("굴림", Font.PLAIN, 12));
        btButton1.setBounds(296, 180, 98, 34);
        btButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.button1();
                clientWindow.update();
            }
        });
        panelMain.add(btButton1);

        btButton2 = new JButton("버튼1");
        btButton2.setFont(new Font("굴림", Font.PLAIN, 12));
        btButton2.setBounds(296, 215, 98, 34);
        btButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.button2();
                clientWindow.update();
            }
        });
        panelMain.add(btButton2);

        btButton3 = new JButton("버튼1");
        btButton3.setFont(new Font("굴림", Font.PLAIN, 12));
        btButton3.setBounds(296, 250, 98, 34);
        btButton3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.button3();
                clientWindow.update();
            }
        });
        panelMain.add(btButton3);

        btButton4 = new JButton("버튼1");
        btButton4.setFont(new Font("굴림", Font.PLAIN, 12));
        btButton4.setBounds(296, 285, 98, 34);
        btButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.button4();
                clientWindow.update();
            }
        });
        panelMain.add(btButton4);

        tfChat = new JTextField();
        tfChat.setBounds(6, 340, 288, 20);
        tfChat.setColumns(10);
        panelMain.add(tfChat);

        btSend = new JButton("전송");
        btSend.setFont(new Font("굴림", Font.PLAIN, 12));
        btSend.setBounds(294, 339, 103, 23);
        btSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientWindow.send();
                clientWindow.update();
            }
        });
        panelMain.add(btSend);
    }
}
