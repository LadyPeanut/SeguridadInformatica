package practica6;

import java.util.Scanner;

import org.owasp.esapi.errors.EncodingException;

public class Leer {
	
	/**
	 * Uso: Leer -v -c -e[SQL|HTML|URL]
	 */
	public static void main(String[] args){
		boolean validar = false;
		boolean canonicalizar = false;
		boolean[] codificar = {false, false, false};
		
		/* argumentos */
		if(args.length > 0){
			for (int i = 0; i < args.length; i++) {
				String s = args[i];
				
				if(s.equals("-v")){
					validar = true;
				}
				if(s.equals("-c")){
					canonicalizar = true;
				}
				if(s.equals("-e")){
					s = args[++i];
					if(s.equals("SQL")){
						codificar[0] = true;
					}
					if(s.equals("HTML")){
						codificar[1] = true;
					}
					if(s.equals("URL")){
						codificar[2] = true;
					}
				}
			}
		}

		Scanner s = new Scanner(System.in);
		int opcion = 0;
		while(s.hasNextLine()){
			String in = s.nextLine();
			
			if(canonicalizar){
				in = Utils.canonicalizar(in);
				System.out.println("Canonalizado: " + in);
			}
			if(validar){	// TODO logger?
				boolean valido = false;
				/* ESAPI.properties */
				switch(opcion){
				case 0:	// nombre
					valido = Utils.validar(in, "Nombre");
					break;
				case 1:	// direccion
					valido = Utils.validar(in, "Direccion");
					break;
				case 2:	// tarjeta de credito
					valido = Utils.validar(in, "TarjetaCredito");
					break;
				case 3:	// DNI
					valido = Utils.validar(in, "DNI");
					break;
				}
				if(!valido) continue;
			}
			if(codificar[0]){	// TODO comprobar que funciona
				String SQLin = Utils.codeSQL(in);
				System.out.println("SQL encoding: " + SQLin);
			}
			if(codificar[1]){	// TODO comprobar que funciona
				String HTMLin = Utils.codeHTML(in);
				System.out.println("HTML encoding: " + HTMLin);
			}
			if(codificar[2]){	// TODO comprobar que funciona
				try {
					String URLin = Utils.codeURL(in);
					System.out.println("URL Encoding: " + URLin);
				} catch (EncodingException e) {
					e.printStackTrace();
				}
			}
			
			opcion = opcion % 4;
		}
		s.close();
	}

}
