import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Vector;

public class AttendClientsPanel extends JPanel {
    Socket clientSocket;
    String myName;
    int myNum; //클라이언트들 중 자신의 번수가 몇 번인지 나타내는 변수
    JLabel myIsReadyLabel;

    Vector<ClientInfo> clientInfos;
    Vector<ImageIcon> icons;

    JLabel isReadyLabel;
    Vector<JLabel> readyVector = new Vector<>(); //레디여부를 나타내는 벡터들
    InputStream is;
    OutputStream out;
    DataInputStream dis;
    DataOutputStream dos;


    public  AttendClientsPanel(Socket clientSocket, String myName, Vector<ClientInfo> clientInfos, Vector<ImageIcon> icons){
        setLayout(null);
        this.clientSocket = clientSocket;
        this.myName = myName;
        this.clientInfos = clientInfos;
        this.icons = icons;
        try {
            is = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
            dis = new DataInputStream(is);
            dos = new DataOutputStream(out);

            ReceiveMsg receiveMsg = new ReceiveMsg();
            receiveMsg.start();

        }catch (Exception e){
            e.printStackTrace();
        }

        drawClientInfos(clientInfos); //화면을 그림
    }

    public void drawClientInfos(Vector<ClientInfo> clientInfos){ //클라이언트들의 정보를 그리는 곳
        System.out.println("매개변수로 들어온 clientInfos size");
        System.out.println(clientInfos.size());
        for(int i=0;i<clientInfos.size();i++){
            ClientInfo clientInfo = clientInfos.get(i);
            //캐릭터인덱스, 이름
            ImageIcon charIcon = this.icons.get(clientInfo.charIndex);

            String name = clientInfo.name;
            JLabel nameLabel = new JLabel(name); //사용자의 이름
            nameLabel.setOpaque(true);
            nameLabel.setBackground(Color.white);
            nameLabel.setForeground(Color.black);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 25));
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            nameLabel.setVerticalAlignment(SwingConstants.CENTER);
            nameLabel.setBounds(i*210+100,100,150,30);
            add(nameLabel);

            int width = 150;  // 원하는 너비
            int height = 150; // 원하는 높이
            Image scaledImage = charIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon changeCharIcon = new ImageIcon(scaledImage);

            JLabel charLabel = new JLabel(changeCharIcon); //사용자가 선택한 캐릭터
            charLabel.setOpaque(true);
            charLabel.setBackground(Color.white);
            charLabel.setBounds(i*210+100,150,charLabel.getIcon().getIconWidth(),charLabel.getIcon().getIconHeight());
            add(charLabel);

            isReadyLabel = new JLabel("Ready"); //ready 여부를 나타내는 라벨
            readyVector.add(isReadyLabel);

            if(name.equals(myName)) {
                myIsReadyLabel = isReadyLabel;
                myNum = i;
            }
            isReadyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            isReadyLabel.setVerticalAlignment(SwingConstants.CENTER);

            isReadyLabel.setFont(new Font("Arial", Font.BOLD, 35));
            isReadyLabel.setOpaque(true); //배경색이 칠해지도록 허락함
            isReadyLabel.setBounds(i*210+80,305,180,60);
            isReadyLabel.setBackground(Color.gray);
            isReadyLabel.setForeground(Color.lightGray);

            add(isReadyLabel);

        }
    }
    class ReceiveMsg extends Thread{ //서버로부터 온 메시지를 수신한다.
        JLabel clientLabel;
        @Override
        public void run(){
            while(true) {
                try {
                    String msg = dis.readUTF(); //msg를 가져옴
                    System.out.println("msg 들어옴 => "+msg);
                    msg = msg.trim(); //trim 메소드를 사용하여 앞 뒤의 공백을 제거
                    String stArray[] = msg.split(" ");

                    switch (stArray[0]) {
                        case "/addUser": //추가 클라이언트 메시지를 받았을 경우
                            System.out.println("adduser로 메시지 받음");

                            if(!stArray[1].equals(myName)) {
                                System.out.println("if문으로 들어옴");
                                clientInfos.add(new ClientInfo(stArray[1], 0, Integer.parseInt(stArray[2])));
                                AttendClientsPanel.this.removeAll();
                                AttendClientsPanel.this.drawClientInfos(clientInfos);
                                AttendClientsPanel.this.repaint();
                            }
                            break;
//                        case "/readyOn":
//                            //해당 인덱스를 가지고 readyVector에 해당 인덱스의 레이블의 색상을 변경한다.
//                            clientLabel = readyVector.get(Integer.parseInt(stArray[1]));
//                            setReadyLabelColor(clientLabel, Color.orange, Color.black);
//                            break;
//                        case "/readyOff":
//                            clientLabel = readyVector.get(Integer.parseInt(stArray[1]));
//                            setReadyLabelColor(clientLabel, Color.gray, Color.lightGray);
//                        case "/addUser":
//                            System.out.println("클라이언트에게 addUser 들어옴");
//                            String name = stArray[1];
//                            int charIndex = Integer.parseInt(stArray[2]);
////                            addUser(name,charIndex);
//
//                            break;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
