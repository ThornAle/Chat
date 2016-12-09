package chat.cli;

import java.net.*;
import java.util.Scanner;

import chat.GUI.ClientGui;
import chat.Srv.ChatMessage;

import java.io.*;

public class Client 
{
	private boolean _active;
	private int _port;
	private String _host;
	private Socket _socket;
	private String _nick;
	private ObjectInputStream _input;
	private ObjectOutputStream _output;
	private ClientGui _cg;
	
	public Client(String host, int port, String nick, ClientGui cg)
	{
		_port = port;
		_host = host;
		_nick = nick;
		_cg = cg;
		_active = false;
	}
	
	public Client(String host, int port, String nick)
	{
		this(host, port, nick, null);
	}
	

	public boolean start ()
	{
		System.out.println("Create Socket, input, output");
		try{
			//InetAddress ia = new InetAddress(_host);
			_socket = new Socket(_host, _port);
			//_socket = new InetSocketAddress(_host, _port);
			System.out.println("Socket OK");
			_input = new ObjectInputStream(_socket.getInputStream());
			System.out.println("Input OK");
			_output = new ObjectOutputStream(_socket.getOutputStream());
			System.out.println("Output OK");
		}catch(Exception e)
		{e.printStackTrace();
		return false;}
		//thread that receive message from server
		System.out.println("Create Socket, input, output OK");
		System.out.println("Start new thread for listening");
		new ListenFrom().start();
		System.out.println("Connection ...");
		try{
			_output.writeObject(new ChatMessage(ChatMessage.LOGIN, "_nick"));
			System.out.println("Logged in");
		}catch (IOException e)
		{e.printStackTrace();
		return false;}
		
		return true;
		
		
	}
	
	public void sendMessage (ChatMessage cm)
	{
		try{
			_output.writeObject(cm);
		}catch(IOException e)
		{e.printStackTrace();}
	}
	
	public void disconnect()
	{
		try{
		sendMessage(new ChatMessage(ChatMessage.LOGOUT,""));
		if(_socket != null)
			_socket.close();
		if(_input != null)
			_input.close();
		if(_output != null)
			_output.close();
		}catch(IOException e)
		{e.printStackTrace();}
	}
	
	private void display (String msg)
	{
		System.out.println(msg);
	}
	
	class ListenFrom extends Thread
	{
		public void run()
		{
			System.out.println("New thread started");
			while(true)
			{
				try{
					String msg = (String) _input.readObject();
					display(msg);
				}catch(Exception e)
				{e.printStackTrace();}
			}
		}
	}
	
	public static void main (String [] args)
	{
		int port = 1664;
		String host = "127.0.0.1";
		System.out.println("New client");
		Client c = new Client(host, port, "leo");
		System.out.println("New client OK");
		if (!c.start())
			return;
		Scanner sc = new Scanner(System.in);
		System.out.println("Scanner OK");
		while(true)
		{
			System.out.print(">");
			String msg = sc.nextLine();
			if(msg.equalsIgnoreCase("LOGOUT")) 
			{
				c.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				break;
			}
			else
			{
				c.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
			}
		}
		c.disconnect();
		
	}
}
