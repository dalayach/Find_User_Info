package com.dalayach.Find_User_Info;

import java.net.*;
import java.io.*;
import net.sf.json.*;
import org.apache.commons.lang.exception.*;
import java.util.Scanner;
import com.dalayach.File_Handler.File_Handler;

/**  Searches for a username on Google+ API and ascertains the user is who the API returned. If so, then, takes the 
 *   data and stores it into variables for use in other classes
 */
 
 //TODO - put the remaining boolean variables to work


class Find_User_Info
{

   final int MAXRESULTS = 5;

   private static File_Handler key_Handler = new File_Handler();

   private Scanner scan;
   private static final String APIKey = key_Handler.fetch_Secret("Google+");
   private static final String email = key_Handler.fetch_Email();
   private final String enterYourUserName       = "Enter your Google+ username.";
   private final String incorrectResponse       = "Incorrect response";
   private final String nothingEntered          = "You didn't enter anything in.";
   private final String nameDoesntMatchUp       = "There are no names that match up with your entry.\n\n";
   private final String keysHaveChanged         = "The database this program is pulling from has been updated and " 
                                                   + "as a result, this program is outdated. Please email " 
                                                   + email + " as this is a bug.";
   private static final String issueWithURL     = "The URL may have been improperly entered or outdated. Email " 
                                                   + email + " as this is a bug";
   private String userName = "";
   private String userID; 
   private String choice;
   private String displayName;
   private static boolean properConnection = true;
   
   //it is also important to note that for properConnection, value of the var changes upon check,
   //so internet may or may not be out already, but as long as the data was already collected
   //before the internet died, it will continue on as if nothing is wrong. This variable should
   //be updated upon use of an internet specific function, such as readURL(), moreover in the 
   //catch part of a try catch block when using readURL
   
   private boolean readURL_CorrectlyExecuted;
   private boolean enteredProperly = false;
   private boolean correctUser = false;
   private boolean programIsOutdated = false;
   private JSONObject plus_ppl_search;
   private JSONObject plus_ppl_search_items_JSONObjectContainer;
   private JSONArray plus_ppl_search_items;

   Find_User_Info()
   {
   
      //APIKey = key_Handler.fetch_Secret("Google+");
      //email = key_Handler.fetch_Email();
   
      while((!enteredProperly || !correctUser) && properConnection)
      {
      
         ensureUserNameProperlyEntered();
         
         if(enteredProperly && properConnection)
         {
         
            verifyUser();//verify the user is the same as the one found
         
         }
      
      }//end while loop
      
      if(properConnection && !programIsOutdated)
      {
      
         captureDataOnUser();//pull data on user and store into variables
      
      }
      
      System.out.println(toString());
            
   }
   
   void ensureUserNameProperlyEntered()
   {
   
      enteredProperly = true;//variable that will change depending on whether or not username entered properly
   
      scan = new Scanner(System.in);
      
      System.out.println(enterYourUserName);
      userName = scan.nextLine();
               
      if(userName.isEmpty())
      {
        
         System.out.println(nothingEntered);
         enteredProperly = false;
      
      }
      
      else
      {
         
         try
         {
            
            plus_ppl_search = JSONObject.fromObject(readURL("https://www.googleapis.com/plus/v1/people?query=" 
                  + userName + "&maxResults=" + MAXRESULTS + "&key=" +  APIKey));//takes in data from Google+ API section plus.people.search and
                                           //stores the data into JSONObject plus_ppl_search
                                 
            plus_ppl_search_items = (JSONArray)(plus_ppl_search.get("items"));//retrieves the array called data within 
                                           //plus_ppl_search and stores it into JSONArray plus_ppl_search_items
            
            if(plus_ppl_search_items.isEmpty())
            {
               
               System.out.println(nameDoesntMatchUp);
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
         
   void verifyUser()
   {
   
      boolean answeredCorrectly = false;
      correctUser = true;//boolean to ensure the user is the one found
   
      while(!answeredCorrectly)
      {
      
         System.out.println("Is your username " 
            + displayName + "? (Y/N)");
         
         choice = scan.nextLine();
      
         if(choice.equals("Y")||choice.equals("y"))
         {
         
            answeredCorrectly = true;
            
         }
         
         else if(choice.equals("N")||choice.equals("n"))
         {
         
            answeredCorrectly = true;//sets true so that program will escape while loop for user answering correctly
            correctUser = false;//sets false because no means the name found does not match the name requested
         
         }
         
         else//if user did not enter a proper response
         {
         
            System.out.println(incorrectResponse);
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
   
   public String toString()
   {
   
      String response = "Posting the top " + MAXRESULTS + " results below...";
      for(int i = 0; i < MAXRESULTS; i++)
      {
      
         response = response + "\n" + String.format("%7s", i + 1) + ". ";
         response = response + plus_ppl_search_items.getJSONObject(i).getString("id");
         response = response + " = " + plus_ppl_search_items.getJSONObject(i).getString("displayName");
      
      }
      
      return response;
   
   }
   
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