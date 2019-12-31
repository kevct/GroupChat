package chat;

import java.net.*;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/*
 * To do:
 * 	make list of users
 */

public class GroupChat implements ActionListener{ 
	static ArrayList<String> arr = new ArrayList<String>();
    static String name; 
    static volatile boolean finished = false; 
    
    int port = 1024;
    MulticastSocket socket;
    String message;
    
    
    static JButton[] button = new JButton[9];
	static CardLayout cardLayout;
	static Container host;
	public static JTextArea textArea = new JTextArea("", 1, 100);
	public static JTextField[] textField = new JTextField[2];
	static DefaultListModel<String> model = new DefaultListModel<String>();
	public static JList<String> list = new JList<String>(model);
	static InetAddress group;
	public static int room = 1;
	
    public static void main(String[] args) throws Exception{ 
    	
    	ActionListener al = new GroupChat();
		
    	Font font = new Font("Courier", Font.ITALIC, 14);
    	
		cardLayout = new CardLayout();
		textArea.setFont(font);
		list.setFont(font);
		
		JScrollPane scroll = new JScrollPane (textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JFrame frame = new JFrame("Chatroom");
		host = frame.getContentPane();
		host.setLayout(cardLayout);
		
		textArea.setEditable(false);
	
		//Add JPanels	
		JPanel connect = new JPanel(new BorderLayout());
		JPanel connectText = new JPanel();
		JPanel connectButton = new JPanel(new GridLayout(4,2,10,30));
		JPanel connectButton1 = new JPanel();
		JPanel connectButton2 = new JPanel();
		JPanel connectButton3 = new JPanel();
		JPanel exitButton = new JPanel();
		
		JPanel chat = new JPanel(new BorderLayout());
		JPanel chatButton = new JPanel();
		JPanel chatButtonTop = new JPanel();
		
		JPanel errorName = new JPanel(new BorderLayout());
		JPanel errorNameButton = new JPanel();
		
		JPanel userList = new JPanel(new BorderLayout());
		JPanel userListButton = new JPanel();
		
		
		//Add Button
		connectButton1.add(new JLabel("Chatroom 1"));
		connectButton1.add(button[0] = new JButton("Connect"));
		connectButton2.add(new JLabel("Chatroom 2"));
		connectButton2.add(button[7] = new JButton("Connect"));
		connectButton3.add(new JLabel("Chatroom 3"));
		connectButton3.add(button[8] = new JButton("Connect"));
		exitButton.add(button[1] = new JButton("Exit"));
		connectText.add(new JLabel("Choose Username: "));
		connectText.add(textField[0] = new JTextField("", 20));
		connectButton.add(connectButton1);
		connectButton.add(connectButton2);
		connectButton.add(connectButton3);
		connectButton.add(exitButton);
		connect.add(connectText, BorderLayout.CENTER);
		connect.add(connectButton, BorderLayout.SOUTH);
		
		chatButton.add(button[2] = new JButton("Send"));
		chatButton.add(textField[1] = new JTextField("", 20));
		chatButtonTop.add(button[3] = new JButton("Exit"));
		chatButtonTop.add(button[5] = new JButton("Users"));
		chat.add(chatButton, BorderLayout.SOUTH);
		chat.add(chatButtonTop, BorderLayout.NORTH);
		chat.add(scroll, BorderLayout.CENTER);
		
		errorNameButton.add(button[4] = new JButton ("OK"));
		errorName.add(new JLabel("Username is already taken.", SwingConstants.CENTER), BorderLayout.NORTH);
		errorName.add(errorNameButton, BorderLayout.SOUTH);
		
		userListButton.add(button[6] = new JButton("Back"));
		userList.add(userListButton, BorderLayout.SOUTH);
		userList.add(list, BorderLayout.CENTER);
		
		
		//Add Panels to Host
		host.add("connect", connect);
		host.add("chat", chat);
		host.add("errorName", errorName);
		host.add("userList", userList);
		//Add ActionListener
		for(int i = 0; i < button.length; i++){
			button[i].addActionListener(al);
		}

		textField[0].addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					button[0].doClick();
					return;
				}
			}
		});
		textField[1].addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					button[2].doClick();
					return;
				}
			}
		});
		
		//Set Visible
		cardLayout.show(host, "connect");
		frame.pack();
		frame.setSize(800, 1200);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
    }
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == button[0]) { //Connect1
			if(!textField[0].getText().equals("")) {
	            try{ 
	            	finished = false; 
	            	room = 1;
	            	group = InetAddress.getByName("239.0.0." + room); 
	                name = textField[0].getText(); 
	            	if(checkName()) {
		            	loadFile();
		    			cardLayout.show(host, "chat");
		                saveName();
		                socket = new MulticastSocket(port); 
		              
		                // Since we are deploying 
		                socket.setTimeToLive(0); 
		                //this on localhost only (For a subnet set it as 1) 
		                socket.joinGroup(group); 
		                Thread t = new Thread(new ReadThread(socket,group,port)); 
		              
		                // Spawn a thread for reading messages 
		                t.start();  
		                 
		                textField[0].setText("");
		                System.out.println("here"); 
		                
		                String sys = "* " + name + " has joined the chatroom.\n";
		                byte[] buffer = sys.getBytes(); 
		                DatagramPacket datagram = new DatagramPacket(buffer,buffer.length,group,port); 
						socket.send(datagram);
						
						textArea.append("* Connected to Chatroom 1 as " + name + ".\n");
		                //textArea.append("* Users in chatroom: " + loadName() + ".\n");
		                
	            	}else {
		            	textField[0].setText("");
		            	cardLayout.show(host, "errorName");
	            	}
	            } 
	            catch(SocketException se) 
	            { 
	                System.out.println("Error creating socket"); 
	                se.printStackTrace(); 
	            } 
	            catch(IOException ie) 
	            { 
	                System.out.println("Error reading/writing from/to socket"); 
	                ie.printStackTrace(); 
	            } 
			}
            return;
		}
		if(e.getSource() == button[7]) { //Connect2
			if(!textField[0].getText().equals("")) {
				try{ 
					finished = false; 
					room = 2;
					group = InetAddress.getByName("239.0.0." + room); 
	                name = textField[0].getText(); 
	            	if(checkName()) {
		            	loadFile();
		    			cardLayout.show(host, "chat");
		                saveName();
		                socket = new MulticastSocket(port); 
		              
		                // Since we are deploying 
		                socket.setTimeToLive(0); 
		                //this on localhost only (For a subnet set it as 1) 
		                socket.joinGroup(group); 
		                Thread t = new Thread(new ReadThread(socket,group,port)); 
		              
		                // Spawn a thread for reading messages 
		                t.start();  
		                 
		                textField[0].setText("");
		                System.out.println("here"); 
		                
		                String sys = "* " + name + " has joined the chatroom.\n";
		                byte[] buffer = sys.getBytes(); 
		                DatagramPacket datagram = new DatagramPacket(buffer,buffer.length,group,port); 
						socket.send(datagram);
						
						textArea.append("* Connected to Chatroom 2 as " + name + ".\n");
		                //textArea.append("* Users in chatroom: " + loadName() + ".\n");
		                
	            	}else {
		            	textField[0].setText("");
		            	cardLayout.show(host, "errorName");
	            	}
	            } 
	            catch(SocketException se) 
	            { 
	                System.out.println("Error creating socket"); 
	                se.printStackTrace(); 
	            } 
	            catch(IOException ie) 
	            { 
	                System.out.println("Error reading/writing from/to socket"); 
	                ie.printStackTrace(); 
	            } 
			}
            return;
		}
		if(e.getSource() == button[8]) { //Connect3
			if(!textField[0].getText().equals("")) {
				try{ 
					finished = false; 
					room = 3;
					group = InetAddress.getByName("239.0.0." + room); 
	                name = textField[0].getText(); 
	            	if(checkName()) {
		            	loadFile();
		    			cardLayout.show(host, "chat");
		                saveName();
		                socket = new MulticastSocket(port); 
		              
		                // Since we are deploying 
		                socket.setTimeToLive(0); 
		                //this on localhost only (For a subnet set it as 1) 
		                socket.joinGroup(group); 
		                Thread t = new Thread(new ReadThread(socket,group,port)); 
		              
		                // Spawn a thread for reading messages 
		                t.start();  
		                 
		                textField[0].setText("");
		                System.out.println("here"); 
		                
		                String sys = "* " + name + " has joined the chatroom.\n";
		                byte[] buffer = sys.getBytes(); 
		                DatagramPacket datagram = new DatagramPacket(buffer,buffer.length,group,port); 
						socket.send(datagram);
						
						textArea.append("* Connected to Chatroom 3 as " + name + ".\n");
		                //textArea.append("* Users in chatroom: " + loadName() + ".\n");
		                
	            	}else {
		            	textField[0].setText("");
		            	cardLayout.show(host, "errorName");
	            	}
	            } 
	            catch(SocketException se) 
	            { 
	                System.out.println("Error creating socket"); 
	                se.printStackTrace(); 
	            } 
	            catch(IOException ie) 
	            { 
	                System.out.println("Error reading/writing from/to socket"); 
	                ie.printStackTrace(); 
	            } 
			}
            return;
		}
		if(e.getSource() == button[1]) { //Quit
			System.exit(1);
		}
		if(e.getSource() == button[2]) { //Send Message
            try {
            	if(!textField[1].getText().equals("")) {
	                message = name + ": " + textField[1].getText() + "\n"; 
	                arr.add(message);
	                byte[] buffer = message.getBytes(); 
	                DatagramPacket datagram = new DatagramPacket(buffer,buffer.length,group,port); 
					socket.send(datagram);
					System.out.println("Sent message " + message);
					saveMessage(message);
					textField[1].setText("");
            	}
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
            return;
		}
		if(e.getSource() == button[3]) { //Exit
			finished = true; 
            try {
            	String sys = "* " + name + " has left the chatroom.\n";
                byte[] buffer = sys.getBytes(); 
                DatagramPacket datagram = new DatagramPacket(buffer,buffer.length,group,port); 
				socket.send(datagram);
				
				socket.leaveGroup(GroupChat.group);
				socket.close(); 
	            System.out.println("Socket Closed.");
	            deleteName();
				cardLayout.show(host, "connect");
				textArea.setText("");
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
            return;
		}
		if(e.getSource() == button[4]) { //OK
			cardLayout.show(host, "connect");
			return;
		}
		if(e.getSource() == button[5]) { //Users
			setUserList();
			cardLayout.show(host, "userList");
			return;
		}
		if(e.getSource() == button[6]) { //Back
			cardLayout.show(host, "chat");
			return;
		}
    } 
	public void setUserList() {
		File f = new File("chatNames" + room + ".txt");
		try {
			model.removeAllElements();
			if(f.isFile()) {
				FileReader frd = new FileReader(f);
				BufferedReader bfrd = new BufferedReader(frd);
				String tmp;
				list = new JList<String>();
				while((tmp = bfrd.readLine()) != null){
					model.addElement(tmp);
				}
				bfrd.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public boolean checkName() throws IOException{
		File f = new File("chatNames" + room + ".txt");
		try {
			if(f.isFile()) {
				FileReader frd = new FileReader(f);
				BufferedReader bfrd = new BufferedReader(frd);
				String tmp;
				while((tmp = bfrd.readLine()) != null){
					if(tmp.equals(name)) {
						bfrd.close();
						return false;
					}
				}
				bfrd.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	public void deleteName() throws IOException{
		ArrayList<String> tmpName = new ArrayList<String>();
		File f = new File("chatNames" + room + ".txt");
		try {
			if(f.isFile()) {
				FileReader frd = new FileReader(f);
				BufferedReader bfrd = new BufferedReader(frd);
				String tmp;
				while((tmp = bfrd.readLine()) != null){
					if(!tmp.equals(name)) {
						tmpName.add(tmp);
					}
				}
				bfrd.close();
				//Write
				FileWriter fr = new FileWriter(f);
				BufferedWriter bfr = new BufferedWriter(fr);
				for(int i = 0; i < tmpName.size(); i++) {
					bfr.write(tmpName.get(i) + "\n");
					bfr.flush();
					System.out.println(tmpName.get(i));
				}
				bfr.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/* //deprecated
	public String loadName() throws IOException{
		File f = new File("chatNames.txt");
		String res = "NO_ONE";
		try {
			if(f.isFile()) {
				FileReader frd = new FileReader(f);
				BufferedReader bfrd = new BufferedReader(frd);
				res = "";
				String tmp;
				while((tmp = bfrd.readLine()) != null){
					res += tmp + ", ";
				}
				bfrd.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return res.substring(0, res.length() - 2);
	}
	*/
	public void saveName() throws IOException{
		try {
			File f = new File("chatNames" + room + ".txt");
			FileWriter fr;
			if(f.isFile()) {
				fr = new FileWriter(f, true);
			}else {
				fr = new FileWriter(f);
			}
			BufferedWriter bfr = new BufferedWriter(fr);
			bfr.write(name + "\n");
			bfr.flush();
			bfr.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void loadFile() throws IOException {
		File f = new File("chatLog" + room + ".txt");
		if(f.isFile()) {
			try {
				FileReader frd = new FileReader(f);
				BufferedReader bfrd = new BufferedReader(frd);
				String tmp;
				while((tmp = bfrd.readLine()) != null) {
					tmp += "\n";
					textArea.append(tmp);
				}
				bfrd.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void saveMessage(String msg) {
		try {
			File f = new File("chatLog" + room + ".txt");
			FileWriter fr;
			if(f.isFile()) {
				fr = new FileWriter(f, true);
			}else {
				fr = new FileWriter(f);
			}
			BufferedWriter bfr = new BufferedWriter(fr);
			bfr.write(msg);
			bfr.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/* //deprecated
	public void saveFile() {
		try {
			File f = new File("chatLog.txt");
			FileWriter fr;
			String end = "";
			int index = 0;
			if(!f.isFile()){
				fr = new FileWriter(f);
			}else {
				FileReader frd = new FileReader(f);
				@SuppressWarnings("resource")
				BufferedReader bfrd = new BufferedReader(frd);
				String tmp;
				while((tmp = bfrd.readLine()) != null) {
					end = tmp;
				}
				bfrd.close();
				System.out.println("end = " + end);
				index = arr.indexOf(end + "\n") + 1;
				System.out.println("index = " + index);
				fr = new FileWriter(f, true);
			}
			BufferedWriter bufWrite = new BufferedWriter(fr);
			for(int i = index; i < arr.size(); i++) {
				bufWrite.write(arr.get(i));
				bufWrite.flush();
			}
			bufWrite.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	} */
}

class ReadThread implements Runnable { 
    private MulticastSocket socket; 
    private InetAddress group; 
    private int port; 
    private static final int MAX_LEN = 1000; 
    ReadThread(MulticastSocket socket,InetAddress group,int port) { 
        this.socket = socket; 
        this.group = group; 
        this.port = port; 
    } 
    
    @Override
    public void run() { 
        while(!GroupChat.finished) { 
                byte[] buffer = new byte[ReadThread.MAX_LEN]; 
                DatagramPacket datagram = new DatagramPacket(buffer,buffer.length,group,port); 
                String message; 
            try{ 
                socket.receive(datagram); 
                message = new String(buffer,0,datagram.getLength(),"UTF-8"); 
                GroupChat.textArea.append(message);
                if(!message.substring(0, 1).equals("*")) {
	                if(GroupChat.arr.size() != 0) {
		                if(!message.equals(GroupChat.arr.get(GroupChat.arr.size() - 1))) {
		                	GroupChat.arr.add(message);
		                }
	                }else {
	                	GroupChat.arr.add(message);
	                }
                }
            } 
            catch(IOException e) { 
                System.out.println("Socket closed!"); 
            } 
        } 
    } 
}
