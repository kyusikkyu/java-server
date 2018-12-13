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
    // 5�繫��
//    public static final String IP = "192.168.0.139";
//    public static final String IP = "192.168.0.21";
    // ��
//    public static final String IP = "192.168.200.128";
    public static final String IP = "54.180.95.149";
    // 3�繫��
//    public static final String IP = "192.168.0.81";

    // ������ ������ ������ ����Ʈ
    ArrayList<User> clients;
    User uData;

    // ���� ��Ĺ ( ���񽺿� ���� )
    private ServerSocket serverSocket = null;

    public MainServer() {
        // ����ο��� ����Ʈ ����
        clients = new ArrayList<>();

        // clients ����ȭ
        Collections.synchronizedList(clients);
    }

    public static void main(String[] args) {
        new MainServer().start(); // �������� ����
    }

    // ( ���� ) �����Լ����� �����ϴ� �޼ҵ�
    private void start() {

        try {
            // 1. ���� ���� ����
            serverSocket = new ServerSocket();
            consoleLog("���� ��Ĺ ���� : ���� �����");

            // 2. ���ε�
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            serverSocket.bind(new InetSocketAddress(IP, PORT));
            consoleLog("���� ��ٸ� - " + IP + ":" + PORT);

            // 3. ��û ���
            while (true) {
                Socket socket = serverSocket.accept(); //���� �����   ( ����ϰ� �ִٰ� ������ ������ ����� )
                System.out.println("���� ���� : Ŭ������");
                // Ŭ�󿡼� �����ϰ� �Ǹ� ���ȸ���� ��������鼭  �����͸� �ְ� ���� �� �ִ�.

                // �����忡�� ��ü�� �ְ���� Stream �����ڸ� �����Ѵ�. ( ������ )
                // run����  ������ �ּҸ� �޾ƿ� ����ϰ� Ŭ���̾�Ʈ�� ������ �Ѱ��ְ� clients���� ip �ּ� mac �ּҸ� ������ .

                // ����ڰ� ���ö����� �����带 ������ ����
                //( ������ ������ ���ƾ߰���  100���� ������ �����尡 100���� ���´ϱ� )
                consoleLog(socket.toString());
                new MultiThread(socket).start(); // MultiThread �� run()����
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    } //  start() ��

    // ����Ŭ���� ( ����ڰ� �����Ҷ�����  �����带 �������ִ� Ŭ���� )
    class MultiThread extends Thread {

        Socket socket = null;
        String msg = null;
        String mac = null;
        String temp = null;
        DataInputStream in;
        DataOutputStream out;

        public MultiThread(Socket socket) {
            this.socket = socket;

            // ������ ������� ���� ��Ʈ�� ����
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
                // ó���� �ѹ��� �����ǵ� �̶� ������ ������ ���� !
                mac = in.readUTF();  //���� ( Ŭ�� ���� ���� ������ �´� )   Ŭ�� - ���� - Ŭ��

                // �ʱ⿡ �ѹ��� �������� ������ �޴°�.
                System.out.println("������ ������  : " + mac);

                // ����Ʈ�� �߰� ( Ŭ���̾�Ʈ�� ������ ���� ���̵� key�� ���� )
                String[] filter;
                filter = mac.split("@");

                //Ŭ���̾�Ʈ�� ������ ���� ����Ʈ�� �߰� �غ�
                uData = new User(socket, Integer.parseInt(filter[0]), filter[1], out);
                clients.add(uData);
                consoleLog("(�߰�)");
                // ù ���
//                if (clients.size() == 0) {
//                    clients.add(uData);
//                }

                // ( ���� �濡 �ִ� ���̵� �ִ��� Ž����  ������ �����Ѵ� )
//                for (int i = 0; i < clients.size(); i++) {
//                    try {
//                        // ���ȣ�� ����
//                        if (Integer.parseInt(filter[0]) == clients.get(i).roomNo) {
//
//                            // ���� ��ȿ� �� ���̵� �ִ� (�߰� X)
//                            if (filter[1].equals(clients.get(i).Userid)) {
//                                consoleLog("�濡 �̹� ���� �ִ�");
//                                break;
//                            } else {
//                                if (i == clients.size()-1) {
//                                    clients.add(uData);
//                                    consoleLog("���������� Ž���ߴµ� ���� ���� (�߰�)");
//                                }
//                            }
//                        } else { // ���ȣ�� �ٸ���
//
//                            // ���� ��ȿ� �� ���̵� �ִ� (�߰� X)
//                            if (filter[1].equals(clients.get(i).Userid)) {
//                                consoleLog("�濡 �̹� ���� �ִ�");
//                                break;
//                            } else {
//                                if (i == clients.size()-1) {
//                                    clients.add(uData);
//                                    consoleLog("���������� Ž���ߴµ� ���� ���� (�߰�)");
//                                }
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
                consoleLog("������ ������ : " + String.valueOf(clients.size()));
//                clients.add(uData);
//                consoleLog("���� �߰�");

                // ������ Ȯ��
                System.out.println(filter[1] + " ���� " + filter[0] + " �� �濡 �����ϼ̽��ϴ�.");

                // �޼��� ������ ( Ŭ�� ������ �濡 �����ߴٶ�� �޼����ε� , ���߿� ��ü ä���� ��쿡  �� ���� );
                // **** sendMsg( mac + "����");


                while (in != null) {
                    try {
                        temp = in.readUTF(); //���ŵ� �޼���
                        String[] filt;
                        filt = temp.split("@");
                        if (filt[3].equals("destroy")) {
                            System.out.println("Ŭ������?");
                            for (int i = 0; i < clients.size(); i++) {
                                if (clients.get(i).Userid.equals(filt[1])) {
                                    clients.remove(i);
                                }
                            }
                            in.close();
                            out.close();
                            socket.close();
                            this.interrupt();
                            System.out.println("Ŭ������?");
                            break;
                        } else {
                            sendMsg(temp);
                            System.out.println("MultiThread �� �� " + temp);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }//run �� ��


        // ������  Ŭ���̾�Ʈ���� ������ .  ( �׷��� ��� �����ڸ� �༭ �Ѵ� )
        void sendMsg(String msg) {
            consoleLog("// sendMsg ����");
            // ���ȣ, �������, �޴� ���, �޼���
            String[] filt1 = msg.split("@");

            // �޼��� ���۽� ��¥
            SimpleDateFormat mFormat = new SimpleDateFormat("aa hh:mm");

            consoleLog("// Ŭ�� ������ : "+clients.size());
            // Ŭ���̾�Ʈ���� ������ ���� ����
            for (int i = 0; i < clients.size(); i++) {
                try {
                    //  Ŭ���̾�Ʈ���� ������ ���� ���տ��� ���� �濡 �ְ� �ڽ��� �ƴ� ���濡�� ������ ��쿡   �޼�����  ������ .
                    // ���ȣ�� ����
                    if (Integer.parseInt(filt1[0]) == clients.get(i).roomNo) {

                        // ������ �ƴ� ��� ������� ������.
                        if (!filt1[1].equals(clients.get(i).Userid)) {
                            // Ŭ���̾�Ʈ�� OutputStream �� ����
                            OutputStream dos = clients.get(i).output;
                            DataOutputStream out = new DataOutputStream(dos);

                            // ���� �ð� �޾ƿ���
                            long mNow;
                            Date mDate;
                            mNow = System.currentTimeMillis();
                            mDate = new Date(mNow);

                            String time = mFormat.format(mDate);

                            // ������ ������Ը� ������
                            if (filt1[2].equals(clients.get(i).Userid)) {
                                // Ŭ���̾�Ʈ�� �޼����� ����  ( ���� �����, ���� )
                                if (filt1[3].equals("��Ī����~!") || filt1[3].equals("����Ȯ��~!")) {
//                                    consoleLog("// ��Ī�����˸�");
                                    out.writeUTF(filt1[1] + "@" + filt1[3] + "@" + time + "@" + filt1[4]);
                                } else if (filt1[3].equals("dudxhddhkTek~!`")) { // ���� ���ȣ ����
                                    out.writeUTF(filt1[1] + "@" + filt1[3] + "@" + time + "@" + filt1[4]);
                                } else {
//                                    consoleLog("// ������ �޽���");
                                    out.writeUTF(filt1[1] + "@" + filt1[3] + "@" + time);
                                }
                            } else { // ��û�� �̹� ������
//                                consoleLog("// ��û�� �̹� ������");
                                out.writeUTF(filt1[1] + "@" + "��û�� ����Ǿ����ϴ�.");
//                                clients.get(i).output.close();
//                                clients.get(i).userSocket.close();
                                clients.remove(i);
                            }

                            consoleLog("���� ��� : " + filt1[1]);
                            consoleLog("���� ���� : " + filt1[3]);
                            consoleLog("���� �ð� : " + time);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } // sendMsg ��
    }  // MultiThread ��

    private static void consoleLog(String log) {
        System.out.println("[server " + Thread.currentThread().getId() + "] " + log);
    }
}
