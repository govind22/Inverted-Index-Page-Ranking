import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/* This program takes filename as input from the command line. First it read this
 * file line by line. For each line it generates it tokens by splitting on the
 * white space. It iterates over each token and put it into the TreeMap if it is a
 * new token. TreeMap is a structure in which token is the key and Int array is the
 * value. This array stores pair of (docnumber, frequency count in that particular
 * document). Array is 1D array so all even position numbers indicates document
 * numbers and odd position shows frequency count. While iterating each token if
 * the word is already existing in the treemap then it just updates the integer
 * array. If the same word is in the different document then it adds pair in the
 * integer array (document number, frequency count = 1). Once it is done for whole
 * file, for printing it just counts all odd position numbers for that particular
 * token and prints total count of frequency count and half size of the array
 * indicates total number of documents.
 */ 


public class IndexPrinter 
{
   public IndexPrinter()
   {

   }
   /**
    @param filename : file name
    @param tm : treemap
    @return : total number of documents in the corpus
    
    This function creates posting list and stores it in the treemap. Treemap has query token and value is 2D array.
    At each index in 2D array stores the posting list for term in the format <d, tfd, p0, p1, p2>
    for example "the" occurs in documet 0 at 2, 3 and at document 1 at 6 and 7 then value is 
    val[0] = 0, 2, 2, 3
    val[1] = 1, 2, 6, 7
    */
   public static int getPositionalList(String filename, TreeMap<String, List<List<Integer>>> tm) 
   {
      String filePath = null;
      try 
      { 
         filePath = new java.io.File(".").getCanonicalPath();
         filePath = filePath.concat("/");
         filePath = filePath.concat(filename);
      }
      catch(IOException e)
      {
         System.err.println("File not found. Please input a valid file");
         System.exit(0);
      }

       

      BufferedReader br = null;
      String strLine;
      DataInputStream in = null;
      List<List<Integer>> getList = new ArrayList<List<Integer>>();
      try 
      {
         File file = new File(filePath);
         FileInputStream fstream = new FileInputStream(file.getAbsolutePath());

         /**
           Get the object of DataInputStream
          */
         in = new DataInputStream(fstream);
         br = new BufferedReader(new InputStreamReader(in));
      }
      catch(IOException e)
      {
         System.err.println("File not found. Please input a valid file");
         System.exit(0);
      }

      int docNum = 0;
      int positionNum = 0;

      // Read File Line By Line
      try 
      {
         while ((strLine = br.readLine()) != null) 
         {
            // This indicates second \n so increment docnumber and continue.
            if(strLine.length() == 0) 
            {
               docNum++;
               positionNum = 0;
               continue;
            }

            /**
              Split the string into tokens based on white spaces 
             */
            String[] tokens = strLine.split(" ");
            for (String token : tokens) 
            {
               getList = tm.get(token); // Get the arraylist of the corresponding token (key)
               // New element add to treemap
               if(getList == null)
               {
                  //List<Integer> list = new ArrayList<Integer>();
                  List<List<Integer>> list = new ArrayList<List<Integer>>();
                  List<Integer> col = new ArrayList<Integer>();
                  list.add(col);
                  col.add(docNum); // Add document number and frequency count in the list
                  col.add(1);
                  col.add(positionNum);
                  tm.put(token, list); 
               } //End of if
               else 
               {  /**
                   Element already exists 
                   Token is appearing more than one time in the same document
                   Check for the last occurence of the same token if it is appearing 
                   more than once in the same document then just increment the
                   frequency count
                   */   

                  if(docNum == getList.get(getList.size()-1).get(0)) // check for the last occurrence of the same token
                  { 
                     //update ft,d value at first position
                     getList.get(getList.size()-1).set(1, (getList.get(getList.size()-1).get(1)+1));
                     //add this position
                     getList.get(getList.size()-1).add(positionNum);
                  }
                  /**
                    Token is appearing in the different document then put docnumber
                    and frequency count (1) in the list for that token
                   */
                  else 
                  {
                     List<Integer> col = new ArrayList<Integer>();
                     getList.add(col);
                     col.add(docNum); // Add document number and frequency count in the list
                     col.add(1);
                     col.add(positionNum);
                     tm.put(token, getList);

                  }   
               }
               positionNum++; 
            } // End of for loop
         } // end of while loop


      } 
      catch (IOException e1) 
      {

         e1.printStackTrace();
      } // End of while loop


      try 
      {
         in.close();
      } 
      catch (IOException e) 
      {

         e.printStackTrace();
      }

      return docNum;  

   }
   /**
    @param tm : treemap
    This function just iterates over the treemap and prints the result.
   */
   static void printResults(TreeMap<String, List<List<Integer>>> tm) 
   {
      Collection allKeys = tm.keySet();
      List<List<Integer>> l1 = new ArrayList<List<Integer>>();

      /**
        Obtain an Iterator for Collection
       */
      Iterator itr = allKeys.iterator();
      Integer total = 0;
      String key;

      /**
        Iterate through TreeMap values iterator
       */
      while(itr.hasNext()) 
      {
         key = (String)itr.next();
         System.out.println(key);
         l1 = tm.get(key);  // Returns value of that key
         /**
           To calculate total frequency add frequency of all documents so add
           second element from list
          */ 
         for (int k = 0; k < l1.size(); k++) 
         {
            System.out.println(l1.get(k));             
         }
      }//end of while loop
   }
}
