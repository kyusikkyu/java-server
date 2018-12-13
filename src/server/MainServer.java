package server;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainServer {
	public static final int PORT = 5000;
    public static final String IP = "54.180.95.149";
   
    ArrayList<User> clients;
    User uData;

   
    private ServerSocket serverSocket = null;

    public MainServer() {
        clients = new ArrayList<>();

        Collections.synchronizedList(clients);
    }

    public static void main(String[] args) {
        new MainServer().start(); 
    }

    
    private void start() {

        try {
            serverSocket = new ServerSocket();
            consoleLog("서버 소캣 생성 : 접속 대기중");

            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            serverSocket.bind(new InetSocketAddress(IP, PORT));
            consoleLog("연결 기다림 - " + IP + ":" + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("서버 시작 : 클라접속");
                
                consoleLog(socket.toString());
                new MultiThread(socket).start(); 


        } catch (Exception e) {
            e.printStackTrace();
        }

    } 

    
    class MultiThread extends Thread {

        Socket socket = null;
        String msg = null;
        String mac = null;
        String temp = null;
        DataInputStream in;
        DataOutputStream out;

        public MultiThread(Socket socket) {
            this.socket = socket;

           
            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            try {
               
                mac = in.readUTF();  
               
                System.out.println("접속자 정보들  : " + mac);
              
                String[] filter;
                filter = mac.split("@");

                uData = new User(socket, Integer.parseInt(filter[0]), filter[1], out);
                clients.add(uData);
                consoleLog("(추가)");
                
                System.out.println(filter[1] + " 님이 " + filter[0] + " 번 방에 입장하셨습니다.");

                while (in != null) {
                    try {
                        temp = in.readUTF(); 
                        String[] filt;
                        filt = temp.split("@");
                        if (filt[3].equals("destroy")) {
                            System.out.println("클라종료?");
                            for (int i = 0; i < clients.size(); i++) {
                                if (clients.get(i).Userid.equals(filt[1])) {
                                    clients.remove(i);
                                }
                            }
                            in.close();
                            out.close();
                            socket.close();
                            this.interrupt();
                            System.out.println("클라종료?");
                            break;
                        } else {
                            sendMsg(temp);
                            System.out.println("MultiThread 의 런 " + temp);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

       
        void sendMsg(String msg) {
            consoleLog("// sendMsg 시작");
            
            String[] filt1 = msg.split("@");

           
            SimpleDateFormat mFormat = new SimpleDateFormat("aa hh:mm");

            consoleLog("// 클라 사이즈 : "+clients.size());
            
            for (int i = 0; i < clients.size(); i++) {
                try {
                    if (Integer.parseInt(filt1[0]) == clients.get(i).roomNo) {

                        if (!filt1[1].equals(clients.get(i).Userid)) {
                            OutputStream dos = clients.get(i).output;
                            DataOutputStream out = new DataOutputStream(dos);

                            long mNow;
                            Date mDate;
                            mNow = System.currentTimeMillis();
                            mDate = new Date(mNow);

                            String time = mFormat.format(mDate);

                            if (filt1[2].equals(clients.get(i).Userid)) {
                                if (filt1[3].equals("매칭수락~!") || filt1[3].equals("수락확인~!")) {
                                    out.writeUTF(filt1[1] + "@" + filt1[3] + "@" + time + "@" + filt1[4]);
                                } else if (filt1[3].equals("dudxhddhkTek~!`")) { // 영통 방번호 전달
                                    out.writeUTF(filt1[1] + "@" + filt1[3] + "@" + time + "@" + filt1[4]);
                                } else {
                                    out.writeUTF(filt1[1] + "@" + filt1[3] + "@" + time);
                                }
                            } else { 
                                out.writeUTF(filt1[1] + "@" + "요청이 종료되었습니다.");
                                clients.remove(i);
                            }

                            consoleLog("보낸 사람 : " + filt1[1]);
                            consoleLog("보낸 내용 : " + filt1[3]);
                            consoleLog("보낸 시간 : " + time);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } 
    } 

    private static void consoleLog(String log) {
        System.out.println("[server " + Thread.currentThread().getId() + "] " + log);
    }
}
