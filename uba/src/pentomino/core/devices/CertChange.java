package pentomino.core.devices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

public class CertChange {

	
	private static String certDir = "/etc/openvpn";
	private static String fallback = "/home/pi/Desktop/Pentomino/CertFallback";
	private static String certQA = "/home/pi/Desktop/Pentomino/CertQA";

	
	public static void paseProduccion() throws IOException {
				
		
		//Respalda el original a fallback
		File srcDir = new File(certDir);
		File fallbackDir = new File(fallback);

		try {
			FileUtils.cleanDirectory(fallbackDir);
		    FileUtils.copyDirectory(srcDir, fallbackDir);		    
		} catch (IOException e) {
		    e.printStackTrace();
		}		
		
        
        //Revisamos si el zip trae el nombre del cliente con otra exension:
        // File (or directory) with old name
        File fileNew = new File(certDir + "/client.ovpn");

        // File (or directory) with new name
        File fileOld = new File(certDir + "/client.conf");

        if (fileOld.exists()) {
        	//Si ya existe lo borramos        	 
		    if (fileOld.delete()) { 
		      System.out.println("Deleted the file: " + fileOld.getName());
		    } else {
		      System.out.println("Failed to delete the file.");
		    } 
        }
        
		//https://www.baeldung.com/java-compress-and-uncompress
		
		String fileZip = "/home/pi/Desktop/Pentomino/downloads/WM01AT0001.zip";
        File destDir = new File(certDir);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
        
        
          
        // Rename file (or directory)
        boolean success = fileNew.renameTo(fileOld);

        if (!success) {
           // Si no se pudo hacemos rollback...
        	
        }
        else {
        	System.out.println("Archivo renombrado " + fileNew.getName() + " a " + fileOld.getName());
        	System.out.println("reboot");					
			String command = "shutdown -r now";
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(command);
			} catch (IOException ex) {						
				ex.printStackTrace();
			}		
        }
        
        //Reinciiamos el cajerito ya con el nuevo cert
	}
	
	public static void paseQa() throws IOException {
		String source = "/etc/openvpn";
		File srcDir = new File(source);

		String fallback = "/home/pi/Desktop/Pentomino/CertFallback/";
		File fallbackDir = new File(fallback);

		try {
			FileUtils.cleanDirectory(fallbackDir);
		    FileUtils.copyDirectory(srcDir, fallbackDir);		    
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		
        
        //Revisamos si el zip trae el nombre del cliente con otra exension:
        // File (or directory) with old name
        File fileNew = new File("/home/pi/Desktop/Pentomino/Test/client.ovpn");

        // File (or directory) with new name
        File fileOld = new File("/home/pi/Desktop/Pentomino/Test/client.conf");

        if (fileOld.exists()) {
        	//Si ya existe lo borramos        	 
		    if (fileOld.delete()) { 
		      System.out.println("Deleted the file: " + fileOld.getName());
		    } else {
		      System.out.println("Failed to delete the file.");
		    } 
        }
        
		//https://www.baeldung.com/java-compress-and-uncompress
		
		String fileZip = "/home/pi/Desktop/Pentomino/downloads/WM01AT0001.zip";
        File destDir = new File("/home/pi/Desktop/Pentomino/Test");
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
        
        
          
        // Rename file (or directory)
        boolean success = fileNew.renameTo(fileOld);

        if (!success) {
           // Si no se pudo hacemos rollback...
        	
        }
        else {
        	System.out.println("Archivo renombrado " + fileNew.getName() + " a " + fileOld.getName());
        }
	}
	
	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        
        return destFile;
    }
	
	
}
