package practica6;

import java.util.Scanner;

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
		while(s.hasNextLine()){
			String in = s.nextLine();
			
			if(canonicalizar){	// TODO canonicalizar
				in = Utils.canonicalizar(in);
			}
			if(validar){	// TODO validar
				boolean valido = Utils.validar(in);
			}
			if(codificar[0]){	// TODO codificar SQL
				String SQLin = Utils.codeSQL(in);
			}
			if(codificar[1]){ // TODO codificar HTML
				String HTMLin = Utils.codeHTML(in);
			}
			if(codificar[2]){	// TODO codificar URL
				String URLin = Utils.codeURL(in);
			}
		}
		s.close();
	}

}
