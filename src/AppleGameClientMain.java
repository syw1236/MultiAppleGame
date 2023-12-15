import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;


public class AppleGameClientMain extends JFrame { //클라이언트이 처음 시작 화면이 되는 것
    JLabel nameLa; //"이름"으로 표시되는 라벨
    String name; //클라이언트가 입력한 이름
    JTextField inputName; //이름 입력하는 곳
    JLabel clickIcon; //아이콘을 클릭하라는 라벨
    Vector<ImageIcon> icons = new Vector<>(); //아이콘들 모음 벡터
    JButton gameStartBtn; //게임 참가 버튼

    int charIndex = -1; //선택한 캐릭터 인덱스

    public AppleGameClientMain(){

        setLayout(null);

        ImageIcon redAppleIcon = new ImageIcon("image/redApple.png");
        ImageIcon yellowAppleIcon = new ImageIcon("image/yellowApple.png");
        ImageIcon greenAppleIcon = new ImageIcon("image/greenApple.png");
        ImageIcon blueAppleIcon = new ImageIcon("image/blueApple.png");
        ImageIcon starIcon= new ImageIcon("image/star.png");

        icons.add(redAppleIcon);
        icons.add(yellowAppleIcon);
        icons.add(greenAppleIcon);
        icons.add(blueAppleIcon);

        JLabel star1 = new JLabel(starIcon);
        star1.setBounds(395,100,star1.getIcon().getIconWidth(),star1.getIcon().getIconHeight());
        add(star1);

        JLabel star2 = new JLabel(starIcon);
        star2.setBounds(620,100,star1.getIcon().getIconWidth(),star1.getIcon().getIconHeight());
        add(star2);

        JLabel title = new JLabel("사과톡톡");
        title.setFont(new Font("Arial", Font.BOLD, 55));
        title.setBounds(425,100,200,55);
        add(title);

        JLabel titleShadow = new JLabel("사과톡톡");
        titleShadow.setFont(new Font("Arial", Font.BOLD, 55));
        titleShadow.setForeground(Color.gray);
        titleShadow.setBounds(428,100,200,55);
        add(titleShadow);

        nameLa = new JLabel("이름"); //"이름"으로 적히는 부분
        nameLa.setFont(new Font("Arial", Font.BOLD, 30));
        nameLa.setBounds(350,200,100,50);

        inputName = new JTextField();
        inputName.setFont(new Font("Arial",Font.PLAIN,20));
        inputName.setBounds(450,200,200,50);


        clickIcon = new JLabel("캐릭터를 선택해주세요.");
        clickIcon.setFont(new Font("Arial",Font.BOLD,25));
        clickIcon.setBounds(350,290,300,50);

        for(int i=0;i<icons.size();i++){ //사과 아이콘 버튼을 생성시키고 부착
            int index = i;
            ImageIcon icon = icons.get(i);
            JButton iconBtn = new JButton(icon);
            iconBtn.setBounds(i*100+350,350,icon.getIconWidth(),icon.getIconHeight()+10);
            iconBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    charIndex = index; //클릭한 아이콘에 대해 인덱스를 저장
                    iconBtn.setBackground(new Color(200, 200, 200));
                    iconBtn.setEnabled(true);
                }
            });
            add(iconBtn);
        }

        gameStartBtn = new JButton("게임 참가");
        gameStartBtn.setFont(new Font("Arial", Font.BOLD, 30));
        gameStartBtn.setBounds(400,450,200,50);
        gameStartBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { //게임 참가 버튼을 클릭할 경우
                //게임 시작 버튼을 누르면 해당 frame은 사라지고 게임 준비 frame이 생성됨과 동시에 서버와 연결이 된다.

                if(inputName.getText().equals("") ||charIndex == -1) {
                   JOptionPane.showMessageDialog(AppleGameClientMain.this, "이름이나 캐릭터를 선택하지 않았습니다.", "Message",JOptionPane.ERROR_MESSAGE );
                   gameStartBtn.setEnabled(true); //게임 참가 버튼을 다시 활성화 시킴
                }
                else if(inputName.getText().length()>5){
                    JOptionPane.showMessageDialog(AppleGameClientMain.this, "이름을 5자리 이하로 설정해주세요", "Message",JOptionPane.ERROR_MESSAGE );

                }
                else {
                    name = inputName.getText();
                    AppleGameClient appleGameClient = new AppleGameClient(name,charIndex,icons);
                    AppleGameClientMain.this.dispose(); //창을 꺼버림

                }




            }

        });


        add(nameLa); //"이름"
        add(inputName); //이름 입력 칸
        add(clickIcon); //"캐릭터를 선택해주세요."
        add(gameStartBtn); //게임 참가 버튼

        setSize(1000,700);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public static void main(String[] args) {
        new AppleGameClientMain();
    }

}
