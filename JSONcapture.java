import java.sql.*;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Iterator;

import org.json.simple.parser.*;



   public class JSONcapture
   {

	   public static PreparedStatement stmnt;

       public static void main (String[] args)
       {

		   Connection DBconn = null;
	       JSONParser JSONparser;
	       BufferedReader inputHelper;

           try
           {

			   if (args.length != 4) {
				   throw new Exception("Please review command line. Correct usage: <username> <password> <DB url> <JSON file>");
			   }

			   //initialize DB
               String userName       = args[0];
               String password       = args[1];
               String url            = "jdbc:mysql://localhost/test?"; //= args[2];
               String JSONinput      = args[3];


               Class.forName("com.mysql.jdbc.Driver").newInstance();

               DBconn                = DriverManager.getConnection (url, userName, password);
               System.out.println ("Database connection established");
               stmnt                 = DBconn.prepareStatement("INSERT INTO jsonfields (id, name, description) VALUES (?,?,?)");


               //Initialize JSON file scanning
               JSONparser            = new JSONParser();
               inputHelper           = new BufferedReader(new FileReader(JSONinput));

               JSONHandler TerapeakHandler = new JSONHandler();
               TerapeakHandler.setKeys("name", "description");
               while (!TerapeakHandler.isEnd())
               {
				   JSONparser.parse((Reader)inputHelper, TerapeakHandler, true);
				   System.out.println("found & posted name, description:");

				   inputHelper.readLine();
				   JSONparser.reset();

		       }//end while
               stmnt.close();

           }//end try
           catch (Exception e)
           {
               //System.err.println ("Cannot connect to database server");
               e.printStackTrace();
           }//end catch
           finally
           {
               if (DBconn != null)
               {
                   try
                   {
                       DBconn.close ();
                       System.out.println ("Database connection terminated");
                   }
                   catch (Exception e) { /* ignore close errors */ }
               }
           }
       }
   }


  //Tag callback class
  class JSONHandler implements ContentHandler
  {
      private Object value;
	  private boolean found = false;
	  private boolean end   = false;
	  private Long count    = 10L;
	  private String JSONfield;
	  private String nameKey, descriptionKey;
	  public String nameValue, descriptionValue;

	   public boolean startObjectEntry(java.lang.String JSONField) throws ParseException, IOException
	   {
		   this.JSONfield = JSONField;
		   return true;
	   }

	   public boolean primitive(Object value) throws ParseException, IOException
	   {
		   //determine if the current key is 'name' or 'description'
		   if(this.JSONfield != null){

			 if(this.JSONfield.equals(nameKey)){

				 //check if name field hasn't already recently been found
				 if (this.nameValue == null){
					   System.out.println ("In 'primitive', for name.");

					   this.nameValue       = value.toString();
					   JSONfield            = null;
					   return true;
			      }
			      else {
					    nameValue           = null;
				        descriptionValue    = null;
			            return false;
			      }

			 }//end if
             //if it's a description field, make sure we alread have a preceeding name field for pairing.
			 if(this.JSONfield.equals(descriptionKey) && (this.nameValue != null)){
				  System.out.println ("In 'primitive', for description.");

				  this.descriptionValue = value.toString();
                  try {
					  JSONcapture.stmnt.setLong(1, ++count);
					  JSONcapture.stmnt.setString(2, nameValue);
					  JSONcapture.stmnt.setString(3, descriptionValue);
					  JSONcapture.stmnt.executeUpdate();
			      }
			      catch (SQLException SQLe){
					  SQLe.printStackTrace();
				  }

				  //reset values for new search
				  nameValue             = null;
				  descriptionValue      = null;
				  JSONfield             = null;
				  return false;
			 }//end if

		   }//end outer if
		   return true;
	   }//end primitive

	   public void setKeys(String name, String description)
	   {
		   this.nameKey        = name;
		   this.descriptionKey = description;
		  }

		  public boolean isEnd(){
		   return end;
	   }


	   public void startJSON() throws ParseException, IOException {
		  found = false;
		  end   = false;
	   }

	   public void endJSON() throws ParseException, IOException {
			end = true;
	   }

		  public boolean startArray() throws ParseException, IOException {
		   return true;
	   }


	   public boolean startObject() throws ParseException, IOException {
		   return true;
	   }

	   public boolean endArray() throws ParseException, IOException {
		   return true;
	   }

	   public boolean endObject() throws ParseException, IOException {
		   /*if (this.JSONfield.equals("manufacturer")){
			System.out.println (this.JSONfield);
		   	return false;
		   }*/
           return true;
	   }

	   public boolean endObjectEntry() throws ParseException, IOException {
		   return true;
	   }

}//JSONHandler class

