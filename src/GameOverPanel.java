import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.Vector;

public class GameOverPanel extends JPanel {
    Vector<ClientInfo> clientInfos;
    AppleGameClient appleGameClient;
    ReadyPanel readyPanel;
    public GameOverPanel(AppleGameClient appleGameClient,Vector<ClientInfo> clientInfos){
        setLayout(null);

        this.clientInfos = clientInfos;
        Collections.sort(clientInfos); //클라이언트 정보를 점수 기준으로 정렬함

        JLabel rankLabel = new JLabel("순위");
        rankLabel.setOpaque(true);
        rankLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rankLabel.setVerticalAlignment(SwingConstants.CENTER);
        rankLabel.setBackground(Color.black);
        rankLabel.setForeground(Color.orange);
        rankLabel.setFont(new Font("Arial", Font.BOLD, 35));
        rankLabel.setBounds(0,0,500,50);
        add(rankLabel);

        drawRank(); //순위를 나타내는 함수 호출

        //처음 화면으로 돌아가는 버튼 구성
        ImageIcon homeIcon = new ImageIcon("image/home.png");
        JButton homeBtn = new JButton(homeIcon);
        homeBtn.setBounds(400,385,homeBtn.getIcon().getIconWidth(),homeBtn.getIcon().getIconHeight());
        homeBtn.setBorder(new EmptyBorder(0, 0, 0, 0)); // 빈 Border로 설정
        homeBtn.addActionListener(new ActionListener() { //집으로 돌아가는 버튼을 누를 시에
            @Override
            public void actionPerformed(ActionEvent e) {
                //클라이언트의 정보를 초기화함
                //대기화면으로 돌아감
                //클라이언트들의 점수 & ready 여부를 초기화 시켜야함
                resetClientInfo(); //클라이언트 정보를 초기화시킴

                Window window = SwingUtilities.getWindowAncestor(GameOverPanel.this);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose(); //다이얼로그를 없앰
                }

                try {
                    appleGameClient.makeReadyScreen(clientInfos); //대기 화면으로 이동
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                appleGameClient.repaint();
            }
        });
        add(homeBtn);

        JLabel homeDes = new JLabel("대기화면으로 돌아가기");
        homeDes.setForeground(Color.gray);
        homeDes.setBounds(380,440,150,30);
        add(homeDes);



      //  setSize(50)
    }
    public void resetClientInfo(){ //클라이언트 정보를 초기화시킴
        for(ClientInfo clientInfo : clientInfos){
            clientInfo.setScore(0);
            clientInfo.setIsReady(false);
        }
    }
    public Vector<ClientInfo> getResetClientInfos(){
        return this.clientInfos;
    }
    public void setReadyPanel(ReadyPanel readyPanel){
        this.readyPanel = readyPanel;
    }
//    public void goToReadyScreen(ReadyPanel readyPanel){
//        appleGameClient.setContentPane(readyPanel);
//    }
    public void drawRank(){ //순위를 표시하는 함수
        for(int i=0;i<clientInfos.size();i++){
            ClientInfo clientInfo = clientInfos.get(i);
            JLabel rankNumLabel = new JLabel();
            String rankSt = i+1+"위";
            rankNumLabel.setText(rankSt);
            rankNumLabel.setFont(new Font("Arial", Font.BOLD, 30));
            rankNumLabel.setBounds(160,i*10+98,50,30);
            add(rankNumLabel);

            String name = clientInfo.getName();
            JLabel nameLabel = new JLabel();
            nameLabel.setText(name);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 25));
            nameLabel.setBounds(230,i*10+100,100,30);
            add(nameLabel);

            int score = clientInfo.getScore();
            JLabel scoreLabel = new JLabel(String.valueOf(score));
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 25));
            scoreLabel.setBounds(330,i*10+100,100,30);
            add(scoreLabel);

        }
    }




}
