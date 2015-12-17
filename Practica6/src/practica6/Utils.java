package practica6;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.EncodingException;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.reference.DefaultValidator;

public class Utils {

	/**
	 * Canonicaliza la string de entrada
	 */
	public static String canonicalizar(String in){
		return ESAPI.encoder().canonicalize(in);
	}
	
	/**
	 * Valida la string de entrada
	 * 
	 * ESAPI.properties y validation.properties han de estar
	 * en la carpeta "esapi" en el directorio del usuario.
	 * En ESAPI.properties han de estar:
	 * 
	 * Validator.Nombre=^[a-zA-Z0-9&-'.]{1,50}$
	 * Validator.Direccion=^[a-zA-z0-9&-',./\u00BA]{1,50}$
	 * Validator.TarjetaCredito=^(MC|VISA|AMEX)[0-9]{16}[0-9]{2}[0-9]{4}[0-9]{3}
	 * Validator.DNI=^[0-9A-Z]{9}
	 */
	public static boolean validar(String in, String field){
		DefaultValidator dfv = new DefaultValidator();
		try{
			boolean validado = dfv.isValidInput(null, in, field, -1, true);
			if(!validado) throw new IntrusionException("False validation", "False validation");
			return validado;
		}
		catch(IntrusionException e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Codifica la string de entrada para SQL
	 */
	public static String codeSQL(String in){
		MySQLCodec codec = new MySQLCodec(MySQLCodec.Mode.STANDARD);
		return ESAPI.encoder().encodeForSQL(codec, in);
	}
	
	/**
	 * Codifica la string de entrada para HTML
	 */
	public static String codeHTML(String in){
		return ESAPI.encoder().encodeForHTML(in);
	}
	
	/**
	 * Codifica la string de entrada para URL
	 * @throws EncodingException 
	 */
	public static String codeURL(String in) throws EncodingException{
		return ESAPI.encoder().encodeForURL(in);
	}
}
