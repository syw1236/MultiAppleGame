public class ChatMessage extends Message{
    String msg;
    public ChatMessage(String msg){
        this.msg = msg;
    }
    @Override
    public String chatting(){
        return msg; //받은 메시지를 보냄
    }
}
