package chatting;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class chatting_client implements Runnable { // 채팅 클라이언트
    // chatting_client client = new chatting_client(); 소켓 연결
    // client.make_room(1, user_id, 참여자리스트); 방 생성
    // client.invite_room(2, user_id, 방번호, 참여자리스트); 방 초대
    // client.delete_room(3, user_id, 방번호); 방 제거
    // client.send_messege(4, 방번호, user_id, 메시지, 시간, 파일유무, 파일경로); 메시지 보내기
    // client.get_room_list(11, user_id); 방 목록 받아오기
    // client.get_room_member(12, user_id, 방번호); 방 멤버 받아오기
    // client.get_room_messege(13, user_id, 방번호); 방 메시지 받아오기
    // client.get_user_list_in_room(14, user_id, 방번호); 방에 있는 유저 목록 받아오기
    // socket.close(); 소켓 닫기
    public static Socket socket; // 소켓
    private ObjectOutputStream oos; // 출력 스트림
    private ObjectInputStream ois; // 입력 스트림

    public String user_id; // 사용자 아이디
    public ArrayList<String> my_room_list; // 내가 속한 방 목록
    private DataOutputStream dos; // 파일 전송용 출력 스트림
    private BufferedOutputStream bos; // 파일 전송용 버퍼
    private PrintWriter pw; // 파일 전송용 출력 스트림
    private ListeningThread t1; // 채팅 메시지 수신용 스레드

    public chatting_client(String user_id) {
        try {
            this.socket = new Socket("swiftsjh.tplinkdns.com", 25588); // 소켓 연결
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
            this.bos =new BufferedOutputStream(socket.getOutputStream());
            this.pw = new PrintWriter(bos);
            pw.println(user_id); // 사용자 아이디 전송
            pw.flush();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void chat_message(protocol content){
        try{
            this.oos.writeObject(content); // 프로토콜로 담은 내용 전송
            this.oos.flush();
        }
        catch(Exception e2){
            System.out.println(e2);
        }
    }
    public void make_room(int typeofrequest, String sender, ArrayList<String> list){
        protocol content = new protocol(typeofrequest, sender, list);
        chat_message(content); // 방 생성
    }
    public void invite_room(int typeofrequest, String sender, String roomnumber, ArrayList<String> list){
        protocol content = new protocol(typeofrequest, sender, roomnumber, list);
        chat_message(content); // 방 초대
    }
    public void exit_room(int typeofrequest, String sender, String roomnumber){
        protocol content = new protocol(typeofrequest, sender, roomnumber);
        chat_message(content); // 방 나가기
    }
    public void send_messege(int typeofrequest, String roomnumber, String sender, String messege, boolean file_exist, String file_name){
        protocol content = new protocol(typeofrequest, roomnumber, sender, messege, file_exist, file_name);
        chat_message(content); // 메시지 보내기
    }
    public void follow_bool(int typeofrequest, String sender, String id){
        protocol content = new protocol(typeofrequest,sender,id);
        chat_message(content);
    }
    public void logout(int typeofrequest, String sender){
        protocol content = new protocol(typeofrequest, sender);
        chat_message(content);
        sockt_close(); // 로그아웃
    }
    public void send_file(int typeofrequest, String roomnumber, String file_path,chatting_client A){
        try { // 파일 전송
            file_client file;
            boolean file_exist = true;
            filechoose choice = new filechoose();
            String filename = choice.jFileChooserUtil();
            String filetype = filename.substring(filename.lastIndexOf("."));
            Socket sc = new Socket("swiftsjh.tplinkdns.com", 25589);
            protocol time = new protocol();
            time.setTime();
            file=new file_client(sc, filename, roomnumber, time.getTime(),1,A);
            A.send_messege(4, roomnumber, user_id, null, file_exist, time.getTime()+filetype);
            file_exist = false;
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void get_room_list(int typeofrequest, String sender){
        protocol content = new protocol(typeofrequest, sender);
        chat_message(content); // 방 목록 받아오기
    }
    public void get_user_list_in_room(int typeofrequest, String sender, String roomnumber){
        protocol content = new protocol(typeofrequest, sender, roomnumber);
        chat_message(content); // 방에 있는 유저 목록 받아오기
    }

    public void get_all_user_list(int typeofrequest, String sender){
        protocol content = new protocol(typeofrequest, sender);
        chat_message(content); // 전체 유저 목록 받아오기
    }

    public void get_post_num(int typeofrequest, String sender){
        protocol content = new protocol(typeofrequest, sender);
        chat_message(content); // 게시글 개수 받아오기
    }
    public void get_following_num(int typeofrequest, String sender){
        protocol content = new protocol(typeofrequest, sender);
        chat_message(content); // 팔로잉 개수 받아오기
    }
    public void get_follow_num(int typeofrequest, String sender){
        protocol content = new protocol(typeofrequest, sender);
        chat_message(content); // 팔로워 개수 받아오기
    }
    public void sockt_close(){
        try{ // 소켓 닫기
            this.oos.close();
            this.ois.close();
            this.socket.close();
        }
        catch(Exception e3){
            System.out.println(e3);
        }
    }
    public ListeningThread get_listening(){
        // 채팅 메시지 수신용 스레드
        return t1;
    }
    @Override
    public void run(){
        // ListeningThread 객체 생성
        ListeningThread t1 = new ListeningThread(socket,my_room_list);
        t1.start(); // ListeningThread 시작
        this.t1 = t1;
    }
}