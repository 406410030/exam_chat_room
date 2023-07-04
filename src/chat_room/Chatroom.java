package chat_room;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Timer;

import javax.swing.JTextArea;

public class Chatroom extends JFrame{
	private JPanel upperPane;
    private JPanel downPane;
	private JTextField chatbar;
	private JTextArea chatwindow;
	private JScrollPane chatscroll, listscroll;
	private JButton sendbtn, disaffbtn;
	private JCheckBox secret;
	private JComboBox<String> currentlist;
	private DefaultComboBoxModel<String> model;
	private String account, roomid;
	private String[] def = {"None"};
//	public ArrayList<String> newlistitem;
	private boolean secretstate = false, groupstate;
	private String localhost = "127.0.0.1";
	private int port = 6060;
	private BufferedWriter output;
	private String Cmd_daff, Cmd_clist, Cmd_grpaff;
	private Socket socket;
	private BufferedReader input;
	
	public Chatroom(String user, String rname, Boolean type, List<String> history){
		account = user;
		roomid = rname;
		groupstate = type;
		chatwindow = new JTextArea();
		chatwindow.setEditable(false);
		chatwindow.setLineWrap(true);
		chatscroll = new JScrollPane(chatwindow);

		msgcut tool = new msgcut();
		System.out.println(history);
			Iterator<String> it =  history.iterator();
			while(it.hasNext()) {
				String logit = it.next();
				System.out.println(logit);
					if(logit.contains("|Secret:")) {
						if(tool.get_Secretname(logit).equals("=NULL")) {
						chatwindow.append( tool.get_Texture(logit) + System.lineSeparator());
						}
						else {
							if(tool.get_Username(logit, 5).equals(account)||tool.get_Secretname(logit).equals(account))
								chatwindow.append( tool.get_Texture(logit)  + System.lineSeparator());
						}
					}
			}
		Cmd_daff = "RequestType:R05"
		+ "|UserName:" + account
		+ "|SocketName:" + roomid
		+ "|Type:" + boolstring(groupstate);

		Cmd_clist =     "RequestType:R10"
				+ "|SocketName:" + roomid;

		upperPane = new JPanel();
		downPane = new JPanel();
		
		secret = new JCheckBox("秘密通話");
	    model = new DefaultComboBoxModel(def);
	    currentlist = new JComboBox(model);
		listscroll = new JScrollPane(currentlist);
		chatbar = new JTextField(20);
		sendbtn = new JButton("送出");
		disaffbtn = new JButton("退出");

		upperPane.add(secret);
		upperPane.add(currentlist);
		downPane.add(chatbar);
		downPane.add(sendbtn);
		downPane.add(disaffbtn);

		if(type)
		add(upperPane, BorderLayout.NORTH);
		add(chatscroll, BorderLayout.CENTER);
		add(downPane, BorderLayout.SOUTH);
		setTitle("User : " + account + " chat room : " + roomid);
		setSize(400, 500);
		setLocation(500, 500);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
		   		try {
			   		output.write(Cmd_daff);
					output.newLine();
			   		output.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				super.windowClosing(e);
				Chatlist window = new Chatlist(account);
				//退出群組關閉視窗
			}
		});
        thehandler handler = new thehandler();
		disaffbtn.addActionListener(handler);
		sendbtn.addActionListener(handler);
		secret.addActionListener(handler);
		currentlist.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
		   		try {
			   		output.write(Cmd_clist);
					output.newLine();
			   		output.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
   		try {
			socket = new Socket(localhost,port);
			input = new BufferedReader( new InputStreamReader(socket.getInputStream()));

			output  = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));   //利用sk來取得輸出串流
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Thread thread = new Thread(new MsgReader());
		thread.start();
		setVisible(true);
	}
	class MsgReader implements Runnable {
		public MsgReader(){
			}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			 try{
					String Msg;
					msgcut tool = new msgcut();
						while(true) {
							Msg = input.readLine();
							if(Msg.contains("ResponseType:C")) {
								chatwindow.append(tool.get_Texture(Msg) + System.lineSeparator());
							}
							else if(Msg.contains("ResponseType:R10")) {
								  List<String> current = tool.get_Current_List(Msg);
								  System.out.println(current);
							      model.removeAllElements();
							      for(String item:current) {
							    	  if(!item.equals(account))
								      currentlist.addItem(item);
							      }
								  System.out.println(Msg);
							}
							else if(Msg.contains("ResponseType:R05")) {
								  System.out.println(Msg);
								  break;
							}
						}
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						System.out.println("unexpect socket exit1");
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.println("unexpect socket exit2" + e1.getMessage());
						e1.printStackTrace();
					}
		}
	}

	private class thehandler implements ActionListener{
		
		public void actionPerformed(ActionEvent event) {

			if(event.getSource() == disaffbtn) {
				
		   		try {
			   		output.write(Cmd_daff);
					output.newLine();
			   		output.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    Chatlist cl = new Chatlist(account);
			    dispose();
				}
			if(event.getSource() == secret) {
				if(secret.isSelected())secretstate = true;
				else secretstate = false;
				System.out.println(secretstate);
		   		try {
			   		output.write(Cmd_clist);
					output.newLine();
			   		output.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		      
//			System.out.println(currentlist.getSelectedItem());
			}
			if(event.getSource() == sendbtn) {
				String Msg = new String();
				Msg =     "RequestType:C"
						+ "|UserName:" + account
						+ "|SocketName:" + roomid
						+ "|Type:" + boolstring(groupstate)
						+ "|Secret:" + checksecret(secretstate)
						+ "|Texture:"+ chatbar.getText();
				System.out.println(Msg+"|send");
				
		   		try {
			   		output.write(Msg);
					output.newLine();
			   		output.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("there is check error");
					e1.printStackTrace();
				}
		   		chatbar.setText("");
		   	}
		}
	}

	private String checksecret(boolean state) {
			// TODO Auto-generated method stub
		if(state) return (String) currentlist.getSelectedItem();
		else return "=NULL";
		}


	private String boolstring(Boolean type) {
		String typename;
		if(type) typename = "T";
		else typename = "F";
		return typename;
	}
}
