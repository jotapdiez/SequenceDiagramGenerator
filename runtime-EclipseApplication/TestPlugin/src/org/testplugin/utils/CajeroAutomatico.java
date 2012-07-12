package org.testplugin.utils;

public class CajeroAutomatico {

	public boolean isTarjetaValida() {
		return true;
	}

	public boolean ingresaClave(String string) {
		if (string.equals("1234"))
			return true;
		return false;
	}

}
