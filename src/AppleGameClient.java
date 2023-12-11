import javax.management.remote.JMXConnectorFactory;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;

public class AppleGameClient extends JFrame {
    private String name; //클라이언트 이름
    private int charIndex; //클라이언트가 선택한 캐릭터
    private int score = 0; //클라이언트 점수
    private Socket clientSocket;
    private InputStream is;
    private OutputStream out;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Vector<ClientInfo> clientInfos = new Vector<>();
    private Vector<ImageIcon> icons; //사과 캐릭터 배열

    private JSplitPane jSplitPane; //화면 분활
    ReadyPanel readyPanel;
    AppleGamePanel gamePanel;
    AppleGameChatPanel chatPanel;
    AppleGameClientInfoPanel clientInfoPanel;

    public AppleGameClient(String name, int charIndex, Vector<ImageIcon> icons) {
        this.name = name;
        this.charIndex = charIndex;
        this.icons = icons;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            clientSocket = new Socket("localhost", 9999); //클라이언트 소켓 생성, 서버에 연결
            is = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
            dos = new DataOutputStream(out);
            dis = new DataInputStream(is);


            //서버에서 이름과 선택한 캐릭터 인덱스를 보냄
            if (dos != null) {
                dos.writeUTF(name);
                dos.writeUTF(String.valueOf(charIndex));
                dos.flush();
            }

            ois = new ObjectInputStream(is);
            clientInfos = (Vector<ClientInfo>) ois.readObject(); //클라이언트 정보들이 담긴 벡터를 받음

            System.out.println("AppleGameClient.java clientInfos = " + clientInfos.size());
            readyPanel = new ReadyPanel(this, clientSocket, name, clientInfos, icons);
            setContentPane(readyPanel);
            ///잠시 게임 화면 테스트를 위해 준비화면


        } catch (Exception e) {
            e.printStackTrace();
        }

        setSize(1000, 700);
        setVisible(true);

        ReceiveMsg receiveMsg = new ReceiveMsg();
        receiveMsg.start();

    }

    public void makeDiviedScreen(Vector<ClientInfo> clientInfos) { //새로 업데이트된 클라이언트의 정보를 받게 됨

        //ReadyPanel.ReceiveMsg.interrupted();

        JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        ImageIcon appleIcon = icons.get(charIndex);

        gamePanel = new AppleGamePanel(name, clientSocket, appleIcon);

        clientInfoPanel = new AppleGameClientInfoPanel(clientInfos, icons);

        chatPanel = new AppleGameChatPanel(clientSocket,name);

        horizontalSplitPane.setLeftComponent(gamePanel);
        horizontalSplitPane.setRightComponent(verticalSplitPane);

        verticalSplitPane.setTopComponent(clientInfoPanel);
        verticalSplitPane.setBottomComponent(chatPanel);

        setContentPane(horizontalSplitPane);

        horizontalSplitPane.setEnabled(false);
        verticalSplitPane.setEnabled(false);

        setVisible(true);

        horizontalSplitPane.setDividerLocation(0.7);  // 좌우 화면의 비율 조절 (0.5는 중앙으로 설정)
        verticalSplitPane.setDividerLocation(0.4);

    }

    class ReceiveMsg extends Thread { //서버로부터 온 메시지를 수신한다.
        JLabel clientLabel;
        String readyName;
        Vector<ClientInfo> newClientInfo;

        @Override
        public void run() {
            while (!this.interrupted()) {
                try {
                    String msg = dis.readUTF(); //msg를 가져옴
                    msg = msg.trim(); //trim 메소드를 사용하여 앞 뒤의 공백을 제거
                    String stArray[] = msg.split(" ");
//                    System.out.println("들어온 메시지 => "+msg);
                    if (msg.startsWith("/readyChat")) { //대기 시간에 나눈 채팅
                        String chatMessage = msg.substring("/readyChat".length()).trim();
                        readyPanel.appendTextArea(chatMessage + "\n");

                    }
                    else if (msg.startsWith("/chat")) { //게임 도중 나눈 채팅
                        String chatMessage = msg.substring("/chat".length()).trim();
                        System.out.println("chat으로 메시지 들어옴");
                        if(chatPanel!=null)
                            chatPanel.appendTextArea(chatMessage+"\n");

                    }
                    else if (msg.startsWith("/addUser")) {
                        readyPanel.addUser(stArray[1], Integer.parseInt(stArray[3]));

                    } else if (msg.startsWith("/readyOn")) {
                        readyPanel.readyOn(stArray[1]);

                    } else if (msg.startsWith("/readyOff")) {
                        readyPanel.readOff(stArray[1]);

                    } else if (msg.startsWith("/allReady")) {
                        System.out.println("/allReady 메시지 받음");
                        makeDiviedScreen(clientInfos);
                    }
                    else if(msg.startsWith("/count"))
                        readyPanel.countMark(stArray[1]);
                      // readyLabel.setText(stArray[1]);

                    else if (msg.startsWith("/score")) {
                        String name = stArray[1];
                        int score = Integer.parseInt(stArray[2]);
                        clientInfoPanel.setClientInfoScore(name,score);
                    }
                    else if(msg.startsWith("/gameOver")){
                        Color grayColor = new Color(0, 0, 0, 128);
                        JPanel gameGrayPanel = new JPanel();
                        gameGrayPanel.setBackground(grayColor);
                        gameGrayPanel.setBounds(0,0,gamePanel.getWidth(),gamePanel.getHeight());
                        gamePanel.add(gameGrayPanel,0);
                        gamePanel.repaint();

                        JPanel clientGrayPanel = new JPanel();
                        clientGrayPanel.setBackground(grayColor);
                        clientGrayPanel.setBounds(0,0,clientInfoPanel.getWidth(),clientInfoPanel.getHeight());
                        clientInfoPanel.add(clientGrayPanel,0);
                        clientInfoPanel.repaint();

                        JPanel chatGrayPanel = new JPanel();
                        chatGrayPanel.setBackground(grayColor);
                        chatGrayPanel.setBounds(0,0,chatPanel.getWidth(),chatPanel.getHeight());
                        chatPanel.add(chatGrayPanel,0);
                        chatPanel.repaint();

//                        JPanel grayPanel = new JPanel();
//                        grayPanel.setBackground(grayColor);
//                        grayPanel.setBounds(0,0,AppleGameClient.this.getWidth(),AppleGameClient.this.getHeight());
//                        AppleGameClient.this.add(grayPanel,0);
//                        AppleGameClient.this.repaint();


                        Vector<ClientInfo> updateClientInfos = clientInfoPanel.getClientInfos();
                        GameOverPanel gameOverPanel = new GameOverPanel(updateClientInfos);

                        JDialog gameOverDialog = new JDialog(AppleGameClient.this, "Game Over", Dialog.ModalityType.APPLICATION_MODAL);
                        gameOverDialog.setContentPane(gameOverPanel);
                        gameOverDialog.setSize(500,500);
                        gameOverDialog.setLocationRelativeTo(AppleGameClient.this);
                        gameOverDialog.setVisible(true);



////                        grayDialog.setBackground(grayColor);
////                        grayDialog.setBounds(0,0,AppleGameClient.this.getWidth(),AppleGameClient.this.getHeight());
////                        AppleGameClient.this.add(grayDialog);
////                        JPanel grayPanel = new JPanel();
//                        GrayPanel grayPanel = new GrayPanel();
//                        //grayPanel.setOpaque(false);
//                       // grayPanel.setBackground(grayColor);
//                       // gameOverDialog.setUndecorated(true);  // 다이얼로그의 장식을 제거하여 대화 상자처럼 만듭니다.
//                        gameOverDialog.setContentPane(grayPanel);
//                        gameOverDialog.setSize(AppleGameClient.this.getSize());
//                        gameOverDialog.setLocationRelativeTo(AppleGameClient.this);
//                        gameOverDialog.setVisible(true);





                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}




