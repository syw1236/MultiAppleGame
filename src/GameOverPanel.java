import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.Vector;

public class GameOverPanel extends JPanel {
    Vector<ClientInfo> clientInfos;
    public GameOverPanel(Vector<ClientInfo> clientInfos){
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

        drawRank();
      //  setSize(50)
    }
    public void drawRank(){ //순위를 표시하는 함수
        for(int i=0;i<clientInfos.size();i++){
            ClientInfo clientInfo = clientInfos.get(i);
            JLabel rankNumLabel = new JLabel();
            String rankSt = i+1+"위";
            rankNumLabel.setText(rankSt);
            rankNumLabel.setFont(new Font("Arial", Font.BOLD, 30));
            rankNumLabel.setBounds(25,i*10+100,50,30);
            add(rankNumLabel);

            String name = clientInfo.getName();
            JLabel nameLabel = new JLabel();
            nameLabel.setText(name);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 25));
            nameLabel.setBounds(130,i*10+100,100,30);
            add(nameLabel);

            int score = clientInfo.getScore();
            JLabel scoreLabel = new JLabel(String.valueOf(score));
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 25));
            scoreLabel.setBounds(200,i*10+100,100,30);
            add(scoreLabel);




        }
    }


}
