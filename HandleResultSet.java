import java.util.ArrayList;


public class HandleResultSet 
{
	
	/**
	 This class is to handle the result set. It just has two functions 
	 one to sort results and another to print result set. Result set 
	 is another class declared in this file which has just docid and score.
	 Results in cosine similarity and in boolean query are stored by taking 
	 array of the ResultSet class. Methods like printResult and sortResultSetByScore
	 work on the arrayList of ResultSet class.
	 */
		
	/**
	 This function just prints sorted result Set.
	 */
	public static void printResult(ArrayList<ResultSet> resultSet)
	{
	   for(int i = 0; i < resultSet.size(); i++)
	   {
			System.out.println("Docid : " + resultSet.get(i).docid + " Score : " + resultSet.get(i).score);
	   }
	}
	
	/**
	 This function sorts ResultSet class arrayList using bubble sort method. 
	 It stores result in the same arrayList so it is in place sorting.
	 */
	public static void sortResultSetByScore(ArrayList<ResultSet> resultSet)
	{	
	   int d; 
	   double s;
	   int N = resultSet.size();
	    
	   for(int i = 0; i < N; i++)
	   {
	      for(int j = 1; j < (N-i); j++)
	      {
		     if(resultSet.get(j-1).score < resultSet.get(j).score)
			 {
			    s = resultSet.get(j-1).score;
			    resultSet.get(j-1).score=resultSet.get(j).score;
			    resultSet.get(j).score=s;
			    d = resultSet.get(j-1).docid;
			    resultSet.get(j-1).docid = resultSet.get(j).docid;
			    resultSet.get(j).docid = d;
			 }
	      }
	   }   			   
	}	
}


