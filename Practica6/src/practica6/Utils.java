package practica6;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.HTMLEntityCodec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.EncodingException;

public class Utils {

	/**
	 * Canonicaliza la string de entrada
	 */
	public static String canonicalizar(String in){
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
		MySQLCodec codec = new MySQLCodec(MySQLCodec.Mode.STANDARD);
		return ESAPI.encoder().encodeForSQL(codec, in);
	}
	
	/**
	 * Codifica la string de entrada para HTML
	 */
	public static String codeHTML(String in){
		//TODO
		return ESAPI.encoder().encodeForHTML(in);
	}
	
	/**
	 * Codifica la string de entrada para URL
	 * @throws EncodingException 
	 */
	public static String codeURL(String in) throws EncodingException{
		//TODO
		return ESAPI.encoder().encodeForURL(in);
	}
}
