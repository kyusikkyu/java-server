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
    // 5사무실
//    public static final String IP = "192.168.0.139";
//    public static final String IP = "192.168.0.21";
    // 집
//    public static final String IP = "192.168.200.128";
    public static final String IP = "54.180.95.149";
    // 3사무실
//    public static final String IP = "192.168.0.81";

    // 접속자 정보를 저장할 리스트
    ArrayList<User> clients;
    User uData;

    // 서버 소캣 ( 서비스용 소켓 )
    private ServerSocket serverSocket = null;

    public MainServer() {
        // 연결부에서 리스트 생성
        clients = new ArrayList<>();

        // clients 동기화
        Collections.synchronizedList(clients);
    }

    public static void main(String[] args) {
        new MainServer().start(); // 쓰레드의 시작
    }

    // ( 접속 ) 메인함수에서 시작하는 메소드
    private void start() {

        try {
            // 1. 서버 소켓 생성
            serverSocket = new ServerSocket();
            consoleLog("서버 소캣 생성 : 접속 대기중");

            // 2. 바인딩
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            serverSocket.bind(new InetSocketAddress(IP, PORT));
            consoleLog("연결 기다림 - " + IP + ":" + PORT);

            // 3. 요청 대기
            while (true) {
                Socket socket = serverSocket.accept(); //접속 대기중   ( 대기하고 있다가 들어오면 소켓을 만들고 )
                System.out.println("서버 시작 : 클라접속");
                // 클라에서 접속하게 되면 통신회선이 만들어지면서  데이터를 주고 받을 수 있다.

                // 쓰레드에서 객체를 주고받을 Stream 생성자를 선언한다. ( 생성자 )
                // run에서  접속한 주소를 받아와 출력하고 클라이언트에 정보를 넘겨주고 clients에게 ip 주소 mac 주소를 보낸다 .

                // 사용자가 들어올때마다 쓰레드를 생성및 실행
                //( 서버의 성능이 좋아야겟지  100명이 들어오면 쓰레드가 100개가 돌태니까 )
                consoleLog(socket.toString());
                new MultiThread(socket).start(); // MultiThread 의 run()실행
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    } //  start() 끝

    // 내부클래스 ( 사용자가 접속할때마다  쓰레드를 생성해주는 클래스 )
    class MultiThread extends Thread {

        Socket socket = null;
        String msg = null;
        String mac = null;
        String temp = null;
        DataInputStream in;
        DataOutputStream out;

        public MultiThread(Socket socket) {
            this.socket = socket;

            // 데이터 입출력을 위한 스트림 실행
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
                // 처음에 한번만 받을건데 이때 정보를 가지고 오자 !
                mac = in.readUTF();  //수신 ( 클라가 글을 쓰면 서버로 온다 )   클라 - 서버 - 클라

                // 초기에 한번만 접속자의 정보를 받는것.
                System.out.println("접속자 정보들  : " + mac);

                // 리스트에 추가 ( 클라이언트의 정보를 저장 아이디를 key로 쓰자 )
                String[] filter;
                filter = mac.split("@");

                //클라이언트의 정보를 만들어서 리스트에 추가 준비
                uData = new User(socket, Integer.parseInt(filter[0]), filter[1], out);
                clients.add(uData);
                consoleLog("(추가)");
                // 첫 등록
//                if (clients.size() == 0) {
//                    clients.add(uData);
//                }

                // ( 기존 방에 있는 아이디가 있는지 탐색후  없을시 저장한다 )
//                for (int i = 0; i < clients.size(); i++) {
//                    try {
//                        // 방번호가 같고
//                        if (Integer.parseInt(filter[0]) == clients.get(i).roomNo) {
//
//                            // 같은 방안에 내 아이디가 있다 (추가 X)
//                            if (filter[1].equals(clients.get(i).Userid)) {
//                                consoleLog("방에 이미 내가 있다");
//                                break;
//                            } else {
//                                if (i == clients.size()-1) {
//                                    clients.add(uData);
//                                    consoleLog("마지막까지 탐색했는데 내가 없네 (추가)");
//                                }
//                            }
//                        } else { // 방번호가 다르고
//
//                            // 같은 방안에 내 아이디가 있다 (추가 X)
//                            if (filter[1].equals(clients.get(i).Userid)) {
//                                consoleLog("방에 이미 내가 있다");
//                                break;
//                            } else {
//                                if (i == clients.size()-1) {
//                                    clients.add(uData);
//                                    consoleLog("마지막까지 탐색했는데 내가 없네 (추가)");
//                                }
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
                consoleLog("접속한 유저수 : " + String.valueOf(clients.size()));
//                clients.add(uData);
//                consoleLog("유저 추가");

                // 접속자 확인
                System.out.println(filter[1] + " 님이 " + filter[0] + " 번 방에 입장하셨습니다.");

                // 메세지 보내기 ( 클라 쪽으로 방에 접속했다라는 메세지인데 , 나중에 단체 채팅일 경우에  써 보자 );
                // **** sendMsg( mac + "접속");


                while (in != null) {
                    try {
                        temp = in.readUTF(); //수신된 메세지
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
        }//run 의 끝


        // 서버가  클라이언트에게 보낸다 .  ( 그룹인 경우 구분자를 줘서 한다 )
        void sendMsg(String msg) {
            consoleLog("// sendMsg 시작");
            // 방번호, 보낸사람, 받는 사람, 메세지
            String[] filt1 = msg.split("@");

            // 메세지 전송시 날짜
            SimpleDateFormat mFormat = new SimpleDateFormat("aa hh:mm");

            consoleLog("// 클라 사이즈 : "+clients.size());
            // 클라이언트들의 정보를 모은 집합
            for (int i = 0; i < clients.size(); i++) {
                try {
                    //  클라이언트들의 정보를 모은 집합에서 같은 방에 있고 자신이 아닌 상대방에게 보내는 경우에   메세지를  보낸다 .
                    // 방번호가 같고
                    if (Integer.parseInt(filt1[0]) == clients.get(i).roomNo) {

                        // 본인이 아닌 모든 사람에게 보낸다.
                        if (!filt1[1].equals(clients.get(i).Userid)) {
                            // 클라이언트의 OutputStream 을 저장
                            OutputStream dos = clients.get(i).output;
                            DataOutputStream out = new DataOutputStream(dos);

                            // 현재 시간 받아오기
                            long mNow;
                            Date mDate;
                            mNow = System.currentTimeMillis();
                            mDate = new Date(mNow);

                            String time = mFormat.format(mDate);

                            // 지정한 사람에게만 보낸다
                            if (filt1[2].equals(clients.get(i).Userid)) {
                                // 클라이언트에 메세지를 전송  ( 보낸 사람과, 내용 )
                                if (filt1[3].equals("매칭수락~!") || filt1[3].equals("수락확인~!")) {
//                                    consoleLog("// 매칭수락알림");
                                    out.writeUTF(filt1[1] + "@" + filt1[3] + "@" + time + "@" + filt1[4]);
                                } else if (filt1[3].equals("dudxhddhkTek~!`")) { // 영통 방번호 전달
                                    out.writeUTF(filt1[1] + "@" + filt1[3] + "@" + time + "@" + filt1[4]);
                                } else {
//                                    consoleLog("// 보내는 메시지");
                                    out.writeUTF(filt1[1] + "@" + filt1[3] + "@" + time);
                                }
                            } else { // 요청이 이미 수락됬다
//                                consoleLog("// 요청이 이미 수락됬다");
                                out.writeUTF(filt1[1] + "@" + "요청이 종료되었습니다.");
//                                clients.get(i).output.close();
//                                clients.get(i).userSocket.close();
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
        } // sendMsg 끝
    }  // MultiThread 끝

    private static void consoleLog(String log) {
        System.out.println("[server " + Thread.currentThread().getId() + "] " + log);
    }
}
