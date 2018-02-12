package test.alphapone.LSPJ;

import org.alphapone.LSPJ.SQLMacro;
import org.junit.Test;


public class TestSQLMacro {

   private static void checkParse(String sql, String expected) {
      SQLMacro.StatementPart[] sp = SQLMacro.parse(sql);
      String result = SQLMacro.StatementPart.aToS(sp);
      System.out.println("SQL:" + sql);
      System.out.println("result:" + result);
      assert(result.equals(expected));
   }

   @Test
   public void inv() {
      System.out.println("SQLMacro test");
      checkParse("select * from dual","[{\"content\":\"select * from dual\",\"name\":null,\"type\":\"TEXT\"}]");
      checkParse("select * from dual where dummy=$1 or $1 is null","[{\"content\":\"select * from dual where dummy=\",\"name\":null,\"type\":\"TEXT\"},{\"content\":\"1\",\"name\":null,\"type\":\"VARIABLE\"},{\"content\":\" or \",\"name\":null,\"type\":\"TEXT\"},{\"content\":\"1\",\"name\":null,\"type\":\"VARIABLE\"},{\"content\":\" is null\",\"name\":null,\"type\":\"TEXT\"}]");
      System.out.println("SQLMacro done");
   }
}
