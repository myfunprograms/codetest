package codetest.domain;

import org.junit.Assert;
import org.junit.Test;

public class StateCodeTest {
	@Test
	public void test() {
		Assert.assertEquals(StateCode.INACTIVE, StateCode.getByCode("Inactive"));
	}
}
