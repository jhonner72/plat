package com.fujixerox.aus.repository.util.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.fujixerox.aus.repository.AbstractComponentTest;

public class ImageExceptionComponentTest implements AbstractComponentTest {
	
	private String message = "error";
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldGetErrorMessage() {
		ImageException ex = new ImageException();
		assertEquals(null, ex.getMessage());
		
		ex = new ImageException(message);
		assertEquals(message, ex.getMessage());
	}

}
