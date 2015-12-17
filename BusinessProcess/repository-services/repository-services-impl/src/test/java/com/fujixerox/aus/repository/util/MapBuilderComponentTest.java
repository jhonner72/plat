package com.fujixerox.aus.repository.util;

import com.fujixerox.aus.repository.AbstractComponentTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/** 
 * Henry Niu
 * 2/4/2015
 */
public class MapBuilderComponentTest implements AbstractComponentTest {
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldBuildMap() {
    	Map<String, String> map = new MapBuilder().put("key1", "value1").put("key2", "value2").put("key3", "value3").build();  
    	assertEquals(3, map.keySet().size());
    	assertEquals("value1", map.get("key1"));
    	assertEquals("value2", map.get("key2"));
    	assertEquals("value3", map.get("key3"));    	
    }
}
