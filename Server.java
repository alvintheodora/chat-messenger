import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Server extends JFrame {
	private JTextField userText;//text where user gonna type
	private JTextArea chatWindow;//window chat	
	private ObjectOutputStream output;//output stream: how to communicate between computers, from our computer to their computer, the commands get packed, and sent	
	private ObjectInputStream input;//input stream : goes from their computer to ours
	private ServerSocket server;//socket basically means connection
	private Socket connection;
	
	public Server(){
		super("Server side messenger");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						sendMessage(event.getActionCommand());
						userText.setText("");
					}
				}
		);
		add(userText,BorderLayout.SOUTH);
		
		chatWindow = new JTextArea();
		chatWindow.setEditable(false);
		add(new JScrollPane(chatWindow),BorderLayout.CENTER);
		
		setSize(300,150);
		setVisible(true);
		
		
	}
	
	//set up and run the server
	public void startRunning(){
		try{
			server = new ServerSocket(6789,100);//(port number, back log (how many people you want to be able to connect))
			while(true){
				try{
					waitForConnection();
					setupStreams();
					whileChatting();
				}catch(EOFException eofException){
					sendMessage("\n Sever ended the connection! ");
				}finally{//finally is executed both if executed does or doesnt occur
					closeCrap();//signal end of stream , close the socket,etc
				}
			}
		}catch(IOException ioException){
			ioException.printStackTrace();//see how we messed up, and we see what we did wrong
		}


}
	//wait for connection , then display connection information
	private void waitForConnection() throws IOException{
		showMessage("Waiting for someone to connect. . .");
		connection = server.accept();//listens connection to be made to this socket, then accepts it(if no connection made, then blocks the method)
		showMessage("\nNow connected to " + connection.getInetAddress().getHostName());//get the ip address
		
	}
	
	//get stream to send and receive data
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());//get stream pathway to connect to someone else
		output.flush();//flush the buffer, and send it
		input = new ObjectInputStream(connection.getInputStream());//get stream pathway for someone to send data to us
		//no need to flush
	}
	
	//during the chat conversation
	private void whileChatting() throws IOException{
		
		String message;
		message = "You are now connected!";
		sendMessage(message);
		ableToType(true);
		
		do{
			try{
				message = (String)input.readObject();
				//TO ADD, CENSORING WORDS
				message = censoreWords(message);
				
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException){//if it is not string or unidentified object we received
				chatWindow.append("\nidk what they inputted");
			}
		}while(!message.equals("CLIENT - END"));
	}
	
	
	//close stream and sockets after done chatting
	private void closeCrap(){//close to saves up memory
		showMessage("\nClosing connection...\n");
		try{
			ableToType(false);
			output.close();//close stream
			input.close();
			connection.close();//close socket
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//send a message to client
	private void sendMessage(String message){
		try{
			//TO ADD, CENSORING WORDS
			message = censoreWords(message);
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\nSERVER - " + message);
		}catch(IOException ioException){
			chatWindow.append("\nERROR: I cant send it");
		}
	}
	
	//update chatWindow
	private void showMessage(final String text){
		SwingUtilities.invokeLater(// lets you create thread; to update GUI,instead of making the new GUI, and automatically scroll down when texting
			new Runnable(){//thread
				public void run(){
					chatWindow.append(text);
				}
			}
		);
		
	}
	
	//able to type or not
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(// lets you create thread; to update GUI,instead of making the new GUI
				new Runnable(){//thread
					public void run(){
						userText.setEditable(tof);
					}
				}
			);
	}
	
	//censoring words, WARNING EXPLICIT BAD WORDS
	private static String censoreWords(String word){
		String find[] = {"fuck","shit","dick","bitch","ass"};
		String replace[] = {"****","****","****","*****","***"};
		for (int i=0;i<find.length;i++){
			word = word.replaceAll(find[i],replace[i]);
		}
		return word;
	}
	
}
