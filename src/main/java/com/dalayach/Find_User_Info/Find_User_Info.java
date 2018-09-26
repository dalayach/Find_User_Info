package com.dalayach.Find_User_Info;

import java.net.*;
import java.io.*;
import net.sf.json.*;
import org.apache.commons.lang.exception.*;
import java.util.Scanner;
import com.dalayach.File_Handler.File_Handler;

/**  Searches for a username on Google+ API and ascertains the user is who the API returned. If so, then, takes the 
 *   data and stores it into variables for use in other classes
 *   
 *   <br /><br />Last updated - 5/30/2015
 *   <br />Program status: Completed
 *
 *   <br /><br />Contact info - dreadheadeddeveloper@gmail.com
 *
 *   @author David Alayachew
 *   @version 1.1
 *
 */
 
 //TODO
 //--Remove main method after a main class is created, since testing is no longer necessary
  
 //Also worth noting that in the one time the readURL was used, maxResults, a variable on the API, was set to
 //5, so this will limit number of results on the items array to only 5. To change, change the URL where it 
 //says maxResults
 
 //SOURCES
 
 //https://developers.google.com/apis-explorer/?hl=en_US#p/plus/v1/plus.people.search

class Find_User_Info
{

   private File_Handler key_Handler = new File_Handler();

   /** Scanner to take in user responses */
   private Scanner scan;
   /** API key that allows for access to the Google+ API - key may need to be refreshed on occasion on the site itself */
   private final String APIKey;
   /** String holds a prewritten phrase, makes reading code easier */
   private final String enterYourUserName     = "Enter your Google+ username.";
   final String incorrectResponse     = "Incorrect response";
   private final String nothingEntered        = "You didn't enter anything in.";
   private final String nameDoesntMatchUp     = "There are no names that match up with your entry.\n\n";
   private final String keysHaveChanged       = "The database this program is pulling from has been updated and " 
                                             + "as a result, this program is outdated. Please email " 
                                             + "dreadheadeddeveloper@gmail.com as this is a bug.";
   private static final String issueWithURL   = "The URL may have been improperly entered or outdated. Email " 
                                             + "dreadheadeddeveloper@gmail.com as this is a bug";
   /** String that holds username */
   private String userName = "";
   /** String that holds user id */
   private String userID; 
   /** String that holds the responses the user gives */
   private String choice;
   /** String that holds the name provided by API */
   private String displayName;
   /** boolean that makes sure an internet connection is active */
   private static boolean properConnection = true;
   
   //it is also important to note that for properConnection, value of the var changes upon check,
   //so internet may or may not be out already, but as long as the data was already collected
   //before the internet died, it will continue on as if nothing is wrong. This variable should
   //be updated upon use of an internet specific function, such as readURL(), moreover in the 
   //catch part of a try catch block when using readURL
   
   /** boolean that makes sure readURL() was properly executed and didn't run into any exceptions */
   private boolean readURLCorrectlyExecuted;
   /** boolean that ensures the user entered their name properly */
   private boolean enteredProperly = false;
   /** boolean that ensures the user is the username provided by the API */
   private boolean correctUser = false;
   /**  */
   private boolean programIsOutdated = false;
   /** JSONObject that holds the retrieved data from the Google+ API section plus.people.search */
   private JSONObject plus_ppl_search;
   /** JSONObject that holds individual keys from plus_ppl_search items key */
   private JSONObject plus_ppl_search_items_JSONObjectContainer;
   /** JSONArray that holds an array of elements from plus_ppl_search items key */
   private JSONArray plus_ppl_search_items;

   Find_User_Info()
   {
   
      APIKey = key_Handler.fetch_Secret("Google+");
   
      while((!enteredProperly || !correctUser) && properConnection)//while username has not been entered properly 
      //and/or the correct user has not been found and as long as the connection is still live
      {
      
         ensureUserNameProperlyEntered();//ensure username has been properly entered
         
         if(enteredProperly && properConnection)//is username has been properly entered and internet connection still active
         {
         
            verifyUser();//verify the user is the same as the one found
         
         }
      
      }//end while loop
      
      if(properConnection && !programIsOutdated)//if internet connection was active at last data retrieval from internet
      //and the program has not yet been outdated
      {
      
         captureDataOnUser();//pull data on user and store into variables
      
      }
            
   }
   
   /** ensures username has been properly entered by checking if username is empty
    *
    *
    */
   void ensureUserNameProperlyEntered()
   {
   
      enteredProperly = true;//variable that will change depending on whether or not username entered properly
   
      scan = new Scanner(System.in);
      
      System.out.println(enterYourUserName);//requests user to enter username
      userName = scan.nextLine();//takes in user response and stores it into userName
               
      if(userName.isEmpty())//if userName is empty
      {
        
         System.out.println(nothingEntered);//informs user
         enteredProperly = false;//sets false so that program will stay within while loop until username entered properly
      
      }
      
      else
      {
         
         try
         {
            
            plus_ppl_search = JSONObject.fromObject(readURL("https://www.googleapis.com/plus/v1/people?query=" 
                  + userName + "&maxResults=5&key=" +  APIKey));//takes in data from Google+ API section plus.people.search and
                                           //stores the data into JSONObject plus_ppl_search
                                 
            plus_ppl_search_items = (JSONArray)(plus_ppl_search.get("items"));//retrieves the array called data within 
                                           //plus_ppl_search and stores it into JSONArray plus_ppl_search_items
            
            if(plus_ppl_search_items.isEmpty())//if JSONArray is empty
            {
               
               System.out.println(nameDoesntMatchUp);//inform user
               enteredProperly = false;//sets false so that program will stay within loop until username entered properly
               
            }
               
            else
            {
               
               displayName = plus_ppl_search_items.getJSONObject(0).getString("displayName");
               
            }
         }
            
         catch(JSONException jsone)
         {
            
            properConnection = false;
            System.out.println(issueWithURL);
            
         }
         
         catch(UnknownHostException uhe)
         {
         
            System.out.println("This program requires internet connection to work.");
            properConnection = false;  
             
         }   
      
      }
   }
         
   /**   verifies that the user is the one found
    *
    *
    */
   void verifyUser()
   {
   
      boolean answeredCorrectly = false;//boolean to ensure the user responds correctly to questions posed later in method
      correctUser = true;//boolean to ensure the user is the one found
   
      while(!answeredCorrectly)//user has not yet answered correctly
      {
      
         System.out.println("Is your username " 
            + displayName + "? (Y/N)");//asks user if their name matches with the one found
         
         choice = scan.nextLine();//stores user response into choice
      
         if(choice.equals("Y")||choice.equals("y"))//if yes
         {
         
            answeredCorrectly = true;//sets true so that program will escape while loop for user answering correctly
            
         }
         
         else if(choice.equals("N")||choice.equals("n"))//else if user says no
         {
         
            answeredCorrectly = true;//sets true so that program will escape while loop for user answering correctly
            correctUser = false;//sets false because no means the name found does not match the name requested
         
         }
         
         else//if user did not enter a proper response
         {
         
            System.out.println(incorrectResponse);//inform user that they did not enter a proper response
            answeredCorrectly = false;//sets false to ensure program stays within while loop
         
         }
      
      }//end while loop
   
   }
   
   /** captures data about user such as id and displayName
    *
    *
    */
   void captureDataOnUser()
   {
   
      try
      {
      
         userID = plus_ppl_search_items.getJSONObject(0).getString("id");//retrieves id and stores it in userID
      
      }
            
      catch(JSONException jsone)
      {
            
         properConnection = false;
         System.out.println(keysHaveChanged);
            
      }
      
   }
   
   /** will return JSON data from a websource 
    *  
    *  @return String that contains JSON data
    *  
    *  @param webservice
    *         url of the webservice that will provide the JSON data
    *
    */
   static String readURL(String webservice) throws UnknownHostException
   {
      
      /** takes in each line of text from the reader */
      String inputLine;
      /** adds each line from inputLine and forms result */
      String result = "";
      
      try
      {
      
         URL provider = new URL(webservice);//url to reach the data provider
         
         try
         {
         
            BufferedReader in = new BufferedReader(new InputStreamReader(provider.openStream()));//reader to read the data 
                                                                              //from provider and put it as a string
            while ((inputLine = in.readLine()) != null)//while there are still lines left to read
            {
            
               result = result + inputLine;//reads each line and adds it to result
            
            }//end while loop
         
            in.close();//closes reader
         
         }
         catch(IOException ioe)
         {
         
            System.out.println(issueWithURL);
            properConnection = false;
            
         }
                    
      }
      
      catch(MalformedURLException murle)
      {
      
         System.out.println(issueWithURL);
         properConnection = false;
      
      }
   
      return result;
      
   }

   /** main method
    *
    *
    */
   public static void main(String[] args)
   {
   
      Find_User_Info gpa = new Find_User_Info();
   
   }

}