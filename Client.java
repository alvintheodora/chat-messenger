import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Client extends JFrame {
	private JTextField userText;//text where user gonna type
	private JTextArea chatWindow;//window chat	
	private ObjectOutputStream output;//output stream: how to communicate between computers, from our computer to their computer, the commands get packed, and sent	
	private ObjectInputStream input;//input stream : goes from their computer to ours
	private String serverIP;//new
	private Socket connection;
	private String message;
	
	public Client(String host){//new
		super("Client Side Messenger");
		serverIP = host;//new
		
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener() {			
				public void actionPerformed(ActionEvent event) {
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}	
		);
		add(userText,BorderLayout.SOUTH);
		
		chatWindow = new JTextArea();
		chatWindow.setEditable(false);
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		
		setSize(300,150);
		setVisible(true);
	}
	
	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();			

		}catch(EOFException eofException){
			showMessage("client terminated connection!");
		}catch(IOException ioException){//new
			ioException.printStackTrace();
		}
		finally{
			closeCrap();
		}
	}
	
	
	private void connectToServer() throws IOException{
		//1. ip address
		//2. port number
		showMessage("Attempting connection...");
		connection = new Socket(InetAddress.getByName(serverIP),6789);//serverIP passed on the constructor parameter
		showMessage("\nConnected to " + connection.getInetAddress().getHostName());
	}
	
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
	}
	
	private void whileChatting() throws IOException{	
		ableToType(true);
		do{
			try{
				message = (String)input.readObject();
				//TO ADD, CENSORING WORDS
				message = censoreWords(message);
				showMessage("\n" + message);//BEDA
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\nIDK I cant read what they sent");
			}

		}while(!message.equals("SERVER - END"));
	}
	
	private void closeCrap(){
		showMessage("\nclosing connection...");
		ableToType(false);
		try{
			
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	private void sendMessage(String message){
		try{
			//TO ADD, CENSORING WORDS
			message = censoreWords(message);
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\nCLIENT - " + message);
		}catch(IOException ioException){
			chatWindow.append("\nIDK WHAT U TYPE");
		}
	}
	
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(text);
				}
			}
				
		);	
		
	}
	
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						userText.setEditable(tof);;
					}
				}
		);
		
	}
	
	//censoring words, WARNING EXPLICIT BAD WORDS!
	private static String censoreWords(String word){
		String find[] = {"fuck","shit","dick","bitch","ass"};
		String replace[] = {"****","****","****","*****","***"};
		for (int i=0;i<find.length;i++){
			word = word.replaceAll(find[i],replace[i]);
		}
		return word;
	}
	
}
