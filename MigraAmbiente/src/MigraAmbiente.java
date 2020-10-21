

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;


public class MigraAmbiente {

	
	private static String certDir = "/etc/openvpn";
	private static String fallback = "/home/pi/Desktop/Pentomino/CertFallback";


	static ReentrantLock lock = new ReentrantLock();

	private static Connection sqlLiteConn = null;
	
	
	public static void main(String[] args) throws IOException {
				
		
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
		
        String archivoCert = GetDirective("AtmId","");
        System.out.println("archivo cert : " + archivoCert);
        
        File fileZip = new File("/home/pi/Desktop/Pentomino/downloads/" + archivoCert + ".zip");
        boolean exists = fileZip.exists();
        
        if(!exists) {
        	System.out.println("No Existe el archivo de certificado");
        	return;        	
        }
        	
        
        
		
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
	
	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        
        return destFile;
    }
	
	public static String GetDirective(String param, String defValue) {


		String retVal = defValue;		
		String sql = "SELECT Value FROM Directives WHERE Key = ?  COLLATE NOCASE;";		

		lock.lock();
		try {

			connect();

			try (PreparedStatement pstmt  = sqlLiteConn.prepareStatement(sql)){		

				// set the value
				pstmt.setString(1,param);

				ResultSet rs  = pstmt.executeQuery();

				if(rs.isClosed())
					System.out.println("param [" + param + "] not found in DB setting defValue");
				else
					retVal = rs.getString("Value");

	
				if (sqlLiteConn != null) {
					sqlLiteConn.close();                
				}
			} catch (SQLException e) {
				if(e.getMessage() != null)
					System.out.println("Config.GetDirective SQLException [" +  e.getMessage() + "]");
				else{
					e.printStackTrace();
				}
			}
		}catch(Exception ge) {
			if(ge.getMessage() != null)
				System.out.println("Config.GetDirective GENERAL EXCEPTION [" +  ge.getMessage() + "]");
			else{
				ge.printStackTrace();
			}
		}
		finally{
			lock.unlock();
		}

		return retVal;
	}

	private static void connect() { 

		try { 

			if(sqlLiteConn != null && !sqlLiteConn.isClosed())
				return;

			String url = "jdbc:sqlite:/home/pi/Desktop/Pentomino/Pentomino.Config.db3";

			sqlLiteConn = DriverManager.getConnection(url);           

		} catch (SQLException e) {
			if(e.getMessage() != null)
				System.out.println("Config.connect SQLException [" +  e.getMessage() + "]");
			else{
				e.printStackTrace();
			}

		} finally {

		}
	}
	
}

