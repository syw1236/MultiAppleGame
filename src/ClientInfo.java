import javax.swing.*;
import java.io.Serializable;

public class ClientInfo implements Serializable,Comparable<ClientInfo> {
    String name; //이름
    int score; //점수
    int charIndex; //선택한 캐릭터 인덱스
    boolean isReady = false;
    JLabel scoreLabel;
    public ClientInfo(String name,int score, int charIndex){
        this.name = name;
        this.score = score;
        this.charIndex = charIndex;
    }
    public void setScoreLabel(JLabel scoreLabel){
        this.scoreLabel = scoreLabel;
    }
    public JLabel getScoreLabel(){
        return this.scoreLabel;
    }
    public void setIsReady(boolean isReady){
        this.isReady = isReady;
    }
    public boolean getIsReady(){ //클라이언트의 준비 여부를 반환함
        return this.isReady;
    }
    public int getCharIndex(){
        return this.charIndex;
    }
    public int getScore(){
        return this.score;
    }
    public void setScore(int score){
        this.score = score;
    }
    public String getName(){
        return this.name;
    }

    @Override
    public int compareTo(ClientInfo other) {
        // 점수를 기준으로 오름차순 정렬
        return Integer.compare(this.score, other.score);
    }
}
