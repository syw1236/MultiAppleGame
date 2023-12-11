import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Vector;

public class ReadyPanel extends JPanel {
    Vector<ClientInfo> clientInfos = new Vector<>();
    Vector<ImageIcon> icons = new Vector<>();
    Vector<JLabel> readyVector = new Vector<>(); //레디여부를 나타내는 벡터들
    Vector<JLabel> clientInfoVector = new Vector<>(); //클라이언트 정보를 가진 라벨들을 담은 벡터
   // private boolean isReady = false; //레디 여부
    JLabel isReadyLabel;
    JLabel myIsReadyLabel;
    String myName; //해당 클라이언트 이름
    int myNum; //클라이언트들 중 자신의 번수가 몇 번인지 나타내는 변수
    boolean myIsready;

    InputStream is;
    OutputStream out;
    DataInputStream dis;
    DataOutputStream dos;
    ObjectInputStream ois;
    Socket clientSocket;
    JTextArea chatTextArea; //전체 채팅창
    AppleGameClient appleGameClient;
    JLabel readyLabel; //전체 화면에 ready를 나타내는 것

    public ReadyPanel(AppleGameClient appleGameClient,Socket clientSocket, String myName, Vector<ClientInfo> clientInfos, Vector<ImageIcon> icons) throws IOException {

        setLayout(null);
        this.appleGameClient = appleGameClient;
        this.clientSocket = clientSocket;
        this.myName = myName;
        this.clientInfos = clientInfos;
        this.icons = icons;

        is = clientSocket.getInputStream();
        out = clientSocket.getOutputStream();
        dis = new DataInputStream(is);
        dos = new DataOutputStream(out);

        System.out.println("clientInfo.size = "+clientInfos.size());


        readyLabel = new JLabel("모두 Ready하면 게임이 시작됩니다.");
        readyLabel.setFont(new Font("Arial", Font.BOLD, 35));
        readyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        readyLabel.setVerticalAlignment(SwingConstants.CENTER);
        readyLabel.setOpaque(true);
        setReadyLabelColor(readyLabel,Color.RED,Color.YELLOW);
        readyLabel.setBounds(1,1,1000,50);
        add(readyLabel);

        drawClientInfos(clientInfos); //클라이언트들의 정보를 그리는 함수 호출

        //채팅창
        chatTextArea = new JTextArea();
        chatTextArea.setFont(new Font("Arial", Font.PLAIN, 17));
        chatTextArea.setBounds(50,450,550,200);
        chatTextArea.setLineWrap(true); // 자동 줄 바꿈 활성화
        chatTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatTextArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // 가로 스크롤 비활성화

//        scrollPane.setPreferredSize(new Dimension(600, 200));
        scrollPane.setSize(600,200);
        scrollPane.setLocation(50,400);
        // JFrame의 컨텐트 팬에 JScrollPane 추가
        add(scrollPane);

        JTextField inputChat = new JTextField();
        inputChat.setFont(new Font("Arial", Font.PLAIN, 17));
        inputChat.setBounds(50,600,530,50);
        add(inputChat);

        JButton readyBtn = new JButton("Ready");
        readyBtn.setOpaque(true);
        readyBtn.setBorderPainted(false);
        readyBtn.setFont(new Font("Arial", Font.BOLD, 35));
        readyBtn.setBackground(Color.orange);
        readyBtn.addActionListener(new ActionListener() {
            String msg;
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg;
                if(!myIsready) {
                    msg = "/readyOn " + myName;
                }
                else{
                    msg = "/readyOff " + myName;
                }

                try{
                    dos.writeUTF(msg);
                    myIsready = !myIsready;
                }catch (Exception err){
                    err.printStackTrace();
                }

            }
        });
        readyBtn.setBounds(680,580,300,50);
        add(readyBtn);

        JButton sendBtn = new JButton("전송");
        sendBtn.setOpaque(true);
        sendBtn.setForeground(Color.gray);
        sendBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        sendBtn.setBounds(580,600,70,50);
        sendBtn.addActionListener(new ActionListener() { //메시지가 오면
            @Override
            public void actionPerformed(ActionEvent e) {
                try{

                    dos.writeUTF("/readyChat < "+myName+" > "+inputChat.getText());
                    System.out.println("클라이언트 입력 메시지 ="+inputChat.getText());
                    inputChat.setText("");

                }catch (Exception err){
                    err.printStackTrace();
                }
            }
        });
        add(sendBtn);

//        ReceiveMsg receiveMsg = new ReceiveMsg();
//        receiveMsg.start();
//        ReceiveObject receiveObject = new ReceiveObject();
//        receiveObject.start();


    }


    public void appendTextArea(String msg){
        chatTextArea.append(msg);
        chatTextArea.setCaretPosition(chatTextArea.getText().length());
    }
    public void drawClientInfos(Vector<ClientInfo> clientInfos){ //클라이언트들의 정보를 그리는 곳
        System.out.println("매개변수로 들어온 clientInfos size");
        System.out.println(clientInfos.size());


        for(int i=0;i<clientInfos.size();i++) {
            ClientInfo clientInfo = clientInfos.get(i);
            //캐릭터인덱스, 이름
            int charIndex = clientInfo.getCharIndex();
            String name = clientInfo.getName();
            boolean isReady = clientInfo.getIsReady();

            ImageIcon charIcon = icons.get(charIndex);
            //System.out.println("선택한 캐릭터 인덱스 ="+clientInfo.charIndex);


            JLabel nameLabel = new JLabel(name); //사용자의 이름
            clientInfoVector.add(nameLabel); //클라이언트 정보 라벨을 담는 벡터에 해당 이름 라벨을 담음

            nameLabel.setOpaque(true);
            nameLabel.setBackground(Color.white);
            nameLabel.setForeground(Color.black);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 25));
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            nameLabel.setVerticalAlignment(SwingConstants.CENTER);
            nameLabel.setBounds(i * 210 + 100, 100, 150, 30);
            add(nameLabel);

            int width = 150;  // 원하는 너비
            int height = 150; // 원하는 높이
            Image scaledImage = charIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon changeCharIcon = new ImageIcon(scaledImage);

            JLabel charLabel = new JLabel(changeCharIcon); //사용자가 선택한 캐릭터
            clientInfoVector.add(charLabel); //클라이언트 정보 라벨을 담는 벡터에 해당 캐릭터 라벨을 담음

            charLabel.setOpaque(true);
            charLabel.setBackground(Color.white);
            charLabel.setBounds(i * 210 + 100, 150, charLabel.getIcon().getIconWidth(), charLabel.getIcon().getIconHeight());
            add(charLabel);

            JLabel isReadyLabel = new JLabel("Ready"); //ready 여부를 나타내는 라벨
            clientInfoVector.add(isReadyLabel); //클라이언트 정보 라벨을 담는 벡터에 해당 ready 여부를 나타내는 라벨을 담음
            //readyVector.add(isReadyLabel);

            if (name.equals(myName)) {
                myIsReadyLabel = isReadyLabel;
                myNum = i;
                myIsready = isReady; //나의 ready 여부를 받는 것

            }
            isReadyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            isReadyLabel.setVerticalAlignment(SwingConstants.CENTER);

            isReadyLabel.setFont(new Font("Arial", Font.BOLD, 35));
            isReadyLabel.setOpaque(true); //배경색이 칠해지도록 허락함


            if (isReady) { //ready 되어있다면
                System.out.println("true");
                setReadyLabelColor(isReadyLabel, Color.orange, Color.black);
            } else{
                System.out.println("false");
                setReadyLabelColor(isReadyLabel, Color.gray, Color.lightGray);
            }

            isReadyLabel.setBounds(i*210+85,305,180,60);
            add(isReadyLabel);
            this.repaint();


        }
    }
    public void setReadyLabelColor(JLabel readyLabel,Color backgroundColor,Color forgroundColor){
        System.out.println("색상 변경함");
        readyLabel.setBackground(backgroundColor);
        readyLabel.setForeground(forgroundColor);
    }

    public void removeClientInfoLabel(Vector<JLabel> clientInfoVector){
        for(JLabel clientIoLabel : clientInfoVector){ //클라이언트 관련 라벨을 다 삭제함
            ReadyPanel.this.remove(clientIoLabel);
        }
        clientInfoVector.clear();

    }

    public void addUser(String name,int charIndex){
        if(!name.equals(myName)) {
            System.out.println("if문으로 들어옴");
            clientInfos.add(new ClientInfo(name, 0, charIndex));
           // newClientInfo = clientInfos;
           // System.out.println("stArray[1] = "+stArray[1]);
           // System.out.println("stArray[2] = "+stArray[3]);
            drawClientInfos(clientInfos); //클라이언트 정보를 가지고 있는 부분을 다시 그림
            ReadyPanel.this.repaint();
        }
    }

    public void readyOn(String name){
        String readyName = name;
        //readyName = stArray[1];
       // System.out.println("readyOn으로 받은 메시지 이름 = "+stArray[1]);
        for(int i=0;i<clientInfos.size();i++){
            ClientInfo clientInfo = clientInfos.get(i);
            if(clientInfo.getName().equals(readyName)){
                clientInfo.setIsReady(true);
                System.out.println("true로 변경");
                break;
            }
        }
        removeClientInfoLabel(clientInfoVector);
        drawClientInfos(clientInfos); //새로 업데이트 된 clienInfos를 그림
        ReadyPanel.this.repaint();
    }
    public void readOff(String name){
        String readyName = name;
        for(int i=0;i<clientInfos.size();i++){
            ClientInfo clientInfo = clientInfos.get(i);
            if(clientInfo.getName().equals(readyName)){
                clientInfo.setIsReady(false);
                System.out.println("false로 변경");
                break;
            }
        }
        removeClientInfoLabel(clientInfoVector);
        drawClientInfos(clientInfos);
    }

    public void countMark(String count){
        readyLabel.setText(count);

    }

//    class ReceiveMsg extends Thread{ //서버로부터 온 메시지를 수신한다.
//        JLabel clientLabel;
//        String readyName;
//        Vector<ClientInfo> newClientInfo;
//        @Override
//        public void run(){
//            while(!this.interrupted()) {
//                try {
//                    String msg = dis.readUTF(); //msg를 가져옴
//                    msg = msg.trim(); //trim 메소드를 사용하여 앞 뒤의 공백을 제거
//                    String stArray[] = msg.split(" ");
////                    System.out.println("들어온 메시지 => "+msg);
//                    if(msg.startsWith("/chat")){
//                        String chatMessage = msg.substring("/chat".length()).trim();
//                        chatTextArea.append(chatMessage+"\n");
//                        chatTextArea.setCaretPosition(chatTextArea.getText().length());
//                        //chatTextArea.append(stArray[1] + " " + stArray[2] + "\n");
//                    }
//                    else if(msg.startsWith("/addUser")){
//                        if(!stArray[1].equals(myName)) {
//                            System.out.println("if문으로 들어옴");
//                            clientInfos.add(new ClientInfo(stArray[1], 0, Integer.parseInt(stArray[3])));
//                            newClientInfo = clientInfos;
//                            System.out.println("stArray[1] = "+stArray[1]);
//                            System.out.println("stArray[2] = "+stArray[3]);
//                            drawClientInfos(newClientInfo); //클라이언트 정보를 가지고 있는 부분을 다시 그림
//                            ReadyPanel.this.repaint();
//                        }
//                    }
//                    else if(msg.startsWith("/readyOn")){
//                        readyName = stArray[1];
//                        System.out.println("readyOn으로 받은 메시지 이름 = "+stArray[1]);
//                        for(int i=0;i<clientInfos.size();i++){
//                            ClientInfo clientInfo = clientInfos.get(i);
//                            if(clientInfo.getName().equals(readyName)){
//                                clientInfo.setIsReady(true);
//                                System.out.println("true로 변경");
//                                break;
//                            }
//                        }
//                        removeClientInfoLabel(clientInfoVector);
//                        drawClientInfos(clientInfos); //새로 업데이트 된 clienInfos를 그림
//                        ReadyPanel.this.repaint();
//                    }
//                    else if(msg.startsWith("/readyOff")){
//                        readyName = stArray[1];
//                        for(int i=0;i<clientInfos.size();i++){
//                            ClientInfo clientInfo = clientInfos.get(i);
//                            if(clientInfo.getName().equals(readyName)){
//                                clientInfo.setIsReady(false);
//                                System.out.println("false로 변경");
//                                break;
//                            }
//                        }
//                        removeClientInfoLabel(clientInfoVector);
//                        drawClientInfos(clientInfos);
//                    }
//                    else if(msg.startsWith("/allReady")){
//                        //ReadyPanel.this.setVisible(false);
//                        System.out.println("/allReady 메시지 받음");
//                        this.interrupt();
//                        appleGameClient.makeDiviedScreen(clientInfos);
//                    }
//                    else if(msg.startsWith("/count"))
//                        readyLabel.setText(stArray[1]);
//                    else if(msg.startsWith("/score")){
//                        System.out.println("/score 메싲 들어옴");
//                    }
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//    }



}
