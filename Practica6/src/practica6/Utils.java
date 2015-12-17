package practica6;

import org.owasp.esapi.ESAPI;

public class Utils {

	/**
	 * Canonicaliza la string de entrada
	 */
	public static String canonicalizar(String in){
		// TODO
		return ESAPI.encoder().canonicalize(in);
	}
	
	/**
	 * Valida la string de entrada
	 */
	public static boolean validar(String in){
		//TODO
		return false;
	}
	
	/**
	 * Codifica la string de entrada para SQL
	 */
	public static String codeSQL(String in){
		//TODO
		return null;
	}
	
	/**
	 * Codifica la string de entrada para HTML
	 */
	public static String codeHTML(String in){
		//TODO
		return null;
	}
	
	/**
	 * Codifica la string de entrada para URL
	 */
	public static String codeURL(String in){
		//TODO
		return null;
	}
}
