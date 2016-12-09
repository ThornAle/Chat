package chat.Srv;

import java.io.Serializable;

public class ChatMessage implements Serializable{

	protected static final long serialVersionUID = 1112122200L;
	
	public static final int LOGIN = 0;
	public static final int MESSAGE = 1;
	public static final int LOGOUT = 2;
	
	private int _type;
	private String _msg;
	
	public ChatMessage (int type, String msg)
	{
		_type = type;
		_msg = msg;
	}
	
	public String getMessage() {
		return _msg;
	}


	public int getType() {
		return _type;
	}

}
