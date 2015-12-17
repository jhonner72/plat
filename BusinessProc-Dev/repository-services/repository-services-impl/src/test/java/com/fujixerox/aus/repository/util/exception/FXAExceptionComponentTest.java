package com.fujixerox.aus.repository.util.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.fujixerox.aus.repository.AbstractComponentTest;

public class FXAExceptionComponentTest implements AbstractComponentTest {
	
	private String message = "error";
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldGetErrorMessage() {
		FXAException ex = new FXAException(message);
		assertEquals(message, ex.getMessage());

	}

}
