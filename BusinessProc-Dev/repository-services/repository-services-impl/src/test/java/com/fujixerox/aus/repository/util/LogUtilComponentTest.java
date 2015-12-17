package com.fujixerox.aus.repository.util;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.fujixerox.aus.repository.AbstractComponentTest;

/** 
 * Henry Niu
 * 02/04/2015
 */
public class LogUtilComponentTest implements AbstractComponentTest {

	@Test
    @Category(AbstractComponentTest.class)
    public void shouldLogMessage() {
		LogUtil.log("Debug message", LogUtil.DEBUG, null);
		LogUtil.log("Info message", LogUtil.INFO, null);
		//LogUtil.log("Error message", LogUtil.ERROR, new NullPointerException());
		LogUtil.log("Warning message", LogUtil.WARN, null);
	}
}