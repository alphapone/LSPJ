package org.alphapone.LSPJ;

import java.util.ArrayList;

/**
 * Utility class for loading SQL from local resource,
 *    substitute parameters in SQL and
 *    processing including of SQL files like as a stored procedure
 *
 * @author: alphapone <inl@yandex.com>
 */
public class SQLMacro {

   static enum StatementPartType {
      TEXT,
      VARIABLE,
      INCLUDE
   }

   public static class StatementPart {
      /**
       * Content of statement part
       */
      StringBuilder content;
      /**
       * name: actual for variables and includes only
       */
      String name;
      /**
       * arguments of include: actual for includes only
       */
      String[] arguments;
      /**
       * Type of the statement part
       */
      StatementPartType type;
      
      StatementPart(StatementPartType type) {
      	  this.type = type;
      	  content = new StringBuilder();
      }
      
      /**
       * Print array to string
       */
      public static String aToS(StatementPart[] a) {
      	  StringBuilder sb = new StringBuilder();
      	  if (a!=null) {
      	  	  sb.append('[');
      	  	  for (int i=0, l=a.length; i<l; i++) {
      	  	  	  if (i>0) {
      	  	  	  	  sb.append(',');
      	  	  	  }
      	  	  	  if (a[i]!=null) {
      	  	  	  	  sb.append(a[i].toString());
      	  	  	  } else {
      	  	  	  	  sb.append("null");
      	  	  	  }
      	  	  }
      	  	  sb.append(']');
      	  }
      	  return sb.toString();
      }
      
      @Override
      public String toString() {
      	  StringBuilder sb = new StringBuilder();
      	  sb.append("{\"content\":");
      	  sb.append('"');
      	  sb.append(content);
      	  sb.append("\",\"name\":");
      	  if (name!=null) {
      	  	  sb.append('"');
      	  	  sb.append(name);
      	  	  sb.append('"');
      	  } else {
      	  	  sb.append("null");
      	  }
      	  sb.append(",\"type\":\"");
      	  sb.append(type);
      	  sb.append('"');
      	  sb.append("}");
      	  return sb.toString();
      }
   }
   
   /**
    * Symbol position in source text
    */
   static enum TextPosition {
   	   TEXT,
   	   COMMENT1START, /* block comment */
   	   COMMENT1,
   	   COMMENT1END,
   	   COMMENT2START, /* line comment */
   	   COMMENT2,
   	   STRING,
   	   STRINGEND,
   	   IDENTIFIER,
   	   VARINSTART,   /* start of variable or include */
   	   VARIABLE,     /* $name */
   	   INCLUDE       /* $[file arg1 agr2 arg2] */
   }
   
   /**
    * Split SQL text to statement parts
    */
   public static StatementPart[] parse(String sql) {
   	   final char EOFC = (char)-1;
   	   ArrayList<StatementPart> parts = new ArrayList<>();
   	   TextPosition position = TextPosition.TEXT;
   	   
   	   char pc = EOFC;
   	   StatementPart curr = null;
   	   
   	   for (int i=0,l=sql.length(); i<l; i++) {
   	   	   if (pc!=EOFC) {
   	   	   	   switch (position) {
   	   	   	   case TEXT:
   	   	   	   case COMMENT1START:
   	   	   	   case COMMENT1END:
   	   	   	   case COMMENT2START:
   	   	   	   case COMMENT2:
   	   	   	   case STRING:
   	   	   	   case STRINGEND:
   	   	   	   case IDENTIFIER:
   	   	   	   	   if (curr==null) {
   	   	   	   	   	   curr = new StatementPart(StatementPartType.TEXT);
   	   	   	   	   }
   	   	   	   	   if (curr.type != StatementPartType.TEXT) {
   	   	   	   	   	   curr.content.append(pc);
   	   	   	   	   	   parts.add(curr);
   	   	   	   	   	   curr = new StatementPart(StatementPartType.TEXT);
   	   	   	   	   }
   	   	   	   	   curr.content.append(pc);
   	   	   	   	   break;
   	   	   	   case VARIABLE:
   	   	   	   	   if (curr==null) {
   	   	   	   	   	   curr = new StatementPart(StatementPartType.VARIABLE);
   	   	   	   	   }
   	   	   	   	   if (curr.type != StatementPartType.VARIABLE) {
   	   	   	   	   	   curr.content.append(pc);
   	   	   	   	   	   parts.add(curr);
   	   	   	   	   	   curr = new StatementPart(StatementPartType.VARIABLE);
   	   	   	   	   }
   	   	   	   	   curr.content.append(pc);
   	   	   	   	   break;
   	   	   	   case INCLUDE:
   	   	   	   	   if (curr==null) {
   	   	   	   	   	   curr = new StatementPart(StatementPartType.INCLUDE);
   	   	   	   	   }
   	   	   	   	   if (curr.type != StatementPartType.INCLUDE) {
   	   	   	   	   	   curr.content.append(pc);
   	   	   	   	   	   parts.add(curr);
   	   	   	   	   	   curr = new StatementPart(StatementPartType.INCLUDE);
   	   	   	   	   }
   	   	   	   	   curr.content.append(pc);
   	   	   	   	   break;
   	   	   	   	   
   	   	   	   }
   	   	   }
   	   	   char c = sql.charAt(i);
   	   	   switch(position) {
   	   	   case TEXT:
			   switch (c) {
			   case '/': 
			   	   position = TextPosition.COMMENT1START; 
			   	   break;
			   case '-': 
			   	   position = TextPosition.COMMENT2START;
			   	   break;
			   case '\'':
			   	   position = TextPosition.STRING;
			   	   break;
			   case '$':
			   	   position = TextPosition.VARINSTART;
			   	   break;
			   case '"':
			   	   position = TextPosition.IDENTIFIER;
			   	   break;
			   }
			   break;
		   case COMMENT1START:
		   	   switch(c) {
		   	   case '*':
		   	   	   position = TextPosition.COMMENT1;
		   	   	   break;
		   	   default:
		   	   	   position = TextPosition.TEXT;
		   	   }
		   	   break;
		   case COMMENT1:
		   	   switch (c) {
		   	   case '*':
		   	   	   position = TextPosition.COMMENT1END;
		   	   	   break;
		   	   }
		   	   break;
		   case COMMENT1END:
		   	   switch(c) {
		   	   case '/':
		   	   	   position = TextPosition.TEXT;
		   	   	   break;
		   	   default:
		   	   	   position = TextPosition.COMMENT1;
		   	   }
		   	   break;
		   	case COMMENT2:
		   		switch(c) {
		   		case '\n':
		   			position = TextPosition.TEXT;
		   			break;
		   		}
		   		break;
		   	case STRING:
		   		switch(c) {
		   		case '\'':
		   			position = TextPosition.STRINGEND;
		   			break;
		   	    }
		   	    break;
		   	case STRINGEND:
		   		switch(c) {
		   		case '\'':
		   			position = TextPosition.STRING;
		   			break;
		   	    default:
		   	    	position = TextPosition.TEXT;
		   	    }
		   	    break;
		   	case IDENTIFIER:
		   		switch(c) {
		   		case '"':
		   			position = TextPosition.TEXT;
		   			break;
		        }
		        break;
		   	case VARINSTART:
		   		switch(c) {
		   		case '[':
		   			position = TextPosition.INCLUDE;
		   			break;
		   		default:
		   			position = TextPosition.VARIABLE;
		   			break;
		        }
		        break;
		   	case VARIABLE:
		   		switch(c) {
		   		case '0':
		   		case '1':
		   		case '2':
		   		case '3':
		   		case '4':
		   		case '5':
		   		case '6':
		   		case '7':
		   		case '8':
		   		case '9':
		   			position = TextPosition.VARIABLE;
		   			break;
		   		default:
		   			position = TextPosition.TEXT;
		   			break;
		        }
		        break;
		    case INCLUDE:
		    	switch(c) {
		    	case ']':
		    		position = TextPosition.TEXT;
		        }
		        break;
		   }
		   pc = c;
   	   }
   	   if (curr!=null) {
   	   	   if (pc!=EOFC) {
   	   	   	   curr.content.append(pc);
   	   	   }
   	   	   parts.add(curr);
   	   }	   
   	   return parts.toArray(new StatementPart[parts.size()]);
   }

}
