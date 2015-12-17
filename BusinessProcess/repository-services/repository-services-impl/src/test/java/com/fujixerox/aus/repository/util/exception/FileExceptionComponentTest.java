package com.fujixerox.aus.repository.util.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.fujixerox.aus.repository.AbstractComponentTest;

public class FileExceptionComponentTest implements AbstractComponentTest {
	
	private String message = "error";
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldGetErrorMessage() {
		FileException ex = new FileException();
		assertEquals(null, ex.getMessage());
		
		ex = new FileException(message);
		assertEquals(message, ex.getMessage());

	}

}
