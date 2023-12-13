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

    volatile private Vector<ClientInfo> clientInfos = new Vector<>(); //클라이언트스 정보 벡터
    private boolean allReady = false;
    public AppleGameServer(){
        try{
            serverSocket = new ServerSocket(9999); //서버 소켓 생성
            AcceptServer acceptServer = new AcceptServer(); //클라이언트를 기다리는 스레드 생성
            acceptServer.start(); //스레드 시작
//            CheckReady checkReady = new CheckReady();
//            checkReady.start();

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
                   System.out.println("Waiting gamer...");
                    clientSocket = serverSocket.accept(); //클라이언트를 기다림
                    System.out.println(clientSocket); //클라이언트 소켓 출력
                    out = clientSocket.getOutputStream();
                    is = clientSocket.getInputStream();

                    dis = new DataInputStream(is); //받는 스트림
                    dos = new DataOutputStream(out); //보내는 스트림
//                    oos = new ObjectOutputStream(out); //클라이언트의 outStream
//                    ois = new ObjectInputStream(is);

                    String name = dis.readUTF(); //클라이언트의 이름 받아옴
                    int charIndex = Integer.parseInt(dis.readUTF()); //클라이언트의 캐릭터 인덱스를 받아옴

                    System.out.println("client name "+name );
                    System.out.println("charIndex " + charIndex);

                    clientInfos.add(new ClientInfo(name,0,charIndex));//클라이언트 정보를 통해 객체를 생성하고 배열에 넣음

                    ClientService clientService = new ClientService(name,clientSocket); //클라이언트-서버 통신 스레드 생성
                    clientServices.add(clientService); //클라이언트 연결 스레드 벡터에 추가
                    clientService.start(); //스레드 시작



                    //oos = new ObjectOutputStream(out);
                    clientService.writeObjectOne(clientInfos); //새로운 클라이언트에게 현재까지 있는 클라이언트들이 정보를 전달함

                    System.out.println("서버에서 adduer 보내기 전 clientSerive의 사이즈 = "+clientServices.size());
                    String addUsermsg = "/addUser "+name+" "+0+" "+charIndex;
                    writeStAll(addUsermsg); //모든 클라이언트에게 새로 들어온 클라이언트 정보를 전달함
                    String chatmsg = "/readyChat "+name+"님이 들어오셨습니다.";
                    writeStAll(chatmsg);


//                    writeObjectAll(clientInfos);
//                    clientService.writeObjectOne(clientInfos);

                    //                    for(ClientService client : clientServices){
//                        String msg = "/adduser "+name+" "+charIndex;
//                        client.writeStAll(msg);
//                    }

                    //clientService.writeObjectOne(clientInfos);






                    //objectOutputStream.writeObject(clientI); //클라이언트 정보 전송



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

//    class CheckReady extends Thread{ //클라이언트들이 모두 ready 상태 인지 계속 체크하는 것
//
//        @Override
//        public void run(){
//            while(true){
//                int count = 0;
//                for(ClientInfo clientInfo:clientInfos){
//                    boolean isReady = clientInfo.getIsReady();
//                    if(isReady) {
//                        System.out.println("COUNT UP");
//                        count++;
//                    }
//                }
//                if(count==4) {
//                    System.out.println("COUNT IS 4");
//                    allReady = true;
//                }
//            }
//        }
//    }

    synchronized public void removeClientService(ClientService removeClientService){
        clientServices.remove(removeClientService);
    }

    class ClientService extends Thread{
        Socket clientSocket;
        InputStream is;
        OutputStream out;
        DataInputStream dis;
        DataOutputStream dos;
        ObjectOutputStream oos;
        String name;
       // boolean isready;
        int readyNum;
        ClientService removeClientService; //삭제할 클라이언트-서버 연결 스레드
        boolean stop = false;
        volatile int count;
        volatile int startCount;

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
        synchronized public void setStopTrue(){
            this.stop = true;
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
                       // isready = true; //ready 상태를 true로 설정
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

                        if(count == 2){ //만약 모두가 ready라면 /allReady 라는 메시지를 보낸다. ///
                            startCount = 5; //startCount 뒤에 게임이 시작되는 것임!
                            while(true){
                                System.out.println("startCount = "+startCount);
                                if(startCount==0)
                                    break;
                                writeStAll("/count "+startCount);
                                startCount--; //count가 줄어드는 걸 화면에 나타내도록 하기!!!
                                sleep(500);
                            }
                            writeStAll("/count "+"게임시작");

                            writeStAll("/allReady"); //게임 시작

                            for(ClientInfo clientInfo:clientInfos){ //다시 모두 게임 레디 초기화를 시킴
                                clientInfo.setIsReady(false);
                            }
                        }
                    }
                    else if(msg.startsWith("/readyOff")){
                       // isready = false; //ready 상태를 false로 설정
                        for(ClientInfo clientInfo:clientInfos){
                            if(clientInfo.getName().equals(stArray[1]))
                                clientInfo.setIsReady(false);
                        }
                        writeStAll(msg+"\n");
                    }
//                    else if(msg.startsWith("/gameOver")){
//                        writeStAll(msg); //게임이 오버되었다는 메시지를 보냄
//                    }
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

                        //클라이언트-서버 연결 삭제
                        removeClientService = null;
                        for(ClientService clientService:clientServices){
                            String name = clientService.getName();
                            if(name.equals(stArray[1])){
                                removeClientService = clientService;
                                break;
                            }

                        }
                        removeClientService(removeClientService); // 해당 스레드 삭제하는 함수 호출
                        //client와 통신하는 스레드에서도 삭제해야함
                        setStopTrue();
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
//                oos.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void writeStOne(String msg){ //클라이언트에게 메시지 보내는 함수
            try{
               dos.writeUTF(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        public DataOutputStream getDos(){
            return dos;
        }
//        public boolean getIsReady(){ //ready 여부를 반환하는 것
//            return isready;
//        }

    }

    public void writeObjectAll(Vector<ClientInfo> clientInfos){ //클라이언트들에게 객체를 보내는 함수
        for(ClientService clientService : clientServices){
            clientService.writeObjectOne(clientInfos); //클라이언트에게 객체 정보 전달

        }
    }
    public void writeStAll(String msg){
        for(ClientService clientService : clientServices){
            DataOutputStream dos = clientService.getDos();
            if(dos != null){
                try {

                    dos.writeUTF(msg);
                    System.out.println("client => "+clientService.clientSocket);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
//만일 무두가 ready라면 게임을 시작하도록 해야함