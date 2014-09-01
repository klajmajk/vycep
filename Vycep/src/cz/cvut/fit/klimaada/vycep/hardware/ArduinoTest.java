package cz.cvut.fit.klimaada.vycep.hardware;

import static org.junit.Assert.*;

import org.junit.Test;

public class ArduinoTest {

	@Test
	public void testGetPouredCount() {
		Arduino arduino = new Arduino();
		arduino.getPouredCount("1:2:3:1");
		assertEquals(5, arduino.getPouredCount("1:2:3:1"));
	}

}
