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
 */
package practica5;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Random;

import javax.crypto.SecretKey;

public class Practica5 {

	/*
	 * Se experimenta con las funciones de seguridad de Java Cryptography
	 * Extension (JCE) para:
	 * 	- Obtener un hash
	 * 	- Generar claves de cifrado (publica y privada) y almacenarlas
	 * 	- Cifrar y descifrar con clave publica y privada (en el caso de la 
	 * 	publica, tamanio maximo del mensaje que se puede cifrar)
	 * 	- Firma digital
	 * 
	 * Se medira el tiempo de generacion de claves para tres tamanios 
	 * diferentes de claves - la que viene en el programa, el doble y
	 * el triple).
	 * 
	 * Se calculara la media de los tiempos (se ejecutara varias veces cada 
	 * algoritmo).
	 * 
	 * Se mediran tambien los tiempos de firmado, hash y firma digital, y el
	 * tiempo de cifrado y descifrado para docs de texto con criptografia
	 * de clave secreta y para una lista de cien mensajes aleatorios del 
	 * tamanio maximo determinado anteriormente y comparar los tiempos para
	 * la criptografia de clave publica y secreta.
	 */

	/**
	 * @param args: entero para determinar el numero de veces que se ejecuta cada
	 * funcion, el path de la carpeta donde se encuentran los docs que cifrar y 
	 * descifrar.
	 */
	public static void main(String[] args) {
		int n = -1;
		String path = null;
		
		/* parametros */
		for (int i = 0; i < args.length; i++) {
			String s = args[i];
			if(s.equals("-iters")){
				i++;
				try{
					n = Integer.parseInt(args[i]);
				}
				catch(Exception e){
					System.err.println("Usage: java Practica5 -iters <num> -path <path>");
					System.exit(1);
				}
				
			}
			else if(s.equals("-path")){
				i++;
				try{
					path = args[i];
				}
				catch(Exception e){
					System.err.println("Usage: java Practica5 -iters <numDocs> -path <path>");
					System.exit(1);
				}
			}
		}
		
		if(n < 0 || path == null){
			System.err.println("Usage: java Practica5 -iters <numDocs> -path <path>");
			System.exit(1);
		}
		
		hash(n);	// Obtener hash
		KeyPair[] kps = generarClaves(n);	// Generacion de claves
		almacenar(kps);	// Almacenamiento de claves
	}
	
	/**
	 * 
	 * @param n
	 */
	private static void hash(int n){
		long[] times = new long[n];
		
		String message = generateMessage(5000);
		
		for (int i = 0; i < n; i++) {
			try {
				long t0 = System.nanoTime();
				
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				byte[] bytes = message.getBytes();
				md5.update(bytes);
				bytes = md5.digest();
				
				long t1 = System.nanoTime();
				
				long tiempo = (long)((t1 - t0) / 1000);
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
	 */
	private static KeyPair[] generarClaves(int n){
		KeyPair[] kps = new KeyPair[3];
		for (int i = 0; i < kps.length; i++) {
			System.out.print("Tam " + (i+1) + " --> ");
			kps[i] = generarClave(n, i+1);
		}
		return kps;
	}
	
	private static KeyPair generarClave(int n, int tam){
		long[] times = new long[n];
		KeyPair k = null;
		
		for (int i = 0; i < n; i++) {
			try{
				long t0 = System.nanoTime();
				
				KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
				SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
				kpg.initialize(1024 * tam, sr);
				KeyPair kp = kpg.generateKeyPair();
				
				if(i == 0){
					k = kp;
				}
				
				long t1 = System.nanoTime();
				
				long tiempo = (long)((t1 - t0) / 1000.0);
				times[i] = tiempo;
			}
			catch(NoSuchAlgorithmException e){
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
	
	private static void almacenar(KeyPair[]kps){
		try {
			long t0 = System.nanoTime();
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(null, null);
			for (int i = 0; i < kps.length; i++) {
				KeyStore.SecretKeyEntry ske = new SecretKeyEntry((SecretKey) kps[i].getPublic());
				ks.setEntry("publica" + i + "", ske, null);
				
				ske = new SecretKeyEntry((SecretKey) kps[i].getPrivate());
				ks.setEntry("privada" + i + "", ske, null);
			}
			long t1 = System.nanoTime();
			long tiempo = (long) ((t1 - t0) / 1000.0);
			System.out.println("ALMACENAMIENTO) Tiempo: " + tiempo + " milisegundos");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Genera un mensaje aleatorio de @param length caracteres
	 * @return
	 */
	private static String generateMessage(int length){
		String s = "";
		Random r = new Random();
		for (int i = 0; i < length; i++) {
			char c = (char) r.nextInt(128);	// ascii
			s = s + c;
		}
		return s;
	}
}
