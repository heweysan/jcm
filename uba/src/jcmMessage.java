
public class jcmMessage {

	private byte SYNC;
	private byte LNG;
	private byte CMD;
	private byte CRCL;
	private byte CRCH;
	
	
	public byte getSYNC() {
		return SYNC;
	}
	public void setSYNC(byte sYNC) {
		SYNC = sYNC;
	}
	public byte getLNG() {
		return LNG;
	}
	public void setLNG(byte lNG) {
		LNG = lNG;
	}
	public byte getCMD() {
		return CMD;
	}
	public void setCMD(byte cMD) {
		CMD = cMD;
	}
	public byte getCRCL() {
		return CRCL;
	}
	public void setCRCL(byte cRCL) {
		CRCL = cRCL;
	}
	public byte getCRCH() {
		return CRCH;
	}
	public void setCRCH(byte cRCH) {
		CRCH = cRCH;
	}
	
	
}
