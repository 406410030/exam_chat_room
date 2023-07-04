package chat_room;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;

import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Login extends JFrame{

    private JPanel contentPane;
	private JLabel ACCOUNT, PASSWORD;
	private JTextField accountf;
	private JPasswordField passwordf;
	private JButton btnlogin, btnnone;
	private String localhost = "127.0.0.1";
	private int port = 6060;
	private BufferedWriter output;
	
	public Login() {
		
		super("chat room login");//�]�w�W��
		setLayout(new FlowLayout());

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 280, 200);
		contentPane = new JPanel();
		setContentPane(contentPane);
		
    	ACCOUNT = new JLabel("�b��:");
		contentPane.add(ACCOUNT);
		
		accountf = new JTextField("",20);
		contentPane.add(accountf);
		
		PASSWORD = new JLabel("�K�X:");
		contentPane.add(PASSWORD);
		
		passwordf = new JPasswordField(20);
		contentPane.add(passwordf);

		btnnone = new JButton("�إ߱b��");
        contentPane.add(btnnone);
        
		btnlogin = new JButton("�n�J");
        contentPane.add(btnlogin);
        
        thehandler handler = new thehandler();
		btnnone.addActionListener(handler);
		btnlogin.addActionListener(handler);
        setVisible(true);
	}
	private class thehandler implements ActionListener{
		
		public void actionPerformed(ActionEvent event) {
			
			if(event.getSource() == btnnone) {
					Create_account set = new Create_account();
				}
			
			if(event.getSource() == btnlogin) {
				String acc = accountf.getText();
				String pas = String.valueOf(passwordf.getPassword());
				String msg = "";
				//��server��H���b�K�^��
				try(Socket socket = new Socket(localhost,port)) {
					String Cmd ="RequestType:R01"
							+ "|UserName:" + acc
							+ "|Password:" + pas;
					socket.setKeepAlive(true);
					BufferedReader input = new BufferedReader( new InputStreamReader(socket.getInputStream()));

			   		output  = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));   //�Q��sk�Ө��o��X��y
			   		socket.isConnected();
			   		output.write(Cmd);
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
				
				if(msg.contains("ResponseType:R01-0")) {
					
					JOptionPane.showMessageDialog(null, "�L���b��");
					accountf.setText("");
					passwordf.setText("");
				}
				
				else if(msg.contains("ResponseType:R01-1")) {
					JOptionPane.showMessageDialog(null, "�K�X��J���~");
					accountf.setText("");
					passwordf.setText("");
			}
				else {
					JOptionPane.showMessageDialog(null, "�n�J���\");
				    Chatlist cl = new Chatlist(acc);
				    dispose();
				}
			}
		}
	}

}


