package modele.test;

import modele.BayesianTable;
import junit.framework.TestCase;




public class BayesianTableTest extends TestCase {

	public void testSerialization(){
		BayesianTable tester = new BayesianTable(2, 2, 3, 0);
		try{

			new BayesianTable(tester.toJSONString());
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
