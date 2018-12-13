package server;

import java.io.DataOutputStream;
import java.net.Socket;

public class User {
    Socket userSocket;
    int roomNo;
    String Userid;
    DataOutputStream output;

    public User(Socket userSocket,int roomNo, String UserId, DataOutputStream output) {

        this.userSocket = userSocket;
        this.roomNo = roomNo;
        this.Userid = UserId;
        this.output = output;

    }
}
