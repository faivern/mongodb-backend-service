package se.kau.isgb33;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream.Filter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import java.awt.*;


public class Stub {

	public static void main(String[] args) {
		
		JFrame f = new JFrame("mFLIX");
		f.setFont(new Font("Arial Black", Font.PLAIN, 12));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBackground(new Color(255, 255, 255));
		f.setSize(400,500);
		f.getContentPane().setLayout(null);
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		
		JTextArea area = new JTextArea("Välkommen");
		area.setBackground(new Color(255, 255, 255));
		area.setFont(new Font("Arial", Font.PLAIN, 13));
		area.setLineWrap(true);
		area.setBounds(10,10,365,400);
		
		JTextField t = new JTextField("");
		t.setFont(new Font("Arial", Font.PLAIN, 12));
		t.setBackground(new Color(192, 192, 192));
		t.setForeground(new Color(0, 0, 0));
		t.setBounds(10, 415, 260, 40);
		
		JButton b = new JButton("Sök");
		b.setFont(new Font("Arial", Font.BOLD, 12));
		b.setBounds(275,415,100,40);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent q) {
				
				String connString;
				try (InputStream input = new FileInputStream("connection.properties")) {
					
					Properties prop = new Properties();
					prop.load(input);
					connString = prop.getProperty("db.connection_string");					
					
					ConnectionString connectionString = new ConnectionString(connString);
					MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
					MongoClient mongoClient = MongoClients.create(settings);
					MongoDatabase database = mongoClient.getDatabase(prop.getProperty("db.name"));
					MongoCollection<Document> collection = database.getCollection("mflix");
					
					Bson filter = Filters.in("genres", t.getText());
					AggregateIterable<Document> myDocs = collection.aggregate(Arrays.asList(
						
						Aggregates.match(filter) ,
						Aggregates.sort(Sorts.descending("title")),
						Aggregates.project(
							Projections.fields( 
								Projections.excludeId(),
								Projections.include("title", "year")
								)
								),
								Aggregates.limit(10) 
								
								));
								
								area.setText("");
								
									MongoCursor<Document> iterator = myDocs.iterator();
								if(!iterator.hasNext()){
									
									area.append("Ingen film matchade kategorin.");
									
								}
								
								while(iterator.hasNext()){
									Document myDoc = iterator.next();
									
									area.append(myDoc.getString("title") + ", ");
									area.append(myDoc.get("year").toString() + "\n");
											
								}
								
								
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
				
					e.printStackTrace();
					 
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				} 
								

			}

			 
		});
		
	
		f.getContentPane().add(b);
		f.getContentPane().add(area);
		f.getContentPane().add(t);
		f.setVisible(true);
		

	}

}
