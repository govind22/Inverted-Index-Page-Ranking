import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BooleanQuery 
{
	/**
	 @param input : string
	 @return : boolean value based on given string is integer or not
	 */
   public static boolean isInteger( String input )  
   {  
      try  
	  {  
	     Integer.parseInt( input );  
		 return true;  
	  }  
	  catch( Exception e)  
	  {  
	     return false;  
	  }    
   }  
	
   /**
    @param N : Total number of docs in the corpus
    @param firstOperand : first operand 
    @param operation : operation or / and
    @param secondOperand : second operand
    @param docid : current document id
    @param tm : Treemap
    @return : next document number
    
    docRight(A + B) = min(nextDoc(A), nextDoc(B))
    docRight(A * B) = max(nextDoc(A), nextDoc(B))
    
    */
   public static int docRight(int N, String firstOperand, String operation, String secondOperand, int docid,TreeMap<String, List<List<Integer>>> tm)
   {
      GallopSearch gs = new GallopSearch(tm);
      int d1, d2;
      if(isInteger(firstOperand))
         d1 = Integer.parseInt(firstOperand);
      else
         d1 = gs.nextDoc(firstOperand, N, docid);
      if(isInteger(secondOperand))
         d2 = Integer.parseInt(secondOperand);
      else
         d2 = gs.nextDoc(secondOperand, N, docid);
      
      if (operation.equals("+"))
      {
         if(d1 < d2)
            return d1;
         else 
            return d2;
      }
      if(operation.equals("*"))
      {
         if(d1 > d2)
            return d1;
         else
            return d2;
      }
      /**
       (N+1) is considered as positive infinity.
       */
      return (N+1);
   }
	
   /**
    @param N : Total number of docs in the corpus
    @param firstOperand : strinf first operand
    @param operation : + or *
    @param secondOperand : string second operand
    @param docid : current docid
    @param tm : Treemap
    @return : previous document
    
    docLeft(A * B) = min(prevDoc(A), prevDoc(B))
    docLeft(A + B) = max(prevDoc(A), prevDoc(B))
    */
   public static int docLeft(int N, String firstOperand, String operation, String secondOperand, int docid,TreeMap<String, List<List<Integer>>> tm)
   {
      GallopSearch gs = new GallopSearch(tm);
      int d1, d2;
      if(isInteger(firstOperand))
         d1 = Integer.parseInt(firstOperand);
      else
         d1 = gs.prevDoc(firstOperand,  docid);

      if(isInteger(secondOperand))
         d2 = Integer.parseInt(secondOperand);
      else
         d2 = gs.prevDoc(secondOperand, docid);

      if (operation.equals("+"))
      {
         if(d2 < d1)
            return d1;
         else 
            return d2;
      }
      if(operation.equals("*"))
      {
         if(d2 > d1)
            return d1;
         else
            return d2;
      }

      /**
       (-1) is similar to negative infinity. If there is no previous doc it returns -infinity
       */
      return (-1);
   }	
	
   /**
    @param operation : operation like (-) only this operation should be present here.
    @param firstOperand : string first operand
    @param N : total number of documents in the corpus
    @param docId : current doc id
    @param tm : Treemap
    @return : docRight for queries like (- you)
  
    This algorithm is same as the one explained in the book. for negation it is expected to have just one term. 
    Using nextDoc method it tries to find out next document which does not have this term (here it is you).
    */
   public static int docRightForNegation(String operation, String firstOperand, int N, int docId, TreeMap<String, List<List<Integer>>>tm)
   {
      GallopSearch gs = new GallopSearch(tm);
      int d1;
      int d2 = docId;

      if(operation.equals("-"))
      {
         d1 = gs.nextDoc(firstOperand, N, d2);
         while (d1 == (d2+1))
         {
            d2 = d1;
            d1 = gs.nextDoc(firstOperand, N, d2);
         }
      }
      
      return(d2+1);
   }

	/**
	 @param operation : operations should be (-)
	 @param firstOperand : String first operand
	 @param N : total number of docs in the corpus
	 @param docId : current doc id
	 @param tm : treemap
	 @return docLeft for queries like (- you)
	 
	 This algorithm is based on docRight for not terms. using prevDoc it takes previous document 
	 number from the current document number which has term and then finds document number which 
	 does not has that term
	 */
   public static int docLeftForNegation(String operation, String firstOperand, int N, int docId, 
		                                TreeMap<String, List<List<Integer>>>tm)
   {
      GallopSearch gs = new GallopSearch(tm);
      int d1;
      int d2 = docId;

      if(operation.equals("-"))
      {
         d1 = gs.prevDoc(firstOperand, d2);
         while (d1 == (d2-1))
         {
            d2 = d1;
            d1 = gs.prevDoc(firstOperand, d2);
         }
      }
      
      return(d2-1);
   }

   /**
    @param tm : treemap 
    @param N : total number of docs in the corpus
    @param querySet : ArrayList which has infix expression
    @param docId : current docid
    @return : document which satifies the infix expression
    
    This is same as nextSolution function from the book. It takes v = docRight(Q) and also u = docLeft(Q).
    If both u and v are equal then returns the document i.e. (u) 
    */
   public static int getSolution(TreeMap<String, List<List<Integer>>> tm, int N, ArrayList<String> querySet, int docId)
   {
      String firstOperand;
      String secondOperand;
      String operation;
      int newDocId;

      /**
       To run docLeft on query first copy the whole arrayList.
       */
      ArrayList<String> newQuerySet = new ArrayList<String>(querySet);
      /**
      At the end queryset will just has a document number which satisfies the condition. 
       */
      /**
       This is for v = docRight(query)
       */
      while(querySet.size() != 1)
      {
         for(int i = 0; i < querySet.size(); i++)
         {
            
            /**
             In first iteration replace all terms like (A + B) or (A * B) with document number 
             which satisfies this condition.
             */
            if((querySet.get(i).equals("(")) && (querySet.get(i+1) != "(") && (querySet.get(i+4).equals(")")))
            {
               if(!(querySet.get(i+1).equals("-")))
               {
                  firstOperand = querySet.get(i+1);
                  operation = querySet.get(i+2);
                  secondOperand = querySet.get(i+3);
                  newDocId = docRight(N, firstOperand, operation, secondOperand, docId, tm);
                  querySet.set(i, Integer.toString(newDocId));
                  querySet.remove(i+1);
                  querySet.remove(i+1);
                  querySet.remove(i+1);
                  querySet.remove(i+1);
                  
               }
               /**
                In first iteration replace all terms like (- A) with document number which satisfies this condition
                */
               else if((querySet.get(i+1).equals("-")) && 
                     !(isInteger(querySet.get(i+2))) && 
                     !(querySet.get(i+2).equals(")")) && 
                     !(querySet.get(i+2).equals("(")) && 
                     (querySet.get(i+3).equals(")")) )
               {
                  operation = querySet.get(i+1);
                  firstOperand = querySet.get(i+2);
                  
                  newDocId = docRightForNegation(operation, firstOperand, N, docId, tm);
                  querySet.set(i, Integer.toString(newDocId));
                  querySet.remove(i+1);
                  querySet.remove(i+1);
                  querySet.remove(i+1);				
               }
            }
         }		
      }
      /**
       Last element which just has one docid
       */
      newDocId = Integer.parseInt(querySet.get(0));
      querySet.remove(0);
      
      /**
       Implement u = docLeft(Q)
       */
      int newPrevDocId = -1;
      if(newDocId <= N)
      {
         docId = newDocId + 1;
         while(newQuerySet.size() != 1)

         {
            for(int i = 0; i < newQuerySet.size(); i++)
            {
               
               /**
                replace all terms like (A + B) to document number in the interations
                */
               if((newQuerySet.get(i).equals("(")) && (newQuerySet.get(i+1) != "(") && (newQuerySet.get(i+4).equals(")")))
               {
                  if(!(newQuerySet.get(i+1).equals("-")))
                  {
                     firstOperand = newQuerySet.get(i+1);
                     operation = newQuerySet.get(i+2);
                     secondOperand = newQuerySet.get(i+3);
                     newPrevDocId = docLeft(N, firstOperand, operation, secondOperand, docId, tm);
                     newQuerySet.set(i, Integer.toString(newPrevDocId));
                     newQuerySet.remove(i+1);
                     newQuerySet.remove(i+1);
                     newQuerySet.remove(i+1);
                     newQuerySet.remove(i+1);
                     
                  }
                  /**
                   replace terms like (- A) with document number
                   */
                  else if((newQuerySet.get(i+1).equals("-")) && 
                        !(isInteger(newQuerySet.get(i+2))) && 
                        !(newQuerySet.get(i+2).equals(")")) && 
                        !(newQuerySet.get(i+2).equals("(")) && 
                        (newQuerySet.get(i+3).equals(")")) )
                  {
                     operation = newQuerySet.get(i+1);
                     firstOperand = newQuerySet.get(i+2);
                     
                     newPrevDocId = docLeftForNegation(operation, firstOperand, N, docId, tm);
                     newQuerySet.set(i, Integer.toString(newPrevDocId));
                     newQuerySet.remove(i+1);
                     newQuerySet.remove(i+1);
                     newQuerySet.remove(i+1);				
                  }
               }
            }		
         }
         
         newPrevDocId = Integer.parseInt(newQuerySet.get(0));
         newQuerySet.remove(0);

         /**
          if both documnet numbers are equals then only return document number otherwise 
          return (-1) for false positive case 
          */
         if(newDocId == newPrevDocId)
            return newDocId;
         else
            return (-1);
      }
      return (newDocId);
   }

   /**
    @param tm : Treemap
    @param N : total number of documents in the corpus
    @param querySet : array of infix expression of the query
    
    This function keeps on calling getSolution() to get all document numbers which satisfies this condition.
    */
   public static void getAllSolutions(TreeMap<String, List<List<Integer>>> tm, int N, ArrayList<String> querySet)
   {
      int docId = -1;
      int newDocId = 0;
      ArrayList<ResultSet> resultSet = new ArrayList<ResultSet>();

      
      while(docId <= N)
      {
         ArrayList<String> newQuerySet = new ArrayList<String>(querySet);
         newDocId = getSolution(tm, N, newQuerySet, docId);
         

         if(newDocId <= N)
         {  
            if(newDocId != -1)
            {
               ResultSet res = new ResultSet();			
               res.docid = newDocId;
               res.score = (double)1/(newDocId+1);
               resultSet.add(res);
               
               docId = newDocId;
            }
            else
               docId++;

         }
         else
            break;

      }

      HandleResultSet.sortResultSetByScore(resultSet);	
      HandleResultSet.printResult(resultSet);
   }
	
   public static Boolean isOper(String str)
   {
      
      if(str.equals("+")||str.equals("-")||str.equals("*"))
         return true;
      else
         return false;
   }
	
   /**
    @param parseString : Input query string
    @return : returns arraylist of string which stores the infix expression
    @throws Exception
    
    This function just accepts the query string and converts it to infix expression
    and stores the result into arraylist of string.
    De More's law is also implemented in this which mean if there is
    NOT(A AND B) = ((NOT A) OR (NOT B)) 
    NOT(A OR B) = ((NOT A) AND (NOT B))
    
    */
   public static ArrayList<String> getInfixExpression(String parseString) throws Exception
   {

      Stack<String> infixStk = new Stack<String>();
      Stack<String> infixStkExpr = new Stack<String>();
      String token[], delimiter=" ",temp[] = null, temp2 = null, temp4 = null;
      ArrayList<String> Expression = new ArrayList<String>();
      
      if((parseString.contains("+") || parseString.contains("*") ||parseString.contains("-")))
      {
    	  System.out.println("It has operator");
      }
      else
      {
    	  System.out.println("It dont have operator");
      }
      
      temp=parseString.split(delimiter);
      String andString= "*(", orString= "+(",andString1= "*",orString1= "+";
      for(int i=0;i<temp.length;i++)
         infixStk.push(temp[i]);

      String temp1;
      while(infixStk.size()>=1)
      {

         while(!isOper(temp1=infixStk.pop()))
         {
            
            infixStkExpr.push(temp1);			
         }
         if(temp1.equals("*"))
         {
            String concatAND = "( " + infixStkExpr.pop() + " * " + infixStkExpr.pop() + " )";
            infixStkExpr.push(concatAND);
         }

         if(temp1.equals("-"))
         {
            
            Pattern pattern = Pattern.compile(infixStkExpr.pop());
            String temp3 = pattern.toString();
            
            if((pattern.toString()).contains(andString) || (pattern.toString()).contains(andString1) )
            {
               
               if((pattern.toString()).contains(andString))
                  temp4 = temp3.replace(andString, orString);
               
               else if((pattern.toString()).contains(andString1))
                  temp4 = temp3.replace(andString1, orString1);

               infixStkExpr.push(temp4);
            }
            else if((pattern.toString()).contains(orString) || (pattern.toString()).contains(andString1))
            {	
               
               if((pattern.toString()).contains(orString))
                  temp4 = temp3.replace(orString, andString);
               
               else if((pattern.toString()).contains(orString1))
                  temp4 = temp3.replace(orString1, andString1);

               infixStkExpr.push(temp4);
            }
            else
            {
               String temp41 = "(" + " - " + temp3 + " )";
               infixStkExpr.push(temp41);
            }
         }

         if(temp1.equals("+"))
         {
            String concatOR = "( " + infixStkExpr.pop() + " + " + infixStkExpr.pop() + " )";
            infixStkExpr.push(concatOR);
         }
      }
      String delimiter1 = " ";
      
      String infix = infixStkExpr.pop();
      String infixArray[]= infix.split(delimiter1 );

      for(int i=0;i<infixArray.length;i++)
         Expression.add(infixArray[i]);
      

      return (Expression);
   }
   /**
    @param tm : treemap
    @param N : number of documents in the corpus
    @param query : query string
    @throws Exception 
    
    This is the main function which actually calls other methods to get the results.
   */
   public static void getBooleanScore(TreeMap<String, List<List<Integer>>> tm, int N, String query) throws Exception
   {
      ArrayList<String> querySet;
      querySet = getInfixExpression(query);

      getAllSolutions(tm, N, querySet);

   }

}
