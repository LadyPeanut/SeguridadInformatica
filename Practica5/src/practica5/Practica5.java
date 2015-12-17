/*
 * Package: practica5
 * Class: Practica5
 * Authors:
 * 	Patricia Lazaro Tello (554309)
 * 	Sandra Malpica Mallo (670607)
 */

/* HASH
 * ==================================================================
 * Utilizamos MD5 con tamanio de clave 5.000 bytes.
 * Se utiliza MD5 porque es el algoritmo mas comun para la generacion
 * de hashes.
 */

/*
 * GENERACION DE CLAVES PUBLICA/PRIVADA
 * ==================================================================
 * Utilizamos RSA con tamanios de clave 1.024, 2.048 y 3.072.
 * Se utiliza RSA porque es el mecanismo habitual de cifrado con
 * clave publica y privada.
 * El SecureRandom usa el PseudoRandomNumberGenerator (PRNG) de SHA1.
 * 
 * 
 */

/*
 * GENERACION DE CLAVE SECRETA
 * ==================================================================
 * Utilizamos Blowfish para el cifrado simetrico porque es mas seguro 
 * que DES y mas rapido que AES para el mismo tamaño
 */
 //TODO: explicaciones
package practica5;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

public class Practica5 {

	/*
	 * Se experimenta con las funciones de seguridad de Java Cryptography
	 * Extension (JCE) para: - Obtener un hash - Generar claves de cifrado
	 * (publica y privada) y almacenarlas - Cifrar y descifrar con clave publica
	 * y privada (en el caso de la publica, tamanio maximo del mensaje que se
	 * puede cifrar) - Firma digital
	 * 
	 * Se medira el tiempo de generacion de claves para tres tamanios diferentes
	 * de claves - la que viene en el programa, el doble y el triple).
	 * 
	 * Se calculara la media de los tiempos (se ejecutara varias veces cada
	 * algoritmo).
	 * 
	 * Se mediran tambien los tiempos de firmado, hash y firma digital, y el
	 * tiempo de cifrado y descifrado para docs de texto con criptografia de
	 * clave secreta y para una lista de cien mensajes aleatorios del tamanio
	 * maximo determinado anteriormente y comparar los tiempos para la
	 * criptografia de clave publica y secreta.
	 */
	// TODO: limpiar codigo
	private final static String publicasFile = "clavePublica";
	private final static String privadasFile = "clavePrivada";
//	private final static String cifrado = "practica5";
	private final static int tamRSA = 1024 * 3;

	/**
	 * @param args:
	 *            entero para determinar el numero de veces que se ejecuta cada
	 *            funcion, el path de la carpeta donde se encuentran los docs
	 *            que cifrar y descifrar.
	 * @throws NoSuchAlgorithmException
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeyException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		int n = -1;
		String path = null;

		/* parametros */
		for (int i = 0; i < args.length; i++) {
			String s = args[i];
			if (s.equals("-iters")) { // numero de veces que se ejecuta cada
										// funcion
				i++;
				try {
					n = Integer.parseInt(args[i]);
				} catch (Exception e) {
					System.err.println("Usage: java Practica5 -iters <num> -path <path>");
					System.exit(1);
				}

			} else if (s.equals("-path")) { // ruta al directorio donde se
											// guardan los documentos que cifrar
											// y descifrar
				i++;
				try {
					path = args[i];
				} catch (Exception e) {
					System.err.println("Usage: java Practica5 -iters <numDocs> -path <path>");
					System.exit(1);
				}
			}
		}

		if (n < 0 || path == null) {
			System.err.println("Usage: java Practica5 -iters <numDocs> -path <path>");
			System.exit(1);
		}
		
		hash(n); // Obtener hash
		
		KeyPair[] kps = generarClaves(n); // Generacion de claves
		almacenar(kps); // Almacenamiento de claves
		
		try {
			readPublicKey(0); // recuperacion de claves
			readPrivateKey(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		// cifrado de textos
		 firma(n);
		 
		 int maxTam = obtenerMaxTam();
		 comparativasCifrado(maxTam);
		 comparativasCifrado();
	}

	/**
	 * The RSA algorithm can only encrypt data that has a maximum byte length of
	 * the RSA key length in bits divided with eight minus eleven padding bytes,
	 * i.e. number of maximum bytes = key length in bits / 8 - 11. El tamaño
	 * maximo de mensaje que se puede descifrar con RSA depende del tamaño de la
	 * clave generada
	 * 
	 */
	private static int obtenerMaxTam() {
		return (tamRSA / 8) - 11;
	}

	private static void firma(int n) {
		long[] times = new long[n];
		try {
			for (int i = 0; i < n; i++) {
				byte[] mensaje = generateMessage(256).getBytes("UTF-8");
				
				long t0 = System.nanoTime();
				firma(mensaje, mensaje.length);
				long t1 = System.nanoTime();
				
				times[i] = (long) ((t1 - t0) / 1000000);
			}
			
			long time = 0;
			for (int i = 0; i < n; i++) {
				time += times[i];
			}
			time = time / n;
			
			System.out.println("FIRMA) Tiempo: " + time + " milisegundos");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * firma un mensaje con clave RSA
	 * 
	 * @param mensaje
	 * @param size
	 */
	private static void firma(byte[] mensaje, int size) {
		// simulación de firma digital del mensaje con clave RSA de tamaño size
		// firma del mensaje con la clave pública del receptor
		
		// TODO: calcular tiempos aquí. Aunque es estupido, hacer solo el hash
		// siempre sera mas rapido que todo esto
		int tam = 1024 * 4;
		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(tam);
			KeyPair pareja = generator.generateKeyPair();
			PublicKey pk = pareja.getPublic();
			PrivateKey rk = pareja.getPrivate();

			// resumen del mensaje
			MessageDigest md5;
			try {
				md5 = MessageDigest.getInstance("MD5");
				md5.update(mensaje);
				byte[] hashBytes = md5.digest(); // crear hash

				if (hashBytes.length > (tam / 8) - 11) {
					System.out.println("Tamaño maximo sobrepasado para este tamaño de clave de RSA");
					System.out.println("tamaño clave: " + tam + ", tamaño mensaje: " + hashBytes.length
							+ ", tamaño maximo: " + (tam / 8 - 11));
				} else {

					Cipher cipher;
					try {
						cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
						/* se manda la firma encriptada con la privada */
						cipher.init(Cipher.ENCRYPT_MODE, rk);
						byte[] encryptedBytes = cipher.doFinal(hashBytes);
						/* la firma se desencripta con la publica del remitente */
						cipher.init(Cipher.DECRYPT_MODE, pk);
						byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
						/* 
						 * comparar el hash del mensaje que nos ha llegado con el 
						 * desencriptado de la firma comparar los 
						 * resumenes (si son iguales, todo es correcto) 
						 */
						boolean exito = true;
						for (int i = 0; i < hashBytes.length; i++) { // comparar bit a bit
							if (hashBytes[i] != decryptedBytes[i]) {
								exito = false;
							}
						}
						if (hashBytes.length != decryptedBytes.length) {
							exito = false;
						}
						if (exito) {
//							System.out.println("Mensaje correcto");
						} else {
							System.out.println("Algo ha ido mal");
						}
					} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {

						e.printStackTrace();
					} catch (InvalidKeyException e) {

						e.printStackTrace();
					} catch (IllegalBlockSizeException e) {

						e.printStackTrace();
					} catch (BadPaddingException e) {

						e.printStackTrace();
					}

				}
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e2) {
			e2.printStackTrace();
		}

	}

	/**
	 * Genera 100 mensajes aleatorios, los cifra y descifra con cifrado
	 * simetrico y asimetrico y muestra una comparativa de los tiempos
	 * 
	 * @param maxTam
	 * @throws NoSuchAlgorithmException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeyException 
	 */
	private static void comparativasCifrado(int maxTam) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		long[] cifradoSimetrico = new long[100];
		long[] descifradoSimetrico = new long[100];
		
		long[] cifradoASimetrico = new long[100];
		long[] descifradoAsimetrico = new long[100];
		
		long mediaCifradoSimetrico = 0, mediaCifradoAsimetrico = 0, 
			mediaDesSimetrico = 0, mediaDesAsimetrico = 0;
		for (int i = 0; i < 100; i++) {
			try {
				byte[] rawBytes = generateMessage(maxTam).getBytes("UTF-8");
				
				Key simetrica = getClaveSimetrica();
				KeyPair asimetrica = getClaveAsimetrica();
				
				// cifrar asimetrico
				long t0 = System.nanoTime();
				byte[] simEnc = cifrarSimetrico(rawBytes, simetrica);
				long t1 = System.nanoTime();
				long tiempo = (long) ((t1 - t0) / 1000000);
				cifradoSimetrico[i] = tiempo;
				
				// descifrar simetrico
				t0 = System.nanoTime();
				descifrarSimetrico(simEnc, simetrica);
				t1 = System.nanoTime();
				tiempo = (long) ((t1 - t0) / 1000000);
				descifradoSimetrico[i] = tiempo;
				
				// cifrar asimetrico
				t0 = System.nanoTime();
				byte[] asimEnc = cifrarAsimetrico(rawBytes, asimetrica);
				t1 = System.nanoTime();				
				tiempo = (long) ((t1 - t0) / 1000000);
				cifradoASimetrico[i] = tiempo;
				
				// descifrar asimetrico
				t0 = System.nanoTime();
				descifrarAsimetrico(asimEnc, asimetrica);
				t1 = System.nanoTime();
				tiempo = (long) ((t1 - t0) / 1000000);
				descifradoAsimetrico[i] = tiempo;

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < 100; i++) {
			mediaCifradoAsimetrico += cifradoASimetrico[i];
			mediaCifradoSimetrico += cifradoSimetrico[i];
			mediaDesAsimetrico += descifradoAsimetrico[i];
			mediaDesSimetrico += descifradoSimetrico[i];
		}
		
		mediaCifradoAsimetrico = (long) (mediaCifradoAsimetrico / 100);
		mediaCifradoSimetrico = (long) (mediaCifradoSimetrico / 100);
		mediaDesAsimetrico = (long) (mediaDesAsimetrico / 100);
		mediaDesSimetrico = (long) (mediaDesSimetrico / 100);
		
		System.out.println("CIFRADO 100)(clave publica-privada) Tiempo: " + mediaCifradoAsimetrico + " milisegundos");
		System.out.println("DESCIFRADO 100)(clave publica-privada) Tiempo: " + mediaDesAsimetrico + " milisegundos");
		System.out.println("CIFRADO 100)(clave privada) Tiempo: " + mediaCifradoSimetrico + " milisegundos");
		System.out.println("DESCIFRADO 100)(clave privada) Tiempo: " + mediaDesSimetrico + " milisegundos");
	}
	
	private static void comparativasCifrado() throws 
			NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, 
			IllegalBlockSizeException, BadPaddingException, IOException {
		long[] cifradoSimetrico = new long[100];
		long[] descifradoSimetrico = new long[100];
		
		long mediaCifradoSimetrico = 0, mediaDesSimetrico = 0;
		for (int i = 0; i < 25; i++) {
			try {
				String fichero = "textos/wiki" + (i + 1) + ".txt";
				Path path = Paths.get(fichero);
				byte[] rawBytes = Files.readAllBytes(path);
				
				Key simetrica = getClaveSimetrica();
				
				// cifrar asimetrico
				long t0 = System.nanoTime();
				byte[] simEnc = cifrarSimetrico(rawBytes, simetrica);
				long t1 = System.nanoTime();
				long tiempo = (long) ((t1 - t0) / 1000000);
				cifradoSimetrico[i] = tiempo;
				
				// descifrar simetrico
				t0 = System.nanoTime();
				descifrarSimetrico(simEnc, simetrica);
				t1 = System.nanoTime();
				tiempo = (long) ((t1 - t0) / 1000000);
				descifradoSimetrico[i] = tiempo;

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < 100; i++) {
			mediaCifradoSimetrico += cifradoSimetrico[i];
			mediaDesSimetrico += descifradoSimetrico[i];
		}
		
		mediaCifradoSimetrico = (long) (mediaCifradoSimetrico / 100);
		mediaDesSimetrico = (long) (mediaDesSimetrico / 100);
		
		System.out.println("CIFRADO 25W)(clave secreta) Tiempo: " + mediaCifradoSimetrico + " milisegundos");
		System.out.println("DESCIFRADO 25W)(clave secreta) Tiempo: " + mediaDesSimetrico + " milisegundos");
	}
	
	private static KeyPair getClaveAsimetrica() throws NoSuchAlgorithmException{
		// obtener generador de claves
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		kpg.initialize(tamRSA, sr);
		KeyPair kp = kpg.generateKeyPair(); // obtiene par de claves
		return kp;
	}
	
	private static Key getClaveSimetrica() throws NoSuchAlgorithmException{
		KeyGenerator generator = KeyGenerator.getInstance("Blowfish");
		generator.init(128);
		Key clave = generator.generateKey();
		return clave;
	}
	
	private static byte[] cifrarSimetrico(byte[] msg, Key k) throws NoSuchAlgorithmException, 
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, 
			BadPaddingException{
		Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, k);
		byte[] encryptedBytes = cipher.doFinal(msg);
		return encryptedBytes;
	}
	
	private static byte[] descifrarSimetrico(byte[] msg, Key k) throws NoSuchAlgorithmException, 
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, 
			BadPaddingException{
		Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, k);
		byte[] bytes = cipher.doFinal(msg);
		return bytes;
	}
	
	private static byte[] cifrarAsimetrico(byte[] msg, KeyPair k) 
			throws NoSuchAlgorithmException, NoSuchPaddingException, 
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, k.getPrivate());
		byte[] bytes = cipher.doFinal(msg);
		return bytes;
    }
	
	private static byte[] descifrarAsimetrico(byte[] msg, KeyPair k) 
			throws NoSuchAlgorithmException, NoSuchPaddingException, 
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, k.getPublic());
		byte[] bytes = cipher.doFinal(msg);
		return bytes;
    }
	
	
	/**
	 * Obtener hash
	 * 
	 * @param n
	 */
	private static void hash(int n) {
		long[] times = new long[n];
		// generar un mensaje de num caracteres
		String message = generateMessage(5000);

		for (int i = 0; i < n; i++) {
			try {
				long t0 = System.nanoTime();
				// obtener messageDigest
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				byte[] bytes = message.getBytes();
				md5.update(bytes);
				bytes = md5.digest(); // crear hash

				long t1 = System.nanoTime();

				long tiempo = (long) ((t1 - t0) / 1000000);
				times[i] = tiempo;

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		// calcular la media de iteraciones
		long tiempo = 0;
		for (int i = 0; i < times.length; i++) {
			tiempo += times[i];
		}
		tiempo = (long) (tiempo / times.length);

		System.out.println("HASH) Tiempo: " + tiempo + " milisegundos");
	}

	/**
	 * Genera @param n claves de tipo: 
	 * - Clave publica (t1) 
	 * - Clave publica (t2)
	 * - Clave publica (t3)
	 * 
	 * - Clave privada (t1) 
	 * - Clave privada (t2) 
	 * - Clave privada (t3) 
	 * Genera los pares de claves publica/privada
	 */
	private static KeyPair[] generarClaves(int n) {
		KeyPair[] kps = new KeyPair[3];
		for (int i = 0; i < kps.length; i++) {
			System.out.print("Tam " + (i + 1) + " --> ");
			kps[i] = generarClave(n, i + 1);
		}
		return kps;
	}

	/**
	 * genera un par de clave. Repite las veces necesarias para calcular la
	 * media del tiempo de ejecucion pero solo guarda el primer par creado para
	 * devolverlo
	 * 
	 * @param n
	 * @param tam
	 * @return
	 */
	private static KeyPair generarClave(int n, int tam) {
		long[] times = new long[n];
		KeyPair k = null;

		for (int i = 0; i < n; i++) {
			try {
				long t0 = System.nanoTime();
				// obtener generador de claves
				KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
				SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
				kpg.initialize(1024 * tam, sr);
				KeyPair kp = kpg.generateKeyPair(); // obtiene par de claves

				if (i == 0) {
					k = kp;
				}

				long t1 = System.nanoTime();

				long tiempo = (long) ((t1 - t0) / 1000000.0);
				times[i] = tiempo;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		long tiempo = 0;
		for (int i = 0; i < times.length; i++) {
			tiempo += times[i];
		}
		tiempo = (long) (tiempo / times.length);
		System.out.println("KEY GENERATION) Tiempo: " + tiempo + " milisegundos");

		return k;
	}

	/**
	 * almacena los pares de claves
	 * 
	 * @param kps
	 */
	private static void almacenar(KeyPair[] kps) {
		// idea: forma de utilizar un "almacen" para guardar las claves
		// http://stackoverflow.com/questions/9890313/how-to-use-keystore-in-java-to-store-private-key
		try {
			long t0 = System.nanoTime();
			KeyFactory fact = KeyFactory.getInstance("RSA");
			for (int i = 0; i < kps.length; i++) {
				RSAPublicKeySpec pub = fact.getKeySpec(kps[i].getPublic(), RSAPublicKeySpec.class);
				saveToFile(publicasFile + i, pub.getModulus(), pub.getPublicExponent());
				RSAPrivateKeySpec priv = fact.getKeySpec(kps[i].getPrivate(), RSAPrivateKeySpec.class);
				saveToFile(privadasFile + i, priv.getPrivateExponent(), priv.getPrivateExponent());
			}
			long t1 = System.nanoTime();
			long tiempo = (long) ((t1 - t0) / 1000000.0);
			System.out.println("ALMACENAMIENTO)(3 pares de claves) Tiempo: " + tiempo + " milisegundos");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void saveToFile(String fileName, BigInteger mod, BigInteger exp) throws Exception {
		ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
		try {
			oout.writeObject(mod);
			oout.writeObject(exp);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			oout.close();
		}
	}

	private static PublicKey readPublicKey(int n) throws Exception {
		long t0 = System.nanoTime();
		InputStream in = new FileInputStream(publicasFile + n);
		ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
		try {
			BigInteger m = (BigInteger) oin.readObject();
			BigInteger e = (BigInteger) oin.readObject();
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			PublicKey pubKey = fact.generatePublic(keySpec);
			return pubKey;
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			oin.close();
			long t1 = System.nanoTime();
			long tiempo = (long) ((t1 - t0) / 1000000.0);
			System.out.println("LECTURA)(clave publica) Tiempo: " + tiempo + " milisegundos");
		}
	}

	private static PrivateKey readPrivateKey(int n) throws Exception {
		long t0 = System.nanoTime();
		InputStream in = new FileInputStream(privadasFile + n);
		ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
		try {
			BigInteger m = (BigInteger) oin.readObject();
			BigInteger e = (BigInteger) oin.readObject();
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			PrivateKey privKey = fact.generatePrivate(keySpec);
			return privKey;
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			oin.close();
			long t1 = System.nanoTime();
			long tiempo = (long) ((t1 - t0) / 1000000.0);
			System.out.println("LECTURA)(clave privada) Tiempo: " + tiempo + " milisegundos");
		}
	}

	/**
	 * Genera un mensaje aleatorio de @param length caracteres
	 * 
	 * @return
	 */
	private static String generateMessage(int length) {
		String s = "";
		Random r = new Random();
		for (int i = 0; i < length; i++) {
			char c = (char) r.nextInt(128); // ascii
			s = s + c;
		}
		return s;
	}
}
