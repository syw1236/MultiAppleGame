import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.xml.crypto.Data;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Vector;

public class AppleGameServer {
    private ObjectOutputStream oos; //클라이언트에게 객체 전송(클라이언트들의 정보)
    private ObjectInputStream ois;
    private InputStream is;
    private OutputStream out;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private Vector<ClientService> clientServices = new Vector<>(); //클라이언트와의 연결 정보를 가지고 있는 벡터
    private Vector<ClientService> removeClientServieces = new Vector<>(); //제거할 클라이언트와의 연결 정보를 가지고 있는 벡터

    volatile private Vector<ClientInfo> clientInfos = new Vector<>(); //클라이언트스 정보 벡터
    private boolean allReady = false;

    private Clip countClip;
    private File countFile;
    private AudioInputStream countStream;
    private String countPath = "audio/count.wav";
    public AppleGameServer(){
        try{
            serverSocket = new ServerSocket(9999); //서버 소켓 생성
            AcceptServer acceptServer = new AcceptServer(); //클라이언트를 기다리는 스레드 생성
            acceptServer.start(); //스레드 시작
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            countClip = AudioSystem.getClip();
            countFile = new File(countPath);
            countStream = AudioSystem.getAudioInputStream(countFile);
            countClip.open(countStream);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try{
                    AppleGameServer appleGameServer = new AppleGameServer(); //서버 생성
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    class AcceptServer extends Thread{ //새로운 참가자를 accept()하고 userThread를 생성함, 한 번 만들어서 계속 사용하는 스레드

        @Override
        public void run(){
            while(true){ //사용자 접속을 계속 받기 위해서 While문 사용함
                try{
                    System.out.println("clientSerivece size => "+clientServices.size());
                    for(ClientService clientService:clientServices){
                        System.out.println("client-> "+clientService);
                    }
                   System.out.println("Waiting gamer...");
                    clientSocket = serverSocket.accept(); //클라이언트를 기다림
                    System.out.println("clientService의 사이즈 => "+clientServices.size());
                    System.out.println(clientSocket); //클라이언트 소켓 출력
                    out = clientSocket.getOutputStream();
                    is = clientSocket.getInputStream();
                    dis = new DataInputStream(is); //받는 스트림
                    dos = new DataOutputStream(out); //보내는 스트림

                    String name = dis.readUTF(); //클라이언트의 이름 받아옴
                    int charIndex = Integer.parseInt(dis.readUTF()); //클라이언트의 캐릭터 인덱스를 받아옴
                    System.out.println("client name " + name);
                    System.out.println("charIndex " + charIndex);

                    ClientService clientService = new ClientService(name, clientSocket); //클라이언트-서버 통신 스레드 생성

                    if(clientServices.size() == 4){ //채워져야 할 숫자까지 채워졌다면
                        removeClientServieces.add(clientService);
                        clientService.start();

                        clientService.writeObjectOne(null);

                        dos.writeUTF("/fullUser "+"현재 4명의 플레이어가 게임을 진행하고 있습니다. 다음 기회에 참여해주세요.");
                    }
                    else{
                        clientServices.add(clientService); //클라이언트 연결 스레드 벡터에 추가
                        clientService.start(); //스레드 시작

                        clientInfos.add(new ClientInfo(name, 0, charIndex));//클라이언트 정보를 통해 객체를 생성하고 배열에 넣음

                        clientService.writeObjectOne(clientInfos); //새로운 클라이언트에게 현재까지 있는 클라이언트들이 정보를 전달함

                        System.out.println("서버에서 adduer 보내기 전 clientSerive의 사이즈 = " + clientServices.size());
                        String addUsermsg = "/addUser " + name + " " + 0 + " " + charIndex;
                        writeStAll(addUsermsg); //모든 클라이언트에게 새로 들어온 클라이언트 정보를 전달함
                        String chatmsg = "/readyChat " + name + "님이 들어오셨습니다.";
                        writeStAll(chatmsg);
                    }

                }catch (Exception e){
                    try {
                        dos.close();
                        dis.close();
                    }catch (Exception err){
                        err.printStackTrace();
                    }
                }
            }
        }

    }


    class ClientService extends Thread{
        private Socket clientSocket;
        private InputStream is;
        private OutputStream out;
        private DataInputStream dis;
        private DataOutputStream dos;
        private ObjectOutputStream oos;
        private String name;
        private boolean stop = false;
        private volatile int count;
        private volatile int startCount;

        public ClientService(String name,Socket clientSocket){
            this.name = name;
            this.clientSocket = clientSocket;
            try {
                is = clientSocket.getInputStream();
                out = clientSocket.getOutputStream();
                dis = new DataInputStream(is);
                dos = new DataOutputStream(out);
                oos = new ObjectOutputStream(out);


            }catch (Exception e){
                e.printStackTrace();
            }
        }
        @Override
        public void run(){
            while(!stop) {
                try {
                    String msg = dis.readUTF(); //msg를 가져옴

                    msg = msg.trim(); //trim 메소드를 사용하여 앞 뒤의 공백을 제거
                    String stArray[] = msg.split(" ");
                    if(msg.startsWith("/readyChat")){ //대기시간에 나눈 채팅
                        writeStAll(msg + "\n"); //받은 msg를 모두에게 보냄
                    }
                    else if(msg.startsWith("/chat")) //게임 도중에 나눈 채팅
                        writeStAll(msg + "\n"); //받은 msg를 모두에게 보냄

                    else if(msg.startsWith("/score")){ //업데이트된 점수에 대한 이름과 점수를 받음
                        System.out.println("/score  메시지 받음 => "+msg);
                        writeStAll(msg+"\n");
                    }
                    else if(msg.startsWith("/readyOn")){
                        System.out.println("readyOn 메시지 받음");
                        count = 0;
                        for(ClientInfo clientInfo:clientInfos){
                            System.out.println("서버에서 clientInfo의 ready 여부 => "+clientInfo.getIsReady());
                        }
                        //서버쪽에서 클라이언트 정보를 변경해야함
                        for(ClientInfo clientInfo : clientInfos){ //서버쪽 정보 벡터 변경
                            if(clientInfo.getName().equals(stArray[1])){
                                clientInfo.setIsReady(true);
                            }
                        }
                        for(ClientInfo clientInfo:clientInfos){
                            if(clientInfo.getIsReady()) {
                                count++;
                            }
                        }
                        System.out.println("서버쪽에서의 count = > "+count);
                        writeStAll(msg+"\n"); //클라이언트 쪽에서 메시지를 받으면 클라이언트 쪽의 ready 여부가 변경된다.
                        sleep(100);

                        if(count == 4){ //만약 모두가 ready라면 /allReady 라는 메시지를 보낸다. ///
                            startCount = 5; //startCount 뒤에 게임이 시작되는 것임!
                            countClip.start(); //카운트다운 오디오 시작
                            countClip.setFramePosition(0); //프레임 초기화 시킴
                            while(true){
                                System.out.println("startCount = "+startCount);

                                if(startCount==-1)
                                    break;
                                writeStAll("/count "+startCount);
                                startCount--; //count가 줄어드는 걸 화면에 나타내도록 하기!!!
                                sleep(1000);

                            }
                            writeStAll("/count "+"게임시작");

                            writeStAll("/allReady"); //게임 시작

                            for(ClientInfo clientInfo:clientInfos){ //다시 모두 게임 레디 초기화를 시킴
                                clientInfo.setIsReady(false);
                            }
                        }
                    }
                    else if(msg.startsWith("/readyOff")){
                        for(ClientInfo clientInfo:clientInfos){
                            if(clientInfo.getName().equals(stArray[1]))
                                clientInfo.setIsReady(false);
                        }
                        writeStAll(msg+"\n");
                    }

                    else if(msg.startsWith("/substractUser")){ //사용자가 나갔다는 메시지를 받았을 경우
                        writeStAll(msg);
                        //클라이언트 정보 벡터에서 해당 정보 삭제
                        ClientInfo removeClientInfo = null;
                        for(ClientInfo clientInfo:clientInfos){
                            String clientName = clientInfo.getName();
                            if(clientName.equals(stArray[1])){
                                removeClientInfo = clientInfo;
                                break;
                            }
                        }
                        clientInfos.remove(removeClientInfo); //클라이언트 정보 벡터에서 삭제
                        if(name.equals(stArray[1])) {
                            clientServices.remove(this);
                            break;
                        }
                    }
                    else if(msg.startsWith("/fullUserOut")){
                        removeClientServieces.clear();
                    }

                } catch (Exception e) {
                    try{
                        dos.close();
                        dis.close();
                    }catch (Exception err){
                        err.printStackTrace();
                    }
                }

            }
        }
        public void writeObjectOne(Vector<ClientInfo> clientInfos){ //클라이언트에게 객체를 보내는 함수
            try{
                oos.writeObject(clientInfos);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public DataOutputStream getDos(){
            return dos;
        }

    }

    public void writeStAll(String msg){
        for(ClientService clientService : clientServices){
            DataOutputStream dos = clientService.getDos();
            if(dos != null){
                try {

                    dos.writeUTF(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
