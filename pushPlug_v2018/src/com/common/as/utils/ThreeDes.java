package com.common.as.utils;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;
/**
 * @author Slash
 * @version 1.0 2014-03-27
 */
public class ThreeDes {
	private static final String Algorithm = "DESede"; 
	static String HEXSTR_FORMAT="0123456789abcdef";
    /**
     * keybyte 24字节
     */
	public static byte[] encryptMode(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
			// 加密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 * @param keybyte    密匙   24个字�?
	 * @param src        密文
	 * @return
	 */
	public static byte[] decryptMode(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
			// 解密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 16进制的字符串转byte[]
	 * @param hexStr
	 * @return
	 */
	public static byte[] hexStr2Byte(String hexStr) {  
        char[] hexs = hexStr.toLowerCase().toCharArray();  
        byte[] bytes = new byte[hexStr.length() / 2];  
        int n;  
        for (int i = 0; i < bytes.length; i++) {  
            n = HEXSTR_FORMAT.indexOf(hexs[2 * i]) * 16;  
            n += HEXSTR_FORMAT.indexOf(hexs[2 * i + 1]);  
            bytes[i] = (byte) (n & 0xff); 
        } 
        return bytes;  
    } 
	/**
	 * 字节数据�?6进制字符�?
	 * @param b
	 * @return
	 */
	public static String byte2HexStr(byte[] b){
		String hs="";
        String stmp="";
        for (int n=0;n<b.length;n++) {
            stmp=(Integer.toHexString(b[n] & 0XFF));
            if (stmp.length()==1) hs=hs+"0"+stmp;
            else hs=hs+stmp;
        }
        return hs.toUpperCase();

	}
	public static void main(String[] args) {
		try {
		// 添加新安全算�?如果用JCE就要把它添加进去 
		//Security.addProvider(new com.sun.crypto.provider.SunJCE());
		final byte[] keyBytes ="q11tt0xgpw0v3f5zcutvumb9".getBytes();//密钥
///imsi=460006536066065&imei=355856041724762&startType=1
		String szSrc = "skymobicmp101226";
		szSrc="Q1O@9q-#\\)(^aky@!#G&(^*27-]k=";
		szSrc="startType = 0";
		
		System.out.println("加密前的字符�?" + szSrc);

		byte[] encoded = encryptMode(keyBytes, szSrc.getBytes());
		//System.out.println(encoded.length);
		String out=byte2HexStr(encoded);//由客户端传�?的参数�?
		System.out.println("加密后的16进制字符�?"+out);
		//以上客户�?�?
	  
		byte[] srcBytes = decryptMode(keyBytes,hexStr2Byte(out));
		System.out.println("解密后的字符�?" + (new String(srcBytes)));
		
		/*srcBytes = decryptMode(keyBytes,hexStr2Byte("7693056B72834EB9630332B33927B1439036A989276F05772376930BC5F2115F"));
		System.out.println("解密后的字符�?" + (new String(srcBytes)));*/
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}