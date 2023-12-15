import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

public class AppleGamePanel extends JPanel {
//    int second = 120;
    int second = 90;
//    int timerWidth = 360;
    int timerWidth = 270;
    int timerHeight = 30;
    JLabel timer;
    Random random = new Random();
    Vector<ImageIcon> appleVector = new Vector<>(); //사과 아이콘들을 담은 벡터
    Vector<Apple> appleLabelVector = new Vector<>(); //사과 레이블들을 담은 벡터

    ImageIcon appleIcon; //사용자가 선택한 사과 아이콘

    private Point startPoint;
    private Point endPoint;
    DrawPanel drawPanel;
    Socket clientSocket;
    String name; //해당 클라이언트의 이름
    InputStream is;
    OutputStream out;
    DataInputStream dis;
    DataOutputStream dos;
    int score = 0;
    JLabel scoreMark; //화면 상에 점수를 나타내는 레이블
    AppleGameClient appleGameClient;
    volatile Clip audioClip; //사과 브금 오디오 클립
    volatile Clip itemClip; //사과 아이템 브금 오디오 클립
    volatile File audioFile; //게임 브금 파일
    volatile File itemFile; //사과 아이템 브금 파일
    String itemPath = "audio/item.wav";
    String audioPath = "audio/apple.wav";
    AudioThread audioThread;
//    AudioThread itemThread;



    DragActionListener dragActionListener = new DragActionListener();
    public AppleGamePanel(String name,Socket clientSocket,ImageIcon appleIcon,AppleGameClient appleGameClient) throws UnsupportedAudioFileException, IOException {
        setLayout(null);


        this.appleGameClient = appleGameClient;
        this.name = name;
        this.clientSocket = clientSocket;
        this.appleIcon = appleIcon;

        try {
            is = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
            dos = new DataOutputStream(out);
            dis = new DataInputStream(is);
        }catch (Exception e){
            e.printStackTrace();
        }

//        audioFile = new File("audio/apple.wav");
//        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);

        JLabel timerLabel = new JLabel("Timer");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 23));
        //timerLabel.setBounds(10,10,AppleGamePanel.this.getWidth(),AppleGamePanel.this.getHeight());
        timerLabel.setBounds(10,10,100,30);
        add(timerLabel);

        timer = new JLabel();
        timer.setBounds(110,10,timerWidth,timerHeight);
        timer.setOpaque(true);
        timer.setBackground(Color.red);
        add(timer);

        TimerThread timerThread = new TimerThread(timer); //타이머 스레드 시작
        timerThread.start();

        JLabel scoreLabel = new JLabel("Score");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 23));
        scoreLabel.setBounds(455,10,100,30);
        add(scoreLabel);

        scoreMark = new JLabel();
        String scoreNum = String.valueOf(score);
        scoreMark.setText(scoreNum);
        scoreMark.setFont(new Font("Arial", Font.PLAIN, 20));
        scoreMark.setBounds(550,10,300,30);
        add(scoreMark);


        makeApple(); //게임을 위한 사과를 생성함
        putApple(); //생성한 사과들을 게임판에 나열함
        drawPanel = new DrawPanel();
        drawPanel.setBounds(0, 0, 700, 700); // AppleGamePanel의 크기에 맞춤
        add(drawPanel,0);


        addMouseListener(dragActionListener);
        addMouseMotionListener(dragActionListener);
        try {

            itemClip = AudioSystem.getClip(); //비어있는 아이템 브금 오디오 클립 만들기
            itemFile = new File(itemPath); //아이템 오디오 파일의 경로명
            AudioInputStream itemStream = AudioSystem.getAudioInputStream(itemFile); //아이템 오디오 파일로부터
            itemClip.open(itemStream); //재생할 아이템 오디오 스트림 열기

        }catch (Exception e){
            e.printStackTrace();
        }

        audioThread = new AudioThread();
        audioThread.start();// 노래 스레드 시작





    }
    public void makeApple(){ //게임을 위한 사과들을 생성하는 함수
        ImageIcon apple1 = new ImageIcon("image/1.png");
        ImageIcon apple2 = new ImageIcon("image/2.png");
        ImageIcon apple3 = new ImageIcon("image/3.png");
        ImageIcon apple4 = new ImageIcon("image/4.png");
        ImageIcon apple5 = new ImageIcon("image/5.png");
        ImageIcon apple6 = new ImageIcon("image/6.png");
        ImageIcon apple7 = new ImageIcon("image/7.png");
        ImageIcon apple8 = new ImageIcon("image/8.png");
        ImageIcon apple9 = new ImageIcon("image/9.png");

        appleVector.add(apple1);
        appleVector.add(apple2);
        appleVector.add(apple3);
        appleVector.add(apple4);
        appleVector.add(apple5);
        appleVector.add(apple6);
        appleVector.add(apple7);
        appleVector.add(apple8);
        appleVector.add(apple9);
    }

    public void putApple(){ //사과들을 게임판에 나열하는 함수
        int width = 50;
        int height = 50;


        for(int i=0;i<13;i++){
            for(int j=0;j<10;j++){
                int randomNum = random.nextInt(9);
                ImageIcon appleIcon = appleVector.get(randomNum);

                Image scaledImage = appleIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                ImageIcon changeAppleIcon = new ImageIcon(scaledImage);

                Apple appleLabel = new Apple();
                appleLabel.setIcon(changeAppleIcon);
                appleLabel.setInt(randomNum+1);
                appleLabel.setBounds(i*50+20,j*60+60,appleLabel.getIcon().getIconWidth(),appleLabel.getIcon().getIconHeight());
                appleLabelVector.add(appleLabel);
                add(appleLabel);
            }
        }
    }

    class DragActionListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            startPoint = e.getPoint();
            endPoint = startPoint; // 초기에는 시작점과 끝점이 같음


//            drawPanel.setDragging(true);
            SwingUtilities.convertPointToScreen(startPoint, AppleGamePanel.this);
            SwingUtilities.convertPointFromScreen(startPoint, drawPanel);

            drawPanel.setStartP(startPoint);
            drawPanel.setEndP(startPoint);



//            drawPanel.setStartP(startPoint);

        }

        @Override
        public void mouseDragged(MouseEvent e){
            endPoint = e.getPoint();

            SwingUtilities.convertPointToScreen(endPoint, AppleGamePanel.this);
            SwingUtilities.convertPointFromScreen(endPoint, drawPanel);

            drawPanel.setEndP(endPoint);

            repaint(); // 사각형을 그림
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            endPoint = e.getPoint();

            SwingUtilities.convertPointToScreen(endPoint, AppleGamePanel.this);
            SwingUtilities.convertPointFromScreen(endPoint, drawPanel);

            drawPanel.setEndP(endPoint);
            repaint(); // 사각형을 그림

            if (startPoint != null && endPoint != null) {
                //그려지는 위치 안에 사과가 몇개인지 먼저 체크하자!
                int x = Math.min(startPoint.x, endPoint.x);
                int y = Math.min(startPoint.y, endPoint.y);
                int width = Math.abs(endPoint.x - startPoint.x);
                int height = Math.abs(endPoint.y - startPoint.y);

                //int applesInHalfArea = 0; // 드래그 영역 내에 1/2 영역이 포함된 사과 개수 카운트
                int count = 0;
                Vector<Apple> removeAppleVector = new Vector<>();//삭제할 사과들을 모아놓는 벡터

                for (Apple apppleLabel : appleLabelVector) {
                    int appleX = apppleLabel.getX();
                    int appleY = apppleLabel.getY();
                    int appleWidth = apppleLabel.getWidth();
                    int appleHeight = apppleLabel.getHeight();

                    // startPoint와 endPoint 사이에 드래그한 영역이 있는지 확인
                    if (x + width >= appleX && x <= appleX + appleWidth &&
                            y + height >= appleY && y <= appleY + appleHeight) {

                        // 드래그한 영역과 사과의 교집합 영역 계산
                        int intersectionX = Math.max(x, appleX);
                        int intersectionY = Math.max(y, appleY);
                        int intersectionWidth = Math.min(x + width, appleX + appleWidth) - intersectionX;
                        int intersectionHeight = Math.min(y + height, appleY + appleHeight) - intersectionY;

                        // 교집합 영역이 드래그 영역의 1/2 이상인지 확인
                        double intersectionArea = intersectionWidth * intersectionHeight;
                        double appleArea = appleWidth * appleHeight;

                        if (intersectionArea >= 0.5 * appleArea) {
                            // 교집합 영역이 드래그 영역의 1/2 이상인 경우
//                        applesInHalfArea++;
                            int appleCount = apppleLabel.getInt();
                            count += appleCount;
                            System.out.println("appleCount = " + appleCount);
                            System.out.println("count = " + count);
                            removeAppleVector.add(apppleLabel); //삭제할 벡터에 해당 사과 추가

                            // 여기에서 필요한 작업 수행 가능 (예: 해당 사과를 삭제하거나 특정 동작 수행)
                            // appleLabelVector.remove(apppleLabel);
                            // repaint();
                        }
                    }
                }


                if (count == 10) { //드래그 영역 내의 숫자가 10일 경우
                    itemClip.setFramePosition(0); //재생 위치를 첫 프레임으로 변경
                    itemClip.start(); //아이템 제거 오디오 재생하기
                    for (Apple removeApple : removeAppleVector) {
                        AppleGamePanel.this.remove(removeApple);
                        AppleGamePanel.this.repaint();
                        appleLabelVector.remove(removeApple);
                    }


                    System.out.println("모든 숫자의 합은 10이다.");
                    score += removeAppleVector.size();
                    scoreMark.setText(String.valueOf(score));
                    try {
                        dos.writeUTF("/score " + name + " " + score); //서버에게 업데이트된 점수를 보냄
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                } else {
                    removeAppleVector.clear();//10이 되지 않으므로 해당 벡터 초기화
                    System.out.println("모든 숫자의 합은 10이 아니다.");
                }

                endPoint = startPoint;
                drawPanel.setStartP(startPoint);
                drawPanel.setEndP(endPoint);
                AppleGamePanel.this.repaint();
                //System.out.println("드래그한 영역 사과 개수 = "+applesInHalfArea);
            }
        }

    }

    class AudioThread extends Thread{
        private volatile boolean stop = false; //멈추는 stop 변수
        public AudioThread(){
            try {
                audioClip = AudioSystem.getClip(); //비어있는 사과 브금 오디오 클립 만들기
                audioFile = new File(audioPath); //오디오 파일의 경로명
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile); //오디오 파일로부터
                audioClip.open(audioStream); //재생할 오디오 스트림 열기
            }catch (Exception e){
                e.printStackTrace();
            }

        }


        public synchronized void setStop(boolean stop){
            this.stop = stop;
        }
        @Override
        public void run(){
            while(!stop){ //stop이 false이면 작동 중지
//                if(stop)
//                    break;
                //System.out.println(name+" audio thread starting");
//                audioClip.start(); //노래 시작
//                System.out.println("audioClip 길이 -> "+audioClip.getFrameLength());
//                System.out.println("현재 audioClip 포지션 => "+audioClip.getFramePosition());
////
////                if (audioClip.getFramePosition() == audioClip.getFrameLength()) {
////                    System.out.println("노래가 끝까지 갔습니다. 다시 시작합니다.");
////                    audioClip.setFramePosition(0); // 재생 위치를 첫 프레임으로 변경
////                }
//                if(!audioClip.isRunning()){
//                    audioClip.setFramePosition(0);
//                }
//                if(audioClip.getFramePosition()==audioClip.getFrameLength())
//                    System.out.println("audio 프레임 끝까지 옴");
//                    audioClip.setFramePosition(0);

                audioClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            audioClip.stop();
            System.out.println("끝남");
            //clip.setFramePosition(0);//재생 위치를 첫 프레임으로 변경
        }
    }

    class TimerThread extends Thread{
        JLabel timer;
        public TimerThread(JLabel timer){
            this.timer = timer;
        }
        @Override
        public void run(){
            while(true){
                if(second > 0){
                    second--;
                    timerWidth-=3;
//                    timerWidth-=1;
                    System.out.println("남은 시간 => "+second);
                    System.out.println("남은 너비 => "+timerWidth);

                    timer.setBounds(90,10,timerWidth,timerHeight);
                    AppleGamePanel.this.repaint();
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {//timer 완료
                    break;
                }
            }
            //게임이 끝났을 떄 수행
            System.out.println("goToReady true로 변경함");
            //clip.stop();
            audioThread.setStop(true); //스레드의 stop 변수를 true로 변경하여 스레드를 중지시킴
            //appleGameClient.setGoToReady(true);
            appleGameClient.gameOverScreen();
            //clip.stop();
            //clip.stop();


        }
    }


}
