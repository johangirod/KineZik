package modele.test;

import junit.framework.Assert;
import modele.Installation;

import org.junit.Test;

public class InstallationTest {

	@Test
	public final void testIsCorrect() {
		Assert.assertTrue(! Installation.isCorrect(null));
		Assert.assertTrue(! Installation.isCorrect(""));
		Assert.assertTrue(! Installation.isCorrect("AB223323-A238-2817-b123-128391029381"));
		Assert.assertTrue(! Installation.isCorrect("12345678-abcd-effe-ab90-12345678abcd-"));
		Assert.assertTrue(! Installation.isCorrect("12345678-abcd-effe-ab90-12345678abcd1"));
		Assert.assertTrue(! Installation.isCorrect("12345678-abcd-effe-dc12-123478abcd1"));
		Assert.assertTrue(! Installation.isCorrect("123456738-abcd-effe-dc12-123478abcd1"));
		Assert.assertTrue(! Installation.isCorrect("123456738-abcd-efe-ab90-123478abcd1"));
		Assert.assertTrue(! Installation.isCorrect("123456738-abc2d-efe-dc12-123478abcd1"));
		
		
		
		
		Assert.assertTrue(Installation.isCorrect("ab223323-a238-2817-b182-128391029381"));	
		Assert.assertTrue(Installation.isCorrect("12345678-abcd-effe-dc12-12345678abcd"));
	}

}
