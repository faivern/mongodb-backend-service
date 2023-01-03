package mongo.restaurant;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.delete;
import com.mongodb.client.model.Filters;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
import java.util.Properties;
//import java.util.stream.Collectors;

import org.apache.commons.text.WordUtils;
import org.bson.Document;
//import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
//import com.mongodb.client.ClientSession;
//import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
//import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
//import com.mongodb.client.result.DeleteResult;

public class RestBackend {

	public static void main(String[] args) throws IOException {
		
		String connString;
		Logger logger = LoggerFactory.getLogger(RestBackend.class);
		InputStream input = new FileInputStream("connection.properties");

			Properties prop = new Properties();
			prop.load(input);
			connString = prop.getProperty("db.connection_string");
			logger.info(connString);


			ConnectionString connectionString = new ConnectionString(connString);
			MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
			MongoClient mongoClient = MongoClients.create(settings);
			MongoDatabase database = mongoClient.getDatabase(prop.getProperty("db.name"));
			logger.info(prop.getProperty("db.name"));

			
			//--------------------------------------------------------------------------------------- --------------------------------------------------------------------------------------------------------
			
			get("/title/:title", (req,res)->{
				MongoCollection<Document> collection = database.getCollection("metflix");
				String filter=req.params("title").toLowerCase();
				filter=WordUtils.capitalizeFully(filter);
				logger.info("Filtering titles for title: " + filter);
				Document myDoc = collection.find(Filters.eq("title", filter)).first();
				if (myDoc != null) {
					myDoc.remove("_id");
					myDoc.remove("poster");
					myDoc.remove("cast");
					myDoc.remove("fullplot");
					
					
				} else {
					res.status(404);
	    		  return ("<html><body><h1>Title not found.</h1></body></html>");
				}
				return myDoc.toJson();
			});
			
			//--------------------------------------------------------------------------------------- --------------------------------------------------------------------------------------------------------

			get("/fullplot/:title", (req,res)->{
				MongoCollection<Document> collection = database.getCollection("metflix");
				String filter=req.params("title").toLowerCase();
				filter=WordUtils.capitalizeFully(filter);
				logger.info("Filtering titles for title: " + filter);
				Document myDoc = collection.find(Filters.eq("title", filter)).projection(Projections.include("title","fullplot")).first();
				if (myDoc != null) {
					myDoc.remove("_id");
	
				} else {
					res.status(404);
	    		  return ("<html><body><h1>Title not found.</h1></body></html>");
				}
				return myDoc.toJson();
			});
			
			//--------------------------------------------------------------------------------------- --------------------------------------------------------------------------------------------------------


			get("/cast/:title", (req,res)->{
				MongoCollection<Document> collection = database.getCollection("metflix");
				String filter=req.params("title").toLowerCase();
				filter=WordUtils.capitalizeFully(filter);
				logger.info("Filtering titles for title: " + filter);
				Document myDoc = collection.find(Filters.eq("title", filter)).projection(Projections.include("title", "cast")).first();
				
				if (myDoc != null) {
					myDoc.remove("_id");
					
				} else {
					res.status(404);
	    		  return ("<html><body><h1>Title not found.</h1></body></html>");
				}
				return myDoc.toJson();
			});
			
			//--------------------------------------------------------------------------------------- --------------------------------------------------------------------------------------------------------

			get("/genre/:genre", (req,res)->{ 
				MongoCollection<Document> collection = database.getCollection("metflix"); 
				String filter=req.params("genre").toLowerCase();
				int limit=10;
				if (req.queryParams("limit")!=null) {
					try {
						limit=Integer.parseInt(req.queryParams("limit"));
					} catch (NumberFormatException e) { }
				}
				filter=WordUtils.capitalizeFully(filter);
				logger.info("Finding movies with genre: " + filter);
				MongoCursor<Document> cursor = collection.find(Filters.eq("genres", filter)).limit(limit).iterator();
				JsonArray ja=new JsonArray();
				Document myDoc;
				
				if(!cursor.hasNext()) {
					res.status(404);
					return ("<html><body><h1>Genre does not exist.</h1></body></html>");
				}
				
				try {
					while (cursor.hasNext()) {
						myDoc=cursor.next();
						myDoc.remove("_id");
						myDoc.remove("cast");
						myDoc.remove("fullplot");
						myDoc.remove("poster");
						ja.add(myDoc.toJson());
					}
				} finally {
					cursor.close();
				}
				JsonObject response=new JsonObject();
				response.add( "Movies: ", ja );
				return response;
			});
			
			//--------------------------------------------------------------------------------------- --------------------------------------------------------------------------------------------------------

			get("/actor/:actor", (req,res)->{
				MongoCollection<Document> collection = database.getCollection("metflix");
				
				String filter=req.params("actor").toLowerCase();
				int limit=10;
				if (req.queryParams("limit")!=null) {
					try {
						limit=Integer.parseInt(req.queryParams("limit"));
					} catch (NumberFormatException e) { }
				}
				filter=WordUtils.capitalizeFully(filter);
				logger.info("Finding movies with actor: " + filter);
				MongoCursor<Document> cursor = collection.find(Filters.eq("cast", filter)).projection(Projections.include("title")).limit(limit).iterator();
				JsonArray ja=new JsonArray();
				Document myDoc;
				
				if(!cursor.hasNext()) {
					res.status(404);
					return ("<html><body><h1>Actor does not exist.</h1></body></html>");
				}
				
				try {
					while (cursor.hasNext()) {
						myDoc=cursor.next();
						myDoc.remove("_id");
						
						ja.add(myDoc.toJson());
					}
				} finally {
					cursor.close();
				}
				JsonObject response=new JsonObject();
				response.add("Movies: ", ja);
				return response;
			});
			
			//--------------------------------------------------------------------------------------- --------------------------------------------------------------------------------------------------------

			get("/image/:title", (req,res)->{
				MongoCollection<Document> collection = database.getCollection("metflix");
				String filter=req.params("title").toLowerCase();
				
				filter=WordUtils.capitalizeFully(filter);
				logger.info("Filtering titles for title: " + filter);
				Document myDoc = collection.find(Filters.eq("title", filter)).projection(Projections.include("title", "poster")).first();
				if (myDoc != null) {
					myDoc.remove("_id");
						
				} else {
					res.status(404);
					return ("<html><body><h1>Title not found. </h1></body></html>");
				}
				return myDoc.toJson();
			});
			
			//--------------------------------------------------------------------------------------- --------------------------------------------------------------------------------------------------------

			 delete("/title/:title", (req, res) -> {
				 
				MongoCollection<Document> collection = database.getCollection("metflix");
				String filter=req.params("title").toLowerCase();
				
				filter=WordUtils.capitalizeFully(filter);
				logger.info("Filtering titles for title: " + filter);
				Document myDoc = collection.find(Filters.eq("title", filter)).first();
				if (myDoc != null) {
					collection.deleteOne(Filters.eq("title", filter));
					
				} else {
					res.status(409);
	    		  return ("<html><body><h1> Title not found. </h1></body></html>");
				}
				
				res.status(204);
				return (" ");
			});
			
			//--------------------------------------------------------------------------------------- --------------------------------------------------------------------------------------------------------
			 post("/title", (req, res) -> {
				 res.type("application/json");
				 try {
					 MongoCollection<Document> collection = database.getCollection("metflix");
					 collection.insertOne(new Document(Document.parse(req.body())));
					 logger.info("Adding movie ");
					 	 
				 } catch (MongoException me) {
					 res.status(409);
					 System.err.println("Unable to insert due to an error: " + me);
				 }
				 
				 res.status(202);
				 return (" ");
			 });
			 
			
			//--------------------------------------------------------------------------------------- --------------------------------------------------------------------------------------------------------

			 put("/title/:title", (req, res) -> {
				 
				MongoCollection<Document> collection = database.getCollection("metflix");
				String filter=req.params("title").toLowerCase();
			
				filter=WordUtils.capitalizeFully(filter);
				logger.info("Filtering titles for title: " + filter);
				Document myDoc = collection.find(Filters.eq("title", filter)).first();
				if (myDoc != null) {
			
					   collection.replaceOne(Filters.eq("title", filter), (Document.parse(req.body())));
				} else {
					res.status(409);
	    		  return ("<html><body><h1> Title not found. </h1></body></html>");
				}
				
				
				res.status(202);
				return (" ");
			});
			 
			//--------------------------------------------------------------------------------------- --------------------------------------------------------------------------------------------------------
			
			get("/similar/:title", (req,res)->{ 
				MongoCollection<Document> collection = database.getCollection("metflix");
				String filter=req.params("title").toLowerCase();
				int limit=10;
				if (req.queryParams("limit")!=null) {
					try {
						limit=Integer.parseInt(req.queryParams("limit"));
					} catch (NumberFormatException e) { }
				}
				
				filter=WordUtils.capitalizeFully(filter);
				logger.info("Finding similar movies: " + filter);
				Document movieDoc = collection.find(Filters.eq("title", filter)).projection(Projections.include("genres", "title")).first();
				
				if(movieDoc==null) {
					res.status(404);
					return ("<html><body><h1>Title not found.</h1></body></html>");
				}
				
				MongoCursor<Document> cursor = collection.find(Filters.all("genres", movieDoc.get("genres"))).projection(Projections.include("title")).limit(limit).iterator();
				
				JsonArray ja=new JsonArray();
				Document myDoc;
				
				try {
					
					while (cursor.hasNext()) {
						myDoc=cursor.next();
						myDoc.remove("_id");
						
						ja.add(myDoc.toJson());
						
					}
					
				} finally {
					
					cursor.close();
				}
				
				JsonObject response=new JsonObject();
				response.add("movies",ja );
				return response;
			});
			
			//--------------------------------------------------------------------------------------- ---------------------------------------------------------------------------------------------------------
			
		}

	 {
	
	
	}
		
	}
	