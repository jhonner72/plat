package com.fujixerox.aus.lombard;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JaxbDateAdapterTest {

    @Test
    public void shouldMarshallAndUnmarshall() throws Exception {
        JaxbDateAdapter target = new JaxbDateAdapter();

        Date date = target.unmarshal("2015-03-12T09:30:00.123+11:00");
        assertThat(target.marshal(date), is("2015-03-12T09:30:00.123+11"));
    }
}