package pentomino.jcmagent;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

public class SftpUtils {

	private Session session = null;

	private String _HostName;
	private String _port;
	private String _username;
	private String _password;
	private String _path;    


	public SftpUtils(String HostName,String port,String username,String password,String path) {
		_HostName = HostName;
		_port = port;
		_username = username;
		_password = password;
		_path = path;

		System.out.println(_HostName + " " + _port + " " + _username + " " +  _password + " " + _path);

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SftpUtils sftpClient = new SftpUtils("11.50.0.7","22","filemanager",":s0pJG-Ex1eX","retrieve/in");	


		try {
			sftpClient.connect();
			System.out.println("Conectado");
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		try {
			System.out.println("Uploading...");


			sftpClient.upload("./downloads/archivo1 - copia (3).txt", "retrieve/in/5ed967a2602dcf8f1fcf3942.filemanager");
			System.out.println("...Uploaded");
		} catch (JSchException | SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Desconectando...");
		sftpClient.disconnect();
		System.out.println("...Desconectado");
	}




	public void connect() throws JSchException {

		JSch jsch = new JSch();

		session = jsch.getSession(_username, _HostName, Integer.parseInt(_port));

		session.setPassword(_password);

		session.setConfig("StrictHostKeyChecking", "no");

		session.connect();

	}



	public void upload(String source, String destination) throws JSchException, SftpException {

		System.out.println("UPLOAD [" + source + "] [" + destination + "]");

		Channel channel = session.openChannel("sftp");

		channel.connect();

		ChannelSftp sftpChannel = (ChannelSftp) channel;

		sftpChannel.put(source, destination, new progressMonitor());

		sftpChannel.exit();

	}

	public void rename(String source, String destination) throws JSchException, SftpException {

		System.out.println("RENAME [" + source + "] [" + destination + "]");

		Channel channel = session.openChannel("sftp");

		channel.connect();

		ChannelSftp sftpChannel = (ChannelSftp) channel;

		sftpChannel.rename(source, destination);

		sftpChannel.exit();

	}

	public void download(String source, String destination) throws JSchException, SftpException {		  

		System.out.println("Source [" + source + "] destination [" + destination + "]");

		Channel channel = session.openChannel("sftp");

		channel.connect();

		ChannelSftp sftpChannel = (ChannelSftp) channel;

		sftpChannel.get(source, destination, new progressMonitor());

		sftpChannel.exit();

	}


	public void disconnect() {
		if (session != null) {
			session.disconnect();
		}
	}




	// Change the class name if you want
	private class progressMonitor implements SftpProgressMonitor{
		private long max                = 0;
		private long count              = 0;
		private long percent            = 0;
		//private CallbackContext callbacks = null;

		// If you need send something to the constructor, change this method
		public progressMonitor() {}

		public void init(int op, java.lang.String src, java.lang.String dest, long max) {
			this.max = max;
			System.out.println("starting");
			System.out.println(src); // Origin destination
			System.out.println(dest); // Destination path
			System.out.println(max); // Total filesize
		}

		public boolean count(long bytes){
			this.count += bytes;
			long percentNow = this.count*100/max;
			if(percentNow>this.percent){
				this.percent = percentNow;
				System.out.println("total size[" + max + "] progress [" + this.percent + "] bytes [" + this.count + "]"); // Progress 0,0
			}

			return(true);
		}

		public void end(){
			System.out.println("finished");// The process is over
			System.out.println("total size[" + max + "] progress [" + this.percent + "] bytes [" + this.count + "]");
		}
	}

}
