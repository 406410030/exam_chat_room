package chat_room;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server{
private static int port = 6060;
public static void main(String[] args) {
        //using serversocket as argument to automatically close the socket
	
        ArrayList<ServerThread> threadList = new ArrayList<>();
        try (ServerSocket serversocket = new ServerSocket(port)){
            while(true) {
                Socket socket = serversocket.accept();
                ServerThread serverThread = new ServerThread(socket, threadList);
                //add the threat to threadpool & starting the thread
                threadList.add(serverThread); 
                serverThread.start();
            }
        } catch (Exception e) {
            System.out.println("Error occured in server: " + e.getStackTrace());
        }
    }
}