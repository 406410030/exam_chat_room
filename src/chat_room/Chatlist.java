package chat_room;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class Chatlist extends JFrame{
	private JPanel contentPane;
	private JLabel Groupcreate, Select, Userlist;
	private JTextField Gcreate;
	private JComboBox Ulist, Glist, Flist;
	private JButton grpcrtbtn, grpaffbtn, exitbtn, friendbtn;
	private JRadioButton grpbtn, frdbtn;
	private String account;
	private String[] def = {"None"};
	
	private String localhost = "127.0.0.1";
	private int port = 6060;
	private BufferedWriter output;
	private String Cmd_crt,Cmd_logout,Cmd_ulist,Cmd_flist,Cmd_glist,Cmd_friend,Cmd_grpaff;
	private ButtonGroup btngroup;
	private JScrollPane ulistscroll, glistscroll, flistscroll;
	private DefaultComboBoxModel umodel, gmodel, fmodel;
	
	public Chatlist(String user) {
		super(user);//設定名稱
		setLayout(new FlowLayout());
		account = user;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(740, 350, 380, 500);
		contentPane = new JPanel();
        contentPane.setLayout(null);
		setContentPane(contentPane);

		Cmd_logout = "RequestType:R06"
				+ "|UserName:" + account;
		
		Cmd_ulist = "RequestType:R07";
		
		Cmd_flist = "RequestType:R08"
				+ "|UserName:" + account;
		
		Cmd_glist = "RequestType:R09";
		
		Groupcreate = new JLabel("建立群組:");
    	Groupcreate.setBounds(0, 10, 70, 30);
		contentPane.add(Groupcreate);
		
		Gcreate = new JTextField("");
    	Gcreate.setBounds(70, 15, 200, 20);
		contentPane.add(Gcreate);
		
		grpcrtbtn = new JButton("建立");
		grpcrtbtn.setBounds(280, 10, 70, 30);
		contentPane.add(grpcrtbtn);
		
		Userlist = new JLabel("所有人員:");
    	Userlist.setBounds(0, 55, 70, 30);
		contentPane.add(Userlist);

	    umodel = new DefaultComboBoxModel(def);
		Ulist = new JComboBox(umodel);
    	Ulist.setBounds(70, 60, 200, 20);
		ulistscroll = new JScrollPane(Ulist);
		contentPane.add(Ulist);
		
		friendbtn = new JButton("加好友");
		friendbtn.setBounds(280, 55, 80, 30);
		contentPane.add(friendbtn);
		
		Select = new JLabel("選擇進入:");
    	Select.setBounds(0, 120, 70, 30);
		contentPane.add(Select);

	    gmodel = new DefaultComboBoxModel(def);
		Glist = new JComboBox(gmodel);
    	Glist.setBounds(70, 160, 200, 20);
		glistscroll = new JScrollPane(Glist);
		contentPane.add(Glist);

	    fmodel = new DefaultComboBoxModel(def);
		Flist = new JComboBox(fmodel);
    	Flist.setBounds(70, 190, 200, 20);
		flistscroll = new JScrollPane(Flist);
		contentPane.add(Flist);

		grpbtn = new JRadioButton();
		grpbtn.setText("群組");
		grpbtn.setBounds(5, 155, 60, 30);
		grpbtn.setSelected(true);
		contentPane.add(grpbtn);

		frdbtn = new JRadioButton();
		frdbtn.setText("好友");
		frdbtn.setBounds(5, 185, 60, 30);
		contentPane.add(frdbtn);
		
		btngroup = new ButtonGroup();
		btngroup.add(grpbtn);
		btngroup.add(frdbtn);
		
		grpaffbtn = new JButton("登入");
		grpaffbtn.setBounds(280, 170, 70, 30);
		contentPane.add(grpaffbtn);
		
		exitbtn = new JButton("登出");
		exitbtn.setBounds(150, 300, 70, 30);
		contentPane.add(exitbtn);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Send_cmd(Cmd_logout);
				super.windowClosing(e);
				Login ln = new Login();
				//automatic logout
				dispose();
			}
		});

		Ulist.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				msgcut tool = new msgcut();
				System.out.println("correct check Umouse");
				String Msg = Send_cmd(Cmd_ulist);
				List<String> total = tool.get_User_List(Msg);
				System.out.println(total);
			    umodel.removeAllElements();
			    for(String item:total) {
			    		Ulist.addItem(item);
			    	}
			      if(!total.isEmpty())
				      total.clear();
				  System.out.println(Msg);
			}
		});
		Flist.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				msgcut tool = new msgcut();
				System.out.println("correct check Fmouse");
				String Msg = Send_cmd(Cmd_flist);
				List<String> frd = tool.get_Friend_List(Msg);
				System.out.println(frd);
			    fmodel.removeAllElements();
			    for(String item:frd) {
			    		Flist.addItem(item);
			    	}
				  System.out.println(Msg);
			}
		});
		Glist.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				msgcut tool = new msgcut();
				System.out.println("correct check Gmouse");
				String Msg = Send_cmd(Cmd_glist);
				List<String> grp = tool.get_Group_List(Msg);
				System.out.println(grp);
			    gmodel.removeAllElements();
			    for(String item:grp) {
			    	System.out.println(item);
			    	Glist.addItem(item);
			    	}
				  System.out.println(Msg);
			}
		});
        thehandler handler = new thehandler();
		grpcrtbtn.addActionListener(handler);
		grpaffbtn.addActionListener(handler);
		friendbtn.addActionListener(handler);
		exitbtn.addActionListener(handler);
		setVisible(true);
	}
	private class thehandler implements ActionListener{
		
		public void actionPerformed(ActionEvent event) {
			msgcut tool = new msgcut();
			if(event.getSource() == grpcrtbtn) {
				if(Gcreate.getText() != "") {
					Cmd_crt = "RequestType:R02"
							+ "|UserName:" + account
							+ "|SocketName:" + Gcreate.getText();
					String Msg = Send_cmd(Cmd_crt);
					Gcreate.setText("");
					if(Msg.contains("ResponseType:R02-0"))
						JOptionPane.showMessageDialog(null, "建立成功");
					if(Msg.contains("ResponseType:R02-1"))
						JOptionPane.showMessageDialog(null, "已存在群組");
				}
			}
			if(event.getSource() == grpaffbtn) {
				//affiliate group chat
				if(grpbtn.isSelected()) {
					Cmd_grpaff = "RequestType:R04"
							+ "|UserName:" + account
							+ "|SocketName:" + (String) Glist.getSelectedItem()
							+ "|Type:" + "T";
					String Msg = Send_cmd(Cmd_grpaff);
					JOptionPane.showMessageDialog(null, "進入群組"+(String) Glist.getSelectedItem());
				    Chatroom cr = new Chatroom(account, (String) Glist.getSelectedItem(), true, tool.get_Log(Msg));
				    dispose();
				}
				else if(frdbtn.isSelected()) {
					Cmd_grpaff = "RequestType:R04"
							+ "|UserName:" + account
							+ "|SocketName:" + (String) Flist.getSelectedItem()
							+ "|Type:" + "F";
					String Msg = Send_cmd(Cmd_grpaff);
					JOptionPane.showMessageDialog(null, "進入好友"+(String) Flist.getSelectedItem());
				    Chatroom cr = new Chatroom(account, (String) Flist.getSelectedItem(), false, tool.get_Log(Msg));
				    dispose();
				}
				}
			if(event.getSource() == friendbtn) {
				//add friend
				String[] f_id =  ((String) Ulist.getSelectedItem()).split(" :");
				Cmd_friend =  "RequestType:R03"
						+ "|UserName:"  + account
						+"|FriendName:" + f_id[0];
				System.out.println(Cmd_friend);
				String Msg = Send_cmd(Cmd_friend);
				if(Msg.contains("ResponseType:R03-0"))
					JOptionPane.showMessageDialog(null, "好友建立成功");
				if(Msg.contains("ResponseType:R03-1"))
					JOptionPane.showMessageDialog(null, "已是好友");
		   		}
			if(event.getSource() == exitbtn) {
				//logout
				Send_cmd(Cmd_logout);
				JOptionPane.showMessageDialog(null, "登出成功");
				Login lw = new Login();
				dispose();
				}
		}
	}

	public String Send_cmd(String cmd) {
		String msg = null;
		try(Socket socket = new Socket(localhost,port)) {
			BufferedReader input = new BufferedReader( new InputStreamReader(socket.getInputStream()));

	   		output  = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));   //利用sk來取得輸出串流
	   		output.write(cmd);
	   		output.newLine();
	   		output.flush();
			msg = input.readLine();
		    System.out.println(msg);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return msg;
		}
}
