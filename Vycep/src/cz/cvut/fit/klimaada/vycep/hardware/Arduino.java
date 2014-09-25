package cz.cvut.fit.klimaada.vycep.hardware;

import android.content.Intent;
import android.util.Log;

public class Arduino {
	private String remaining;
	private long test;
	private int arduinoTest;

	private final static String DATA_EXTRA = "primavera.arduino.intent.extra.DATA";

	public Arduino() {
		super();
		this.remaining = "";
	}

	public static Intent sendOpen() {
		return sendString("open");
	}

	public static Intent sendClose() {
		return sendString("Close");
	}

	private static Intent sendString(String string) {
		Intent intent = new Intent();
		intent.setAction("primavera.arduino.intent.action.SEND_DATA");
		intent.putExtra("primavera.arduino.intent.extra.DATA",
				string.getBytes());
		return intent;

	}

	private static String getData(Intent intent) {
		// TODO Auto-generated method stub
		final byte[] data = intent.getByteArrayExtra(DATA_EXTRA);
		return new String(data);
	}

	public int getPoured(Intent intent) {
		return getPouredCount(getData(intent));
	}


	public int getPouredCount(String in) {
		//Log.d("ARDUINO", "in"+in);
		String input = remaining + in;
		//Log.d("ARDUINO", "inptu: "+input);
		if (input.charAt(input.length() - 1) != ':') {
			int index = input.lastIndexOf(":");
			if (index != -1) {
				remaining = input.substring(index);
				input = input.substring(0, index);
			} else
				return 0;
		}
		if (input.length() > 1) {
			String ss = input.substring(0, input.length() - 1);
			//Log.d("ARDUINO", "ss:"+ss);
			String[] numbers = ss.split(":");

			//Log.d("ARDUINO", "numbers:"+numbers);
			int sum = 0;
			for (String string : numbers) {
				sum += Integer.parseInt(string);
			}
			test += sum;
			//Log.d("ARDUINO", "test:"+test);
			return sum;
		}
		return 0;

	}
	
	/*private String getStringToParse(String in) {

		String input = remaining + in;
		//System.out.println("ARDUINO" + "inputBefore:" + input);
		int index = input.lastIndexOf(";");
		if (index != -1) {
			remaining = input.substring(index);
			input = input.substring(0, index);
			return input;
		} else
			return "";
	}

	public int getPouredCount(String in) {
		int sum = 0;
		String toParse = getStringToParse(in);
		if (toParse.length() > 0) {
			String[] pairs = toParse.split(";");
			for (String pair : pairs) {
				if (pair.length() != 0) {
					if (pair.length() >= 3) {
						sum += parsePair(pair);
					} else {
						System.out
								.println("Parsing errror not long enough for pair: "
										+ pair);
					}
				}
			}
			System.out.println("Poured: "+ sum+ " test: "+ test + " arduinoTest: " +arduinoTest);
			return sum;
		} else
			return 0;

	}

	
	private int parsePair(String pair) {

		String[] numbers = pair.split(":");
		if (numbers.length == 2) {
			int poured = Integer.parseInt(numbers[0]);
			test += poured;
			arduinoTest = Integer.parseInt(numbers[1]);;
			return poured;
		} else {
			System.out.println("Parsing errror not a pair: " + pair);
			return 0;
		}

	}*/
}
