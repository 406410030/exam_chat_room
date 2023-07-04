package chat_room;

import java.awt.FlowLayout;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Create_account extends JFrame{

    private JPanel contentPane;
	private JLabel ACCOUNT, PASSWORD, PASSWORD2;
	private JTextField accountf;
	private JPasswordField passwordf, password2f;
	private JButton btncreate;
	private String localhost = "127.0.0.1";
	private int port = 6060;
	private BufferedWriter output;
	
	public Create_account() {
		
		super("create account");//�]�w�W��
		setLayout(new FlowLayout());

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 300, 200);
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
		
		PASSWORD2 = new JLabel("�A����J�K�X:");
		contentPane.add(PASSWORD2);
		
		password2f = new JPasswordField(20);
		contentPane.add(password2f);
		
		btncreate = new JButton("�إ߱b��");
        contentPane.add(btncreate);
        
        thehandler handler = new thehandler();

		btncreate.addActionListener(handler);
        
        setVisible(true);
	}
	private class thehandler implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			boolean same = false;
			String acc = accountf.getText();
			String pas = String.valueOf(passwordf.getPassword());
			String pas2 = String.valueOf(password2f.getPassword());
			if(!pas.equals(pas2)) {
				JOptionPane.showMessageDialog(null, "�A���T�{�K�X��J���~");
				accountf.setText("");
				passwordf.setText("");
				password2f.setText("");
			}
			else {
				String msg = null;
					//�i�JSERVER�M��b���O�_����
				try(Socket socket = new Socket(localhost,port)) {
					String Cmd ="RequestType:R00"
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
					//�S�����ƴN�������U
				if(msg.contains("ResponseType:R00-1")) {
					JOptionPane.showMessageDialog(null, "�b������");
					accountf.setText("");
					passwordf.setText("");
					password2f.setText("");
				}
				if(msg.contains("ResponseType:R00-0")){
					JOptionPane.showMessageDialog(null, "�إߦ��\");
					dispose();
				}
			}
		}
	}

}

