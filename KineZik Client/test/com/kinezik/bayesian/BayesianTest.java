package bayesian;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Test;

public class BayesianTest extends TestCase{

	@Test
	public void bayesianTest() {
		Context context= getContext();
		String tables ="tables"
		storeBayesianTables(context,tables);
		
	}

}
