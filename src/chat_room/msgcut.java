package chat_room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class msgcut {
	public static String Username;
	public static String Password;
	public static String Socketname;
	public static String Type;
	public static Boolean GType;
	public static String Secretname;
	public static String Texture;
	public static List<String> Group;
	public static List<String> Friend;
	public static List<String> User;
	public static List<String> Current;
	public static List<String> Log;
	public static final int Username_Bound = 10;
	public static final int Password_Bound = 10;
	public static final int Socket_Bound = 12;
	public static final int Friend_Bound = 12;
	public static final int Type_Bound = 6;
	public static final int Secret_Bound = 8;
	public static final int List_Bound = 6;
	public static final int Texture_Bound = 9;
	
	public static void main(String[] args) {
		String Msg ="RequestType:R04"
				+ "|UserName:" + "brian"
				+ "|SocketName:" + "group12"
				+ "|Type:" + "T";

		Msg =     "RequestType:R00"
				+ "|UserName:" + "brian"
				+ "|Password:" + "4129889";

		Msg =     "RequestType:R01"
				+ "|UserName:" + "brian"
				+ "|List:" + "Group12,Group123,gredddd";

//		Username = Msg.substring( Msg.indexOf("|UserName:") + Username_Bound, Msg.indexOf("|SocketName:"));
//		Type = get_Type(Msg);
//		get_Group_List(Msg);
		Msg =     "RequestType:C"
				+ "|UserName:" + "brian"
				+ "|SocketName:" + "group12"
				+ "|Type:" + "T"
				+ "|Secret:" + "=NULL"
				+ "|Texture:"+ "Hello world";
	}
	public String get_Username(String Msg, int rtype) {
		if(rtype == 0)
		return Msg.substring( Msg.indexOf("|UserName:") + Username_Bound, Msg.indexOf("|Password:"));
		else if(rtype == 1)
		return Msg.substring( Msg.indexOf("|UserName:") + Username_Bound, Msg.indexOf("|FriendName:"));
		else if(rtype == 2)
		return Msg.substring( Msg.indexOf("|UserName:") + Username_Bound, Msg.indexOf("|SocketName:"));
		else if(rtype == 3)
		return Msg.substring( Msg.indexOf("|UserName:") + Username_Bound, Msg.indexOf("|List:"));
		else if(rtype == 4)
			return Msg.substring( Msg.indexOf("|UserName:") + Username_Bound);
		else
			return Msg.substring( Msg.indexOf("|UserName:") + Username_Bound, Msg.indexOf("|Secret:"));
	}
	public String get_Pessword(String Msg) {
		return Msg.substring( Msg.indexOf("|Password:") + Password_Bound);
	}
	public String get_Socketname(String Msg, int rtype) {
		if(rtype == 0)
		return Msg.substring( Msg.indexOf("|SocketName:") + Socket_Bound);
		else
		return Msg.substring( Msg.indexOf("|SocketName:") + Socket_Bound, Msg.indexOf("|Type:"));
	}
	public String get_Friend(String Msg) {
		return Msg.substring( Msg.indexOf("|FriendName:") + Friend_Bound);
	}
	public String get_Type(String Msg) {
		return Msg.substring( Msg.indexOf("|Type:") + Type_Bound, Msg.indexOf("|Type:") + Type_Bound + 1);
	}
	public Boolean get_Gstate(String Msg) {
		String tmp = get_Type(Msg);
		if(tmp.equals("T")) return true;
		else return false;
	}
	public String get_Secretname(String Msg) {
		return Msg.substring( Msg.indexOf("|Secret:") + Secret_Bound, Msg.indexOf("|Texture:"));
	}
	public String get_ListString(String Msg) {
		return Msg.substring( Msg.indexOf("|List:") + List_Bound);
	}
	public String get_Texture(String Msg) {
		return Msg.substring( Msg.indexOf("|Texture:") + Texture_Bound);
	}
	public List<String> get_Group_List(String Msg){
		Group = new ArrayList<String>();
		String tmp = get_ListString(Msg);
		List<String> item = new ArrayList<String>(Arrays.asList(tmp.split(",")));
		Group.addAll(item);
		item.clear();
		return Group;
	}
	public List<String> get_Friend_List(String Msg){
		Friend = new ArrayList<String>();
		String tmp = get_ListString(Msg);
		List<String> item = new ArrayList<String>(Arrays.asList(tmp.split(",")));
		Friend.addAll(item);
		return Friend;
	}
	public List<String> get_Current_List(String Msg){
		Current = new ArrayList<String>();
		String tmp = get_ListString(Msg);
		List<String> item = new ArrayList<String>(Arrays.asList(tmp.split(",")));
		Current.addAll(item);
		return Current;
	}
	public List<String> get_User_List(String Msg){
		User = new ArrayList<String>();
		String tmp = get_ListString(Msg);
		List<String> item = new ArrayList<String>(Arrays.asList(tmp.split(",")));
		User.addAll(item);
		return User;
	}
	public List<String> get_Log(String Msg){
		Log = new ArrayList<String>();
		String tmp = get_ListString(Msg);
		List<String> item = new ArrayList<String>(Arrays.asList(tmp.split(",")));
			Log.addAll(item);
		return Log;
	}
	public String ListtoString(List<String> list) {
		String Ml = "";
		Iterator<String> it = list.iterator();
		while(it.hasNext()) {
			Ml += it.next();
			if(it.hasNext()) Ml += ",";
		}
		return Ml;
	}
	public Boolean get_Sstate(String Msg) {
		if(Msg.equals("=NULL")) return false;
		else return true;
	}
}
