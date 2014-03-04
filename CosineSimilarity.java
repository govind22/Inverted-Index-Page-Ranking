import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
  This class will rank the documents in descending order of their score as per cosine ranking algorithm, 
  for search string given by the user.      
*/

public class CosineSimilarity 
{
	/**
    This method calculate log to the base 2 of given number
    @param num input number whose log base 2 to be determined
    @return log to base 2 of given number
    */	
	
   public static double log2(double num)
   {
      return (Math.log(num)/Math.log(2));
   }

   /** 
    @param termFrequency It is frequency of a term within document
    @param documentFrequency Nt : Number of documents in which term is apperared 
    @param N : Total number of documents in the collection
    @return tfidf value for that term
    */
   public static double getTFIDF(int termFrequency, int documentFrequency, int N)
   {
      double tf;
      double idf;
      double tfidf;

      
      /**
       tf = log2(termfrequency) + 1
       */
      if (termFrequency > 0)
      {
         tf = log2((double)termFrequency);
         tf = tf + 1.0;
      }
      else
         tf = 0.0;

      /**
       idf = log2(N/Nt)
       */
      idf = log2(((double)(N+1))/((double)documentFrequency));
      /**
       tfidf = tf * idf
       */
      tfidf = tf * idf;

      

      return (tfidf);
   }

   /** 
    @param eachDoc : one particular document vector which is to be filled
    @param tm : Treemap
    @param N : Total number of docs in the collection
    @param currentDocNum : document number for which fillDocVectorWithTFIDF is called
     
    This function actually fills document vector with tfidf values.
    */
   public static void fillDocVectorWithTFIDF(List<Double>eachDoc, TreeMap<String, List<List<Integer>>>tm, int N, int currentDocNum) 
   {
      Collection allKeys = tm.keySet();
      List<List<Integer>> l1 = new ArrayList<List<Integer>>();
      int currentTermNum = 0;
      boolean addedTerm = false;

      /**
       Obtain an Iterator for Collection
       */
      Iterator itr = allKeys.iterator();
      String key;
      int termFrequency = 0;
      int documentFrequency = 0;

      /**
       Iterate through TreeMap values iterator
       */
      while(itr.hasNext()) 
      {
         key = (String)itr.next();
         
         l1 = tm.get(key);  // Returns value of that key
         addedTerm = false;
         for (int k = 0; k < l1.size(); k++) 
         {
            if(currentDocNum == l1.get(k).get(0))
            {
            	/**
            	 At position 1 termFrequency for that particular document is stored
            	 */
               termFrequency = l1.get(k).get(1);
               documentFrequency = l1.size();
               eachDoc.add(getTFIDF(termFrequency, documentFrequency, N));
               addedTerm = true;
            }

         }
         /**
          If the term is not available for that particular document puts 0.0 at that location.
          */
         if(addedTerm == false)
         {
            eachDoc.add(0.0); 
         }
      }

      /**
       Ideally it should be set to equal
       */
      /*if(eachDoc.size() == tm.size())
      {
         System.out.println("hurrrraaayyyyyy");
      }*/
   }

   /**
    @param queryVector : queryVector which needs to be filled
    @param tm : Treemap
    @param N : total number of documents
    @param queryFrequency : kind of posting list for query vector.
    
    This function actually fills queryVector with its tfidf values.
    */
   public static void fillQueryVectorWithTFIDF(List<Double>queryVector, TreeMap<String, List<List<Integer>>>tm, int N, TreeMap<String, Integer>queryFrequency) 
   {
      Collection allKeys = tm.keySet();
      List<List<Integer>> l1 = new ArrayList<List<Integer>>();
      /**
       Obtain an Iterator for Collection
       */
      Iterator itr = allKeys.iterator();
      String key;

      int documentFrequency = 0;
      int termFrequency = 0;

      /**
       Iterate through TreeMap values iterator
       */
      while(itr.hasNext()) 
      {
         key = (String)itr.next();
      
         l1 = tm.get(key);  // Returns value of that key
      
         if(queryFrequency.get(key) != null)
         {
            termFrequency = queryFrequency.get(key);
            documentFrequency = l1.size(); 
            queryVector.add(getTFIDF(termFrequency, documentFrequency, N));    
         }
         /**
          if term is not present in the query Vector put 0.0
          */
         else
            queryVector.add(0.0); 
      }

      /**
       Ideally both sizes should match
       */
      /*if(queryVector.size() == tm.size())
         System.out.println("hurrrraaayyyyyy");*/
   }

   /**
    @param documentVector : It is a 2D array where at each 1D array it stores document vector for that particular document. 
    @param tm : TreeMap
    @param N : Total number of documents in the corpus
    
    This function creates document vector. It creates ArrayList for each document and then calls fillDocVectorWithTFIDF
    */
   public static void createDocumentVector(List<List<Double>>documentVector, TreeMap<String, List<List<Integer>>>tm, int N) 
   {
      for(int i = 0; i <= N; i++)
      {
         List<Double> eachDoc = new ArrayList<Double>();
         documentVector.add(eachDoc);
         fillDocVectorWithTFIDF(eachDoc, tm, N, i);
         
         
      }
   }

   /**
    @param queryVector : 1D array 
    @param query : query string
    @param tm : Treemap
    @param N : total number of documents in the collection
    
    This function creates query vector. Before that it creates queryFrequency treemap which is 
    posting list for query string. After that it fills query vector with tfidf values using 
    fillQueryVectorWithTFIDF function 
    */
   public static void createQueryVector(List<Double>queryVector, String query, TreeMap<String, List<List<Integer>>> tm, int N) 
   {
      
      String[] queryTokens = query.split(" ");
      
      /**
       create treemap for query tokens
       */
      TreeMap <String, Integer>queryFrequency = new TreeMap<String, Integer>();
      int val;

      for (String token : queryTokens) 
      {
         
         if(queryFrequency.get(token) == null) //New term
            queryFrequency.put(token, 1);
         else //already existing term
            queryFrequency.put(token, (queryFrequency.get(token)+1));
      }

      /**
       Fill the queryvector with tf-idf values. For idf use same concept as we calculate for document vector
       */

      fillQueryVectorWithTFIDF(queryVector, tm, N, queryFrequency);
      
      

   }

   /**
    @param documentVector : It is a 2D array. 
    @param queryVector : query vector
    @param N : Total number of documents in the corpus
    
    This function actually normalizes all document vectors and query vectors. Normalized means 
    divide tfidf values with the norm value. Norm (|d| or |q|) = summation(i=1 to V) qi^2
    Normalized vector = summation(i=1 to V)(di/|d|)
    */
   public static void getNormalizedVectors(List<List<Double>>documentVector, List<Double>queryVector, int N)
   {
      double val = 0.0;
      double newVal = 0.0;

      for(int i = 0; i <= N; i++)
      {
    	  /**
    	   Gets norm valus from norm function
    	   */
         val = norm(documentVector.get(i));
         
         /**
          Sets document vecotor by dividing with normed value. 
          */
         for(int j = 0; j < documentVector.get(i).size(); j++)
         {
            newVal = (documentVector.get(i).get(j))/val;
            documentVector.get(i).set(j, newVal);
         }
         
         

      }
      /**
       Query vector is normalized here.
       */
      val = norm(queryVector);
      
      for(int j = 0; j < queryVector.size(); j++)
      {
         newVal = (queryVector.get(j))/val;
         queryVector.set(j, newVal);
      }
      
      
   }

   /**
    @param queryTokens : array of string which stores tokens splitted by space on the query string
    @param N : total number of docs in the collection
    @param doc : current doc id
    @param tm : Treemap
    @return : minimum document id after the current doc id which satisfies query Tokens.
    
    This function calculates minimum next document number which satisfies the query tokens.
    */
   public static int getNextMinDoc(String queryTokens[], int N, int doc, TreeMap<String, List<List<Integer>>> tm)
   {
	   /**
	    For first query get nextDoc.
	    */
      GallopSearch gs = new GallopSearch(tm);
      int min = gs.nextDoc(queryTokens[0], N, doc);
      
      int d = min;

      for(int i = 1; i < queryTokens.length; i++)
      {
         if(d > min)
         {
            d = min;
            
         }
         min = gs.nextDoc(queryTokens[i], N, doc);

         
      }

      if(d > min)
      {
         d = min;
         
      }
      
      return (d);
   }

   /**
    @param documentVector : 2D array of document vector which is normalized
    @param queryVector : normalized queryVector
    @param N : Total # of docs in the collection
    @param query : query string
    @param tm : TreeMap
    
    This function actually creates arraylist of resultSet class to store docid and its score. 
    At the end resultSet is sorted based on score value and printd in the screen.  
    */
   public static void rankCosine(List<List<Double>>documentVector, List<Double>queryVector, int N, String query, 
         TreeMap<String, List<List<Integer>>> tm) 
   {
      getNormalizedVectors(documentVector, queryVector, N);
      String queryTokens[] = query.split(" ");
      ArrayList<ResultSet> resultSet = new ArrayList<ResultSet>();

      int doc = getNextMinDoc(queryTokens, N, -1, tm);
      

      while(doc <= N)
      {
         ResultSet res = new ResultSet();			
         res.docid = doc;
         res.score = dotProduct(documentVector.get(doc), queryVector);
         resultSet.add(res);
         doc = getNextMinDoc(queryTokens, N, doc, tm);
         
      }	     	

      HandleResultSet.sortResultSetByScore(resultSet);	
      HandleResultSet.printResult(resultSet);
   }

   /**
    @param docVector : document vector
    @param queryVector : query vector
    @return : returns sum
    
    This function actually takes dot product of 2 vectors. which is summation(i=1 to V) (di/|d|) * (qi/|q|)
    */
   public static double dotProduct(List<Double>docVector, List<Double>queryVector) {

      double sum = 0.0;
      for (int i = 0; i < docVector.size(); i++)
         sum = sum + (docVector.get(i) * queryVector.get(i));

      
      return (sum);

   }

   /**
    @param vector : The vecor which needs to be normalized
    @return : returns normalized value.
    
    This function calculates normalized value which is normalized (|d| or |q|) = squareroot(summation(i=1 to V) (di*di))
    */
   public static double norm(List<Double>vector) 
   {
      double sum = 0.0;
      for(int i = 0; i < vector.size(); i++)
         sum = sum + (vector.get(i) * vector.get(i));

      return (Math.sqrt(sum));
   }

   /**
    @param tm : Treemap
    @param N : Number of docs in the corpus
    @param query : query string
    
    This is the main function which gets called from index.java and call other functions in sequence.
    */
   public static void getCosineValues(TreeMap<String, List<List<Integer>>> tm, int N, String query)
   {
      List<List<Double>> documentVector = new ArrayList<List<Double>>();
      List<Double> queryVector = new ArrayList<Double>();
      createDocumentVector(documentVector, tm, N);
      createQueryVector(queryVector, query, tm, N);
      rankCosine(documentVector, queryVector, N, query, tm);	
   }
}


