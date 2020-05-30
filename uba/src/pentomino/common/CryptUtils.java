package pentomino.common;

import java.io.FileOutputStream;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import com.google.gson.Gson;
import pentomino.jcmagent.AgentMessage;

public class CryptUtils {

	
	private static Gson gson = new Gson();
	
	private static final String ALGORITHM = "AES";
	private static final String UNICODE_FORMAT = "UTF-8";
	
	private static final byte[] keyValue =  new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };
	
	private static Key key;
	
	private static Cipher chipher;
	
	public static void main(String[] args) {
		
	}	
	
	public static void SaveEntry(AgentMessage payload) {
		
		byte[] encryptedData = encryptString(gson.toJson(payload));
		String archi = "" + payload.Timestamp + "." + payload.Id;
		System.out.println("EL ARCHIVO ES " + archi);
		
		//GUARDO EL ARCHIVO
		try {
			FileOutputStream fos = new FileOutputStream("./queue/" + archi);
			fos.write(encryptedData);
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static byte[] encryptString(String dataToEncrypt) {
		try {
			
			if(key == null)
				generateKey();
			if(chipher == null) {
				chipher = Cipher.getInstance("AES");
			}
			
			byte[] text = dataToEncrypt.getBytes(UNICODE_FORMAT);
			chipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] textEncrypted = chipher.doFinal(text);
			
			
			
			return textEncrypted;
		}
		catch(Exception e) {
			System.out.println("ecryptString exception");
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static String decryptString(byte[] dataToDecrypt) {
		try {
			
			if(key == null)
				generateKey();
			if(chipher == null) {
				chipher = Cipher.getInstance("AES");
			}
			
			chipher.init(Cipher.DECRYPT_MODE, key);
			byte[] textDecrypted = chipher.doFinal(dataToDecrypt);
			String result = new String(textDecrypted);
			
			return result;
		}
		catch(Exception e) {
			System.out.println(e);
			return null;
		}
	}
		

		
		private static void generateKey() {
		    try {
		    	System.out.println("Generetaing key");
		    	
		    	 key = new SecretKeySpec(keyValue, ALGORITHM);
		    	
		    }
		    catch(Exception e) {
		    	return;
		    }
		}

}
