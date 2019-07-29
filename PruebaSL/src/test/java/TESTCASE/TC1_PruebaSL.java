package TESTCASE;

import org.testng.annotations.Test;
import FUNCTION.Functions;
import PAGE.*;
import PARAMETER.Parameters;

/**
 *Página caso de prueba .
 */
public class TC1_PruebaSL extends Parameters{
	
	/**
	 * test.
	 * @throws Exception RuntimeError
	 */
	@Test(priority = 1)
	public void TC1Test() throws Exception{	
			
		try {
			PruebaSL.PruebaSLM();
			
		} catch (Exception e) {
			PruebaSL.PruebaSLM();
		}
		Functions.createReporter();
	}
}