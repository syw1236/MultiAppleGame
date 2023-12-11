import javax.swing.*;
import java.awt.*;
import java.lang.invoke.VarHandle;
import java.util.Vector;

public class AppleGameClientInfoPanel extends JPanel {
    Vector<ClientInfo> clientInfos;
    Vector<ImageIcon> icons;
    volatile Vector<JLabel> clientInfoLabels = new Vector<>();
    public AppleGameClientInfoPanel(Vector<ClientInfo> clientInfos, Vector<ImageIcon> icons){
        setLayout(null);
        this.clientInfos = clientInfos;
        this.icons = icons;
        drawClientInfo(clientInfos); //클라이언트들의 현재 게임 정보를 담은 화면을 그리는 함수 호출
    }
    public void drawClientInfo(Vector<ClientInfo> clientInfos){ //클라이언트들의 현재 게임 정보를 담은 화면을 그리는 함수
        for(int i = 0;i<clientInfos.size();i++){
            ClientInfo clientInfo = clientInfos.get(i);
            ImageIcon charIcon = icons.get(clientInfo.getCharIndex());

            int width = 50;  // 원하는 너비
            int height = 50; // 원하는 높이
            Image scaledImage = charIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon changeCharIcon = new ImageIcon(scaledImage);

            JLabel charLabel = new JLabel(changeCharIcon);
            clientInfoLabels.add(charLabel);
            charLabel.setBounds(10,i*55+10,charLabel.getIcon().getIconWidth(),charLabel.getIcon().getIconHeight());
            add(charLabel);

            String name = clientInfo.getName();
            JLabel nameLabel = new JLabel(name);
            clientInfoLabels.add(nameLabel);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
            nameLabel.setBounds(100,i*55+25,150,30);
            add(nameLabel);

            int score = clientInfo.getScore();
            JLabel scoreLabel = new JLabel(Integer.toString(score));
            clientInfo.setScoreLabel(scoreLabel);
            //clientInfoLabels.add(scoreLabel);
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
            scoreLabel.setBounds(200,i*55+25,200,30);
            add(scoreLabel);

        }
    }
    public void setClientInfoScore(String name,int score){
        for(ClientInfo clientInfo:clientInfos){
            if(clientInfo.getName().equals(name)) {
                clientInfo.setScore(score); //클라이언트 정보 업데이트
                JLabel scoreLabel = clientInfo.getScoreLabel();
                scoreLabel.setText(Integer.toString(clientInfo.getScore())); //레이블 숫자 변경
            }
        }
    }
    public Vector<ClientInfo> getClientInfos(){
        return this.clientInfos;
    }
}
