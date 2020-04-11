/*
 * by SydBernard
 */

public class kermit {

	
	private int crc16(byte byt, int fcs)
	{
		byte bit;

	    for(bit=0; bit<8; bit++)
	    {
	        fcs ^= (byt & 0x01);
	        fcs = ((fcs & 0x01) != 0) ? (fcs >> 1) ^ 0x8408 : (fcs >> 1);
	        byt = (byte) (byt >> 1);
	    }
	    return fcs;
	}
	
	public int crc_kermit(byte[] data, byte number_of_bytes)
	{
		int prev_CRC = 0;
		
		for(byte j = 0; j<number_of_bytes; j++){
			prev_CRC =  this.crc16(data[j], prev_CRC);
		}
	return prev_CRC;
	}
	
	/* Only for ID003 protocol  */
	public boolean compareCRC(byte[] array){     
		byte numbyte = array[1];
		int chkReceived =  ((array[numbyte -1] << 8 ) & 0xFF00 ) |  ((array[numbyte -2]) & 0x00FF ) ;
		int chkCalculated = this.crc_kermit(array, (byte)(numbyte -2 ) );
	
		if(chkCalculated  == chkReceived){
			return true;
		}else {
			return false;
		}
	}
	
	
}
