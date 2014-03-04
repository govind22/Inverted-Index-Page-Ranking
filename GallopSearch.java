import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
This class handles GallopSearching. So all methods like next, prev, nextDoc, prevDoc
are implemented in this class.
*/

public class GallopSearch 
{
  
   String term;
   static TreeMap<String, List<List<Integer>>> treemap= new TreeMap<String, List<List<Integer>>>();
   TreeMap<String, List<Integer>> cacheIndex= new TreeMap<String, List<Integer>>();

   /**
    This is parameterised constructor which takes treemap as input. 
    It also creates cacheIndex array for each term. It stores 
    */
   public GallopSearch(TreeMap<String, List<List<Integer>>> tm)
   {
      this.treemap=tm;
      Set st = treemap.keySet();
      Iterator itr=st.iterator();
      while(itr.hasNext())
      {
         ArrayList<Integer> arr = new ArrayList<Integer>();
         //arr.add(-1);
         arr.add(-1);
         cacheIndex.put((String)itr.next(),arr);
      }

   }

   @SuppressWarnings("unchecked")
   /**
     This method will return next occurance of a term within a document corpus
     @param term Term whose next occurance to find out
     @param position : current position from where next occurance of term to find out. At 0th location doc id is stored
                       at 1st location it stores position in that document 
     @return integer array with two element, first is document and second is position in that document
     
     This function actually handle next method. It is Gallop search based logic as per explained in the book.
     It also stores last result in cacheindex treemap which is similar to ct explained in the book.
     Returns document number and position of the next occurance of the term after position.  
    */
   public int[] next(String term, int[] position)
   {
      int lastPos[] = new int[2]; 
      int lastOccur[] = new int[2];	
      int low,jump,high;

      List<List<Integer>> termList = treemap.get(term);	
      int lt = 0;

      for(int i = 0; i <termList.size(); i++)
         lt = lt + termList.get(i).get(1);

      lastPos[0] = termList.get(termList.size()-1).get(0);
      lastPos[1] = termList.get(termList.size()-1).get((termList.get(termList.size()-1).get(1))+1);
      
      if((lt == 0) || ((lastPos[0]==position[0]) && (lastPos[1] <= position[1])) || (lastPos[0] < position[0]))		    
         return new int[] {-1, -1};
      
      List <Integer> arr = cacheIndex.get(term);

      if((termList.get(0).get(2) > position[1]) && (termList.get(0).get(0) >= position[0]))
      {
         arr.set(0, 1);
         lastOccur[0] = termList.get(0).get(0);
         lastOccur[1]= termList.get(0).get(2);

         return new int[] {lastOccur[0],lastOccur[1]};
      }
      
      int currentDocCtr = 0;
      int offsetInCurrentDoc = 0;
      int currentDoc = 0;

      if(arr.get(0) > 1) 
      {	 
         currentDocCtr = -1;
         while(offsetInCurrentDoc < arr.get(0))
         {
            currentDocCtr = currentDocCtr + 1;
            offsetInCurrentDoc = offsetInCurrentDoc + termList.get(currentDocCtr).get(1); 
            currentDoc = termList.get(currentDocCtr).get(0);
         }

         if((offsetInCurrentDoc - termList.get(currentDocCtr).get(1)) > arr.get(0))
         {
            offsetInCurrentDoc = offsetInCurrentDoc - termList.get(currentDocCtr).get(1);
            currentDocCtr--;
            currentDoc = termList.get(currentDocCtr).get(0);
         }

         offsetInCurrentDoc = offsetInCurrentDoc - termList.get(currentDocCtr).get(1);		         
         low = arr.get(0);
      }	
      else
         low = 0;
      jump = 1;
      high = low+jump;

      if(high > (offsetInCurrentDoc + termList.get(currentDocCtr).get(1)))
      {
         offsetInCurrentDoc = offsetInCurrentDoc + termList.get(currentDocCtr).get(1); 			
         currentDocCtr++;
         currentDoc = termList.get(currentDocCtr).get(0);

      }
      while((high < lt) && (currentDoc <= position[0]) && 
            ((high-offsetInCurrentDoc+1) < (termList.get(currentDocCtr).size())) && 
            (((currentDoc == position[0]) && 
              (termList.get(currentDocCtr).get(high-offsetInCurrentDoc+1) <= position[1])) || 
             (currentDoc < position[0])))
      {
         low = high;
         jump = jump*2;
         high = low + jump;
         if((high < lt) && (currentDocCtr < termList.size()) && 
               (high > (termList.get(currentDocCtr).get(1) + offsetInCurrentDoc)))
         {
            offsetInCurrentDoc = offsetInCurrentDoc + termList.get(currentDocCtr).get(1);
            currentDocCtr = currentDocCtr + 1;
            currentDoc = termList.get(currentDocCtr).get(0);            
         } 
      }
      if((high > lt) && (currentDocCtr < termList.size()) && 
            (high > (termList.get(currentDocCtr).get(1) + offsetInCurrentDoc)))
      {
         offsetInCurrentDoc = offsetInCurrentDoc + termList.get(currentDocCtr).get(1);
         currentDocCtr = currentDocCtr + 1;        
         currentDoc = termList.get(currentDocCtr).get(0);
          high = lt;
      }
      

      if(currentDocCtr > 0)
      {
         /*if((position[0] == termList.get(currentDocCtr-1).get(0)) && 
           (position[1] < termList.get(currentDocCtr-1).get(termList.get(currentDocCtr-1).size()-1)))*/
         if((position[0] == termList.get(currentDocCtr-1).get(0)) && 
               (position[1] < termList.get(currentDocCtr-1).get(termList.get(currentDocCtr-1).size()-1)) || (currentDocCtr > termList.size()))

         {
            currentDocCtr = currentDocCtr-1;

            currentDoc = termList.get(currentDocCtr).get(0);
            
            offsetInCurrentDoc = offsetInCurrentDoc - termList.get(currentDocCtr).get(1);
            
            high = termList.get(currentDocCtr).get(1) + offsetInCurrentDoc;
         }
      }
      
      int ct;
      ct = binarySearch(term,low,high, position, currentDoc, currentDocCtr, offsetInCurrentDoc);
      arr.set(0, ct);

      for(int i = currentDocCtr; i < termList.size(); i++)
      {
         if((ct-offsetInCurrentDoc+1) > (termList.get(currentDocCtr).size()-1))
         {
            offsetInCurrentDoc = offsetInCurrentDoc + termList.get(currentDocCtr).get(1);
            currentDocCtr++;
         }
         else
            break;
      }
      lastOccur[0]= termList.get(currentDocCtr).get(0);
      lastOccur[1]= (termList.get(currentDocCtr).get(ct-offsetInCurrentDoc+1));
      return new int[] {lastOccur[0],lastOccur[1]};
   }

   /**
     This method is to perform binary search on given interval of low to high
     @param term2 Term whose next occurance is to be determined
     @param low Starting position for binary search
     @param high end position for binary search
     @param position1 current position within the document corpus 
     @param currentDoc Current document in the corpus
     @param currentDocCtr Counter for current document 
     @param offsetInCurrentDoc offset within current document
     @return an integer which indicates latest position in cacheIndex 
     
     This function actually handles binarysearch
    */
   private int binarySearch(String term2, int low, int high, int[] position1, 
         int currentDoc, int currentDocCtr, int offsetInCurrentDoc) 
   {
      int mid;

      List<List<Integer>> termList1 = treemap.get(term2);	

      if(currentDoc > position1[0])
      {
         high = offsetInCurrentDoc + 1;
         return(high);
      }

      while((high-low) > 1)
      {
         mid = (low+high)/2;
         if(((currentDoc == position1[0]) && ((mid-offsetInCurrentDoc+2-1) <=  termList1.get(currentDocCtr).size()) && 
                  (termList1.get(currentDocCtr).get(mid-offsetInCurrentDoc+2-1) <= position1[1])))// ||
            //((currentDoc > position1[0]) && (mid-offsetInCurrentDoc+2) <=  termList1.get(currentDocCtr).size()))
         {
            low = mid;
         }
         else
            high = mid;         
      }
      return high;
   }
   /**
    @param term : token string
    @return : returns int arracy which has first occrance of the term. At 0th position it stores document number and at 
    1st postition first position in that document where it occured is stored. 
    */
   public int[] first(String term)
   {
       List<List<Integer>> termList = treemap.get(term);
       if(termList.size() > 0)
    	   return new int[] {termList.get(0).get(0), termList.get(0).get(2)};
           //return(termList.get(0).get(0));
       return new int[] {-1, -1};
   }
   
   /**
    @param term : query string
    @return returns first document number
    */
   public int firstDoc(String term)
   {
       List<List<Integer>> termList = treemap.get(term);
       if(termList.size() > 0)
           return(termList.get(0).get(0));
       return (-1);
   }
   /**
    @param term : token string
    @return : int array which has last occrance of the term. At 0th position it stores document number and at 
    1st postition last position in that document where it occured is stored.
    */
   
   public int[] last(String term)
   {
       List<List<Integer>> termList = treemap.get(term);
       if(termList.size() > 0)
           return new int[] {termList.get(termList.size()-1).get(0), termList.get(termList.size()-1).get(termList.get(termList.size()-1).size()-1)};
       return new int[] {-1, -1};
   }
   /**
   @param term : query string
   @return returns last document number
   */
   public int lastDoc(String term)
   {
       List<List<Integer>> termList = treemap.get(term);
       if(termList.size() > 0)
           return(termList.get(termList.size()-1).get(0));
       return (-1);
   }
   
   /**
   @param term : query string
   @param docid : current document number
   @return returns previous document number
   
   This function returns (-1) for -infinity
   */
   public int prev(String term, int docid)
   {
       int lt = 0;
       int low, jump, high;
       List<List<Integer>> termList = treemap.get(term);
       List <Integer> arr = cacheIndex.get(term);
       lt = termList.size();
  
       if((termList.get(termList.size()-1).get(0) < docid))
       {
           arr.set(0, (termList.size()-1));
           return lastDoc(term);
       }
       
       
       if((arr.get(0) >= 0) && (termList.get(arr.get(0)).get(0) > docid))
       {
      
           high = termList.get(arr.get(0)).get(0);
       }
       else
       {    
           high = termList.size()-1;
       }    
       
       jump = 1;
       
       low = high - jump;
       if(low < 0)
           low = 0;
       
       while((low >= 0) && (termList.get(low).get(0) >= docid))
       {
      
           high = low;
           jump = jump * 2;
           low = high - jump;            
       }
       
       if(low < 0)
           low = 0;
       int ct = binarySearch(term, low, high, docid);
      
       arr.set(0, ct);
       if(termList.get(ct).get(0) < docid)
          return termList.get(ct).get(0);
       else
           return (-1);
       
   }
   /**
    @param term : token
    @param low : lower number min limit where it can be
    @param high : high number max limit where it can be
    @param docid : current document number
    @return low number
    
    This function returns the position index of the posting list
    */
   public int binarySearch(String term, int low, int high, int docid)
   {
       int mid;
       List<List<Integer>> termList = treemap.get(term);
       while((high - low) > 1)
       {
           mid = ((low + high) / 2);
      
           if(termList.get(mid).get(0) >= docid)
               high = mid;
           else
               low = mid;
       }
       return low;
   }

   /**
     This method will return next document in which token occurs
     @param token This is query to be searched by user
     @param N Total number of document within the corpus
     @param d current document in the corpus
     @return an integer value which determines next document in which token occurs
    */
   public int nextDoc(String token, int N, int d)
   {
      List<List<Integer>> termList = treemap.get(token);
      int lastOccur[];
      int current[] = new int[2];
      int newDocId = -1;

      if (d == -1)
         return firstDoc(token);
      
      /**
       Creates current
       */
      current[0] = d;
      current[1] = 0;      

      while(newDocId <= d)
      {	   	
         current = next(token, current);
         newDocId = current[0];
         if(newDocId == -1)
            break;		    
      }

      if(newDocId > d)
         return (newDocId);

      return (N+1);
   }
   /**
    This method will return previous document in which token occurs
     @param token : This is query to be searched by user
     @param d current document in the corpus
     @return an integer value which determines previous document in which token occurs
   */
      
   public int prevDoc(String token, int d)
   {
	   return(prev(token, d));
   }
}
