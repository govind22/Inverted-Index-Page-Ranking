import java.util.List;
import java.util.TreeMap;


public class InvertedIndex
{

	/**
    @param args : <filename> <query string> <query algorithm>
    */
   public static void main(String[] args) 
   {
      
      TreeMap <String, List<List<Integer>>>tm = new TreeMap<String, List<List<Integer>>>();
      int N = 0;
      

      if (args.length <= 2) 
      {
         System.out.println("You need to enter the <filename>, <query string> and <algorithm>.");
         System.out.println("Usage javac Index.java <filename> <query string> <algorithm>. Filename and java program should be in the same folder");
         System.exit(0);
      }

      N = IndexPrinter.getPositionalList(args[0], tm);
      
      //IndexPrinter.printResults(tm);

      if(args[2].equalsIgnoreCase("cosine"))
      {
    	 System.out.println("Output for query string : <" + args[1] + "> as per cosine similarity is : "); 
         CosineSimilarity.getCosineValues(tm, N, args[1]);
      }
      if(args[2].equalsIgnoreCase("boolean"))
      {
         try
         {
        	System.out.println("Output for query string : <" + args[1] + "> as per boolean is : ");
            BooleanQuery.getBooleanScore(tm, N, args[1]);
         }
         catch(Exception e)
         {
            System.out.println(e);
         }
      }
   }
}
