import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class AppleGameChatPanel extends JPanel {
    private JTextArea chatTextArea;
    private JTextField inputText;
    private Socket clientSocket;
    private InputStream is;
    private OutputStream out;
    private DataInputStream dis;
    private DataOutputStream dos;

    private String myName;

    public AppleGameChatPanel(Socket clientSocket,String myName){
        setLayout(null);

        this.clientSocket = clientSocket;
        this.myName = myName;
        try{
            is = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
            dis = new DataInputStream(is);
            dos = new DataOutputStream(out);
        }catch (Exception e){
            e.printStackTrace();
        }

        chatTextArea = new JTextArea();
        chatTextArea.setFont(new Font("Arial", Font.PLAIN, 17));
        chatTextArea.setLineWrap(true); // 자동 줄 바꿈 활성화
        chatTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatTextArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // 가로 스크롤 비활성화

        scrollPane.setSize(253,300);
        scrollPane.setLocation(5,5);
        // JFrame의 컨텐트 팬에 JScrollPane 추가
        add(scrollPane);

        inputText = new JTextField();
        inputText.setFont(new Font("Arial", Font.PLAIN, 17));
        inputText.setBounds(3,303,220,35);
        add(inputText);

//        inputText.requestFocus();

        JButton sendBtn = new JButton("전송");
        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = inputText.getText();

                try {
                    dos.writeUTF("/chat < " + myName + " > " + inputText.getText());
                }catch (Exception err){
                    err.printStackTrace();
                }
                System.out.println("클라이언트 입력 메시지 ="+inputText.getText());
                inputText.setText("");
            }
        });
        sendBtn.setForeground(Color.gray);
        sendBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        sendBtn.setBounds(220,303,42,35);
        add(sendBtn);
        requestFocus();

    }
    public void appendTextArea(String msg){
        chatTextArea.append(msg);
    }

}
