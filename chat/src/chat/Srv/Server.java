package chat.Srv;

import java.net.*;
import java.util.ArrayList;
import java.io.*;
import chat.GUI.ServerGui;


//Manage the server
public class Server
{
	//Connection port
	private int _port;
	
	//Server Socket
	private ServerSocket _sok;
	
	//ID for clients
	public static int _UID=0;
	
	//Connected clients
	private ArrayList<ClientThread> _clients;
	
	//manage server loop
	private boolean _keepGoing;
	
	//Reference to GUI Object
	private ServerGui _sg;
	
	
	public Server (int port, ServerGui sg)
	{
		_sg = sg;
		_port = port;
		_clients = new ArrayList<ClientThread>();
	}
	
	public Server(int port) {
		//this._sok = cli;
		_port = port;
	}
	
	//start the server
	public void start()
	{
		_keepGoing = true;
		try{
			_sok = new ServerSocket(_port);
			System.out.println("Servver started");
			while(_keepGoing)
			{
				//accept a new client
				Socket s = _sok.accept();
				System.out.println("New client connected");
				if(!_keepGoing)
					break;
				//Connection from new client, add to list of clients, start new thread
				ClientThread tt = new ClientThread(s);
				_clients.add(tt);
				//inialize new client's thread and call client's function run
				tt.start();
			}
			
			//closing the server
			try{
				_sok.close();
				for (ClientThread tc : _clients)
				{
					tc.closeInput();
					tc.closeOutput();
					tc.closeSocket();
				}
			}catch(IOException e){e.printStackTrace();}
		}catch(Exception e){e.printStackTrace();}
	}
	
	//display a message on the console
	public void display (String msg)
	{
		System.out.println(msg);
	}
	
	//Broadcast message to all connected clients
	public synchronized void broadCast(String msg)
	{
		for (ClientThread tc : _clients)
		{
			tc.send(msg);
		}
	}
	
	//when a client logs off
	synchronized void remove (int iD)
	{
		for (int i = 0; i < _clients.size(); ++i)
		{
			ClientThread tc = _clients.get(i);
			if (tc.getID() == iD)
				_clients.remove(i);
		}
	}
	
	public boolean getStatus()
	{ return _keepGoing; }
	public void setStatus()
	{ _keepGoing = false;}
	
	public static void main(String [] args)
	{
		int port = 1664;
		
		Server s = new Server(port);
		System.out.println("Starting Server");
		s.start();
	}
	
	class ClientThread extends Thread 
	{
		//Unique ID for each client
		private int _ID;
		
		//Client's Socket
		private Socket _socket;
		
		//Read message from client
		private ObjectInputStream _input;
		
		//Write message to client
		private ObjectOutputStream _output;
		
		//Client's nickname
		private String _nick;
		
		//Object that manages communication protocol
		private ChatMessage _cm;


		public ClientThread(Socket s) {
			_ID = ++_UID;//generate new ID for the client
			_socket = s;
			try{
				_input = new ObjectInputStream(s.getInputStream());
				_output = new ObjectOutputStream(s.getOutputStream());
				_nick = (String) _input.readObject();
				display(_nick + " joined the room");
			}catch(Exception e)
			{e.printStackTrace();}
		}

		//Managing loop for the client
		public void run() 
		{
			/*read a ChatMessage Object
			  parse ChatMessage according to protocol :
			  - LOGIN (Sting)PSEUDO
			  - MESSAGE (String)CONTENT
			  - LOGOUT
			  broadcast Message to all other users  
			*/
			boolean cont = true;
			
			while (cont)
			{
				try {
					//Receive a new message (ChatMessage object)
					_cm = (ChatMessage) _input.readObject();
				} catch (ClassNotFoundException | IOException e) 
				{e.printStackTrace();}
				String msg = _cm.getMessage();
				switch(_cm.getType())
				{
					case ChatMessage.LOGIN : //It is a new connection, we broadcast its nickname to all connected users
						broadCast(_nick + " joined the room");
						display(_nick + " joined the room");
					break;
					
					case ChatMessage.MESSAGE : //It is a message, we broadcast it to all connected users
						broadCast(_nick + " : " + msg);
						display(_nick + " : " + msg);
					break;
					
					case ChatMessage.LOGOUT : //The user wishes to quit the chatroom
						broadCast(_nick + " exit the room");
						display(_nick + " exit the room");
						cont = false;
					break;
				}
				
			}
			//Remove itself from the list of clients
			remove(_ID);
			
			//close connection
			close();
			
		}
		
		//try to close connection (clean logout)
		private void close()
		{
			closeInput();
			closeOutput();
			closeSocket();
		}
		
		//Send a String
		public void send(String msg) 
		{
			if(!_socket.isConnected())
				close();
			try{
				_output.writeObject(msg);
			}catch(IOException e)
			{e.printStackTrace();}
		}

		public void closeInput() 
		{
			try {
				_input.close();
			} catch (IOException e) 
			{e.printStackTrace();}	
		}

		public void closeSocket() {
			try {
				_socket.close();
			} catch (IOException e) 
			{e.printStackTrace();}	
		}


		public void closeOutput() {
			try {
				_output.close();
			} catch (IOException e) 
			{e.printStackTrace();}	
		}




		public int getID()
		{
			return _ID;
		}
		
	}
	
}
