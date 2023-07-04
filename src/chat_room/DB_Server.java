package chat_room;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import java.util.List;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class DB_Server {
	public static MongoClient mongo = null;
	public static MongoDatabase db = null;
	
	public void connect_DB() {
		
	mongo  = new MongoClient("localhost", 27017);
	System.out.println("connection to "+mongo.getConnectPoint());
	db = mongo.getDatabase("chatroomDB");
	
	}
	
	public void create_collection(){
    
	try {
		
		if(db.getCollection("user") == null)
		db.createCollection("user");
		if(db.getCollection("groupinfo") == null)
		db.createCollection("groupinfo");
		
	}catch (MongoException me) {
        System.err.println("Unable due to an error: " + me);
    }
	}

	public void u_insert(String acc, String pass){
			MongoCollection<Document> collection = db.getCollection("user");
			Document document = new Document().append("account", acc)
						  .append("password", pass)
						  .append("state",false)
						  .append("group", "")
						  .append("friend",Arrays.asList());

			System.out.println(document);
			collection.insertOne(document);
	}

	public void u_aff(String name, String acc){
	    
		MongoCollection<Document> collection = db.getCollection("user");
		Bson filter = Filters.eq("account", acc);
		Bson update = Updates.set("group", name);
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
		                                    .returnDocument(ReturnDocument.AFTER);

        Document result = collection.findOneAndUpdate(filter, update, options);
        System.out.println(result);
	}
	
	public void u_disaff(String name, String acc){
	    
		MongoCollection<Document> collection = db.getCollection("user");
		Bson filter = Filters.eq("account", acc);
		Bson update = Updates.set("group", "");
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
		                                    .returnDocument(ReturnDocument.AFTER);

        Document result = collection.findOneAndUpdate(filter, update, options);
        System.out.println(result);
        
	}
	
	public void u_friend(String friend, String me){
    
	MongoCollection<Document> collection = db.getCollection("user");
	Bson filter = Filters.eq("account", me);

    Document document = new Document().append("f_id", friend)
			  	.append("log", Arrays.asList());
	
	Bson update = Updates.push("friend",document);
	FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
	                                    .returnDocument(ReturnDocument.AFTER);

    Document result = collection.findOneAndUpdate(filter, update, options);
    
    System.out.println(result);
    
	filter = Filters.eq("account", friend);
	
    document = new Document().append("f_id", me)
		  	.append("log", Arrays.asList());
	update = Updates.push("friend", document);
	options = new FindOneAndUpdateOptions()
	                                    .returnDocument(ReturnDocument.AFTER);

    result = collection.findOneAndUpdate(filter, update, options);
    System.out.println(result);
	}
	
	public int u_check(String acc, String pass){
	    int state = 0;
		MongoCollection<Document> collection = db.getCollection("user");
		Bson filter = Filters.eq("account", acc);
		FindIterable<Document> Doc = collection.find(filter);
		MongoCursor<Document> cursor = Doc.iterator();
		while(cursor.hasNext()) {
			Document info = cursor.next();
			if(info.getString("account").equals(acc)) {
				state = 1;
				System.out.println("correct account");
				if(info.get("password").equals(pass)) {
					state = 2;
					System.out.println("correct password");
					}
			}
		}
		return state;
	}
	public void u_login(String acc){
	    
		MongoCollection<Document> collection = db.getCollection("user");
		Bson filter = Filters.eq("account", acc);
		Bson update = Updates.set("state", true);
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
		                                    .returnDocument(ReturnDocument.AFTER);

        Document result = collection.findOneAndUpdate(filter, update, options);
        System.out.println(result);
}
	public void u_logout(String acc){
	    
		MongoCollection<Document> collection = db.getCollection("user");
		Bson filter = Filters.eq("account", acc);
		Bson update = Updates.set("state", false);
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
                .returnDocument(ReturnDocument.AFTER);
		Document result = collection.findOneAndUpdate(filter, update, options);
        System.out.println(result);
}
	public void u_get() {
		MongoCollection<Document> collection = db.getCollection("user");
		FindIterable<Document> Doc = collection.find();
		Document document = Doc.first();
		MongoCursor<Document> cursor = Doc.iterator();
		while (cursor.hasNext()) {
		    System.out.println(cursor.next());
		}
		
		System.out.println(document.get("account"));
	}
	
	public void g_insert(String name){
    
			MongoCollection<Document> collection = db.getCollection("groupinfo");
			Document document = new Document().append("g_id", name)
											.append("member", Arrays.asList())
											.append("chatlog", Arrays.asList());

			System.out.println(document);
			collection.insertOne(document);
		    
	}

	public List<String> show_friend_list(String acc){
		MongoCollection<Document> collection = db.getCollection("user");
		Bson filter = Filters.eq("account", acc);
		FindIterable<Document> Doc = collection.find(filter);
		MongoCursor<Document> cursor = Doc.iterator();
		List<String> flist = new ArrayList<String>();
		while(cursor.hasNext()) {
			Document info = cursor.next();
			if(info.get("account").equals(acc)) {
				List<Document> friendDoc= info.getList("friend", Document.class);
				for(Document friend:friendDoc)
					flist.add(friend.getString("f_id"));
				System.out.println(flist);
				return flist;
			}
		}
		return flist;
	}

	public List<String> show_group_list(){
		MongoCollection<Document> collection = db.getCollection("groupinfo");
		FindIterable<Document> Doc = collection.find();
		MongoCursor<Document> cursor = Doc.iterator();
		List<String> glist = new ArrayList<String>();
		while (cursor.hasNext()) {
			Document info = cursor.next();
			glist.add(info.getString("g_id"));
		}
		System.out.println(glist);
		return glist;
	}

	public List<String> show_current_list(String gid){
		MongoCollection<Document> collection = db.getCollection("groupinfo");
		Bson filter = Filters.eq("g_id", gid);
		FindIterable<Document> Doc = collection.find(filter);
		MongoCursor<Document> cursor = Doc.iterator();
		List<String> clist = new ArrayList<String>();
		while(cursor.hasNext()) {
			Document info = cursor.next();
			if(info.get("g_id").equals(gid)) {
				clist = info.getList("member", String.class);
				System.out.println(info.get("g_id") + ":" + clist);
			}
		}
		return clist;
	}

	public List<String> show_user_list(){
		MongoCollection<Document> collection = db.getCollection("user");
		FindIterable<Document> Doc = collection.find();
		MongoCursor<Document> cursor = Doc.iterator();
		List<String> ulist = new ArrayList<String>();
		while (cursor.hasNext()) {
			Document info = cursor.next();
			ulist.add(info.getString("account") + " : " + info.getString("group"));
		}
		System.out.println(ulist);
		return ulist;
	}
	public List<String> g_aff(String name, String acc){
	    
		MongoCollection<Document> collection = db.getCollection("groupinfo");
		Bson filter = Filters.eq("g_id", name);
		Bson update = Updates.push("member", acc);
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
		                                    .returnDocument(ReturnDocument.AFTER);
		
        Document result = collection.findOneAndUpdate(filter, update, options);
        System.out.println(result);
        
        filter = Filters.eq("g_id", name);
		FindIterable<Document> Doc = collection.find(filter);
		MongoCursor<Document> cursor = Doc.iterator();
		List<String> log = new ArrayList<String>();
		while(cursor.hasNext()) {
			Document info = cursor.next();
			if(info.getString("g_id").equals(name))
			log = info.getList("chatlog", String.class);
		}
        return log;
	}
	
	public void g_disaff(String name, String acc){

		MongoCollection<Document> collection = db.getCollection("groupinfo");
		Bson filter = Filters.eq("g_id", name);
		Bson update = Updates.pull("member", acc);
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
		                                    .returnDocument(ReturnDocument.AFTER);
		
        Document result = collection.findOneAndUpdate(filter, update, options);
        System.out.println(result);
	}

	public void log_update(String name,String acc, String log, Boolean state){

		if(state) {
			MongoCollection<Document> collection = db.getCollection("groupinfo");
			Bson filter = Filters.eq("g_id", name);
			Bson update = Updates.push("chatlog", log);
			FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
			                                    .returnDocument(ReturnDocument.AFTER);
			
	        Document result = collection.findOneAndUpdate(filter, update, options);
	        System.out.println(result);
		}
		else {

			MongoCollection<Document> collection = db.getCollection("user");
			Bson filter = Filters.and(Filters.eq("account", name), Filters.eq("friend.f_id", acc));
			Bson update = Updates.push("friend.$.log", log);
			FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
			                                    .returnDocument(ReturnDocument.AFTER);
			
	        Document result = collection.findOneAndUpdate(filter, update, options);
	        System.out.println(result);

			collection = db.getCollection("user");
			filter = Filters.and(Filters.eq("account", acc), Filters.eq("friend.f_id", name));
			update = Updates.push("friend.$.log", log);
			options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
			
	        result = collection.findOneAndUpdate(filter, update, options);
	        System.out.println(result);
		}
	}

	public Boolean g_check(String id){
		MongoCollection<Document> collection = db.getCollection("groupinfo");
		Bson filter = Filters.eq("g_id", id);
		FindIterable<Document> Doc = collection.find(filter);
		MongoCursor<Document> cursor = Doc.iterator();
		while(cursor.hasNext()) {
			Document info = cursor.next();
			if(info.getString("g_id").equals(id))
			return false;
		}
		return true;
	}
	
	public Boolean f_check(String id, String fid){
		MongoCollection<Document> collection = db.getCollection("user");
		Bson filter = Filters.and(Filters.eq("account", id),Filters.eq("friend.f_id", fid));
		FindIterable<Document> Doc = collection.find(filter);
		MongoCursor<Document> cursor = Doc.iterator();
		while(cursor.hasNext()) {
			Document info = cursor.next();
			if(info.getString("friend.f_id").equals(fid))
			return false;
		}
		return true;
	}

	public List<String> f_aff(String name, String acc){
	    
		MongoCollection<Document> collection = db.getCollection("user");
		Bson filter = Filters.eq("account", acc);
		Bson update = Updates.set("group", name);
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
		                                    .returnDocument(ReturnDocument.AFTER);
		
        Document result = collection.findOneAndUpdate(filter, update, options);
        System.out.println(result);
        
		FindIterable<Document> Doc = collection.find(filter);
		MongoCursor<Document> cursor = Doc.iterator();
		List<String> log = new ArrayList<String>();
		while(cursor.hasNext()) {
			Document info = cursor.next();
			if(info.getString("account").equals(acc)) {
				List<Document> friendDoc= info.getList("friend", Document.class);
				for(Document friend:friendDoc) {
					if(friend.getString("f_id").equals(name))
						log = friend.getList("log", String.class);
				}
			}
		}
	    return log;
	}
	
	public void DB_CLOSE(){
	mongo.close();
	System.out.println("disconnected");
	}
	public DB_Server() {
		connect_DB();
		create_collection();
	}
/*	public static void main(String[] args) throws InterruptedException {
		DB_Server.connect_DB();
//		DB_Server.delete_collection();
	//	DB_Server.create_collection();
		//DB_Server.uinsert("brian", "4129889");
	//	if(DB_Server.ucheck("brian","4129889") == 2) {
		//DB_Server.u_login("brian");
//		ArrayList<String> list = DB_Server.show_user_list();
		DB_Server.u_logout("brian");
//		}
//		DB_Server.gcheck("brian","group01");
//		DB_Server.u_aff("group","group01","brian");
//		DB_Server.uinsert("brian", "4129889");
		DB_Server.DB_CLOSE();
	}*/

	private void delete_collection() {

	      db.getCollection("user").drop();
	      db.getCollection("groupinfo").drop();
	      System.out.println("Collection dropped successfully");
	}
}
