package chat_room;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;


public class ServerThread extends Thread {
    private Socket socket;
    private ArrayList<ServerThread> threadList;
    private BufferedWriter output;
    private String account = "", roomid = "", sname = "";
    private Boolean Gstate = true, Sstate = false;
    private String outputMsg, Msg;
    private DB_Server DB;
    public ServerThread(Socket socket, ArrayList<ServerThread> threads) throws SocketException {
        this.socket = socket;
        this.threadList = threads;
        this.DB = new DB_Server();
    }

    @Override
    public void run() {

        try {
            //Client input
            BufferedReader input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            
            //returning the output to the client
             
            while(true) {
                output  = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                Msg = input.readLine();
            	System.out.println(Msg);
                outputMsg = "Server:";
                msgcut tool = new msgcut();
                //if user types exit command
                if(Msg.contains("RequestType:R00")) {
                	//create the account and if correct create then exit Create account
                	account = tool.get_Username(Msg, 0);
                	String pass = tool.get_Pessword(Msg);
                	int state = DB.u_check(account,pass);
                	
                	if(state == 0) {
                		DB.u_insert(account, pass);
                		outputMsg += "ResponseType:R00-0";
                		output.write(outputMsg);
                		output.newLine();
                		output.flush();
                	}
                	else {
                		outputMsg += "ResponseType:R00-1";
                		output.write(outputMsg);
                		output.newLine();
                		output.flush();
                	}
            		break;
                }
                else if(Msg.contains("RequestType:R01")) {
                	//login the account and if correct login then exit login
                	account = tool.get_Username(Msg, 0);
                	String pass = tool.get_Pessword(Msg);
                	int state = DB.u_check(account,pass);
                	if(state == 2) {
                		DB.u_login(account);
                		outputMsg += "ResponseType:R01-" + String.valueOf(state);
                		output.write(outputMsg);
                		output.newLine();
                		output.flush();
                	}
                	else {
                		outputMsg += "ResponseType:R01-" + String.valueOf(state);
                		output.write(outputMsg);
                		output.newLine();
                		output.flush();
                	}
            		break;
                }
                else if(Msg.contains("RequestType:R02")) {
                	//create the group
                	String g_id = tool.get_Socketname(Msg, 0);
                	if(DB.g_check(g_id)) {
                    	DB.g_insert(g_id);
                    	outputMsg += "ResponseType:R02-0";//未存在群組
                		output.write(outputMsg);
                		output.newLine();
                    	output.flush();
                	}
                	else {
                		outputMsg = "ResponseType:R02-1";//已存在群組
                		output.write(outputMsg);
                		output.newLine();
                		output.flush();
                	}
           		break;
                }
                else if(Msg.contains("RequestType:R03")) {
                	//add the friend
                	String acc = tool.get_Username(Msg, 1),f_id = tool.get_Friend(Msg);
                	if(DB.f_check(acc, f_id)){
                		DB.u_friend(f_id, acc);
                    	outputMsg += "ResponseType:R03-0";//未加好友
                		output.write(outputMsg);
                		output.newLine();
                    	output.flush();
                	}
                	else {
                    	outputMsg += "ResponseType:R03-1";//以加好友
                		output.write(outputMsg);
                		output.newLine();
                    	output.flush();
                	}
            		break;
                }
                else if(Msg.contains("RequestType:R04")) {
                	//affiliate the group and exit chatlist
                	account = tool.get_Username(Msg, 2);
                	roomid = tool.get_Socketname(Msg,1);
                	Boolean type = tool.get_Gstate(Msg);
                	DB.u_aff(roomid, account);
                	if(type) {
                		String history = tool.ListtoString(DB.g_aff(roomid, account));
                		outputMsg += "ResponseType:R04-0|List:" + history;//群組聊天
                		output.write(outputMsg);
                		output.newLine();
                    	output.flush();
                	}
                	else {
                    	String history = tool.ListtoString(DB.f_aff(roomid, account));
                    	outputMsg += "ResponseType:R04-1|List:" + history;//好友聊天
                		output.write(outputMsg);
                		output.newLine();
                    	output.flush();
                	}
                }
                else if(Msg.contains("RequestType:R05")) {
                	//disaffiliate the group and exit chatroom
                	String acc = tool.get_Username(Msg, 2), chatid = tool.get_Socketname(Msg,1);
                	Boolean type = tool.get_Gstate(Msg);
                	DB.u_disaff(chatid, acc);
                	if(type) {
                		DB.g_disaff(chatid, acc);
                	}
                	outputMsg += "ResponseType:R05";//退出聊天室
            		output.write(outputMsg);
            		output.newLine();
                	output.flush();
                    break;
                }
                else if(Msg.contains("RequestType:R06")) {
                	//logout the system and exit
                	String acc = tool.get_Username(Msg, 4);
                	DB.u_logout(acc);
                	outputMsg += "ResponseType:R06";//登出
            		output.write(outputMsg);
            		output.newLine();
                	output.flush();
                    break;
                }
                else if(Msg.contains("RequestType:R07")) {
                	//total user list update
            		String user = tool.ListtoString(DB.show_user_list());//更新list
                	outputMsg += "ResponseType:R07" + "|List:" + user;//userlist
                	System.out.println(outputMsg);
            		output.write(outputMsg);
            		output.newLine();
                	output.flush();
            		break;
                }
                else if(Msg.contains("RequestType:R08")) {
                	//friend list update
                	String acc = tool.get_Username(Msg, 4);
            		String friend = tool.ListtoString(DB.show_friend_list(acc));//更新list
                	outputMsg += "ResponseType:R08" + "|List:" + friend;//friendlist
            		output.write(outputMsg);
            		output.newLine();
                	output.flush();
            		break;
                }
                else if(Msg.contains("RequestType:R09")) {
                	//group list update
            		String group = tool.ListtoString(DB.show_group_list());//更新list
                	outputMsg += "ResponseType:R09" + "|List:" + group;//grouplist
            		output.write(outputMsg);
            		output.newLine();
                	output.flush();
            		break;
                }
                else if(Msg.contains("RequestType:R10")) {
                	//current member list update
                	String chatid = tool.get_Socketname(Msg, 0);
            		String member = tool.ListtoString(DB.show_current_list(chatid));//更新list
                	outputMsg += "ResponseType:R10" + "|List:" + member;//currentlist
            		output.write(outputMsg);
            		output.newLine();
                	output.flush();
                }
                else if(Msg.contains("RequestType:C")) {
                	account = tool.get_Username(Msg, 2);
                	roomid = tool.get_Socketname(Msg,1);
                	Gstate = tool.get_Gstate(Msg);
                	Sstate = tool.get_Sstate(tool.get_Secretname(Msg));
                	String texture = "[" + account + "]:" + tool.get_Texture(Msg);
                	outputMsg = "ResponseType:C"
                			    + "|Username:" + account
                				+ "|Secret:" + tool.get_Secretname(Msg)
                				+ "|Texture:" +texture;//訊息
        			DB.log_update(roomid, account, outputMsg , Gstate);
                	if(Gstate) {
                		if(Sstate) {
                    		printSChat(roomid,outputMsg,tool.get_Secretname(Msg),account);
                		}
                		else {
                    		printGChat(roomid,outputMsg);
                		}
                	}
                	else {
            			printFChat(roomid,account, outputMsg);
                	}
                }
                else {
                	System.out.println("error:message not found:"+Msg);
            		break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error occured msg" +e.getMessage());
        }
    }

    private void printFChat(String friend, String acc, String msg){
        for(ServerThread sT: threadList) {
        	if(sT.roomid.equals(friend)||(sT.roomid.equals(acc)&&sT.account.equals(friend))) {
        		System.out.println(sT.account + ":" + sT.roomid);
            	try {
            		sT.output.write(msg);
    				sT.output.newLine();
    	            sT.output.flush();
            		System.out.println("send message:" + msg);
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				System.out.println(e.getMessage());
    			}
            }
        }
	}

	private void printGChat(String gid, String msg){
		System.out.println(threadList);	
        for(ServerThread sT: threadList) {
    		System.out.println(sT.account + ":" + sT.roomid);
    		if(gid.equals(sT.roomid)) {
            	try {
            		sT.output.write(msg);
    				sT.output.newLine();
    	            sT.output.flush();
            		System.out.println("send message" + msg);
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				System.out.println(e.getMessage());
    			}
    		}
        }
    }
	
    private void printSChat(String gid, String msg, String s, String acc){
        for(ServerThread sT: threadList) {
    		System.out.println(sT.account + ":" + sT.roomid);
    		if(!sT.account.equals("")&&!sT.roomid.equals("")) {
            	if(sT.roomid.equals(gid) && (sT.account.equals(s)||sT.account.equals(acc))){
            		System.out.println(sT.account + ":" + sT.roomid);
                	try {
                		sT.output.write(msg);
        				sT.output.newLine();
        	            sT.output.flush();
                		System.out.println("send message" + msg);
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				System.out.println(e.getMessage());
        			}
                }
    			
    		}
        }
    }
}
