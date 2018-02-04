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
      System.out.println("SQLMacro done");
   }
}
