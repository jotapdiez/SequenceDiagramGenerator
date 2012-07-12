package org.testplugin.utils;

public class Persona {

	CajeroAutomatico cajero = null;
	public Persona() {
		cajero = new CajeroAutomatico();
	}
	
	public void hacerTransferencia() throws Exception
	{
		boolean aceptada = meterTarjeta();
		if (!aceptada)
			throw new Exception("Tarjeta invalida");
		
		aceptada = ingresarClave();
		if (!aceptada)
			throw new Exception("Clave invalida");

	}

	private boolean ingresarClave() {
		return cajero.ingresaClave("1234");
	}

	private boolean meterTarjeta() {
		return cajero.isTarjetaValida();
	}
}
