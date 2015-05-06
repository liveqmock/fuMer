package com.fuiou.mgr.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class VPCDecodeUtil {

	private static RSAPrivateKey privateKey;
	private static RSAPublicKey publicKey;
	private static BouncyCastleProvider provider = new BouncyCastleProvider();

	private static final String ALGORITHM_3DES = "DESede";
	private static final String DES_PADDING = "DESede/ECB/PKCS5Padding"; // "DESede/ECB/NoPadding";
	private static final String ALGORITHM_RSA = "RSA";
	
	public static void init(String pubKeyPath,String priKeyPath){
		try {
			InputStream in_pub = new FileInputStream(new File(pubKeyPath));
			InputStream in_pri = new FileInputStream(new File(priKeyPath));
			loadPublicKey(in_pub);
			loadPrivateKey(in_pri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Auto-generated main method to display this JDialog
	 */
	public static void loadPrivateKey(InputStream in) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			loadPrivateKey(sb.toString());
		} catch (IOException e) {
			throw new Exception("私钥数据读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥输入流为空");
		}
	}

	public static void loadPrivateKey(String privateKeyStr) throws Exception {
		try {
			BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
			RSAPrivateKeyStructure asn1PrivKey = new RSAPrivateKeyStructure((ASN1Sequence) ASN1Sequence.fromByteArray(buffer));
			RSAPrivateKeySpec rsaPrivKeySpec = new RSAPrivateKeySpec(asn1PrivKey.getModulus(), asn1PrivKey.getPrivateExponent());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			privateKey = (RSAPrivateKey) keyFactory.generatePrivate(rsaPrivKeySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("私钥非法");
		} catch (IOException e) {
			throw new Exception("私钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}

	public static void loadPublicKey(InputStream in) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			loadPublicKey(sb.toString());
		} catch (IOException e) {
			throw new Exception("公钥数据流读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥输入流为空");
		}
	}

	public static void loadPublicKey(String publicKeyStr) throws Exception {
		try {
			BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);

			// *
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (IOException e) {
			throw new Exception("公钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}

	public static byte[] encryptedData(RSAPublicKey publicKey, byte[] plainData)
			throws Exception {
		try {
			Cipher cipher1 = Cipher.getInstance("RSA/ECB/PKCS1Padding",provider);
			cipher1.init(Cipher.ENCRYPT_MODE, publicKey);
			int blockSize = cipher1.getBlockSize();
			int outputSize = cipher1.getOutputSize(plainData.length);
			int leavedSize = plainData.length % blockSize;
			int blocksSize = leavedSize != 0 ? plainData.length / blockSize + 1
					: plainData.length / blockSize;
			byte[] raw = new byte[outputSize * blocksSize];
			int i = 0;
			while (plainData.length - i * blockSize > 0) {
				if (plainData.length - i * blockSize > blockSize) {
					cipher1.doFinal(plainData, i * blockSize, blockSize, raw, i
							* outputSize);
				} else {
					cipher1.doFinal(plainData, i * blockSize, plainData.length
							- i * blockSize, raw, i * outputSize);
				}
				i++;
			}
			return raw;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public static byte[] DecryptedData(String dataString, String encoding,
			RSAPrivateKey key) {
		try {
			byte[] pinBlock = base64Decode(dataString);
			// 本土的
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", provider);
			cipher.init(Cipher.DECRYPT_MODE, key);
			int blockSize = cipher.getBlockSize();
			int outputSize = cipher.getOutputSize(pinBlock.length);
			int leavedSize = pinBlock.length % blockSize;
			int blocksSize = leavedSize != 0 ? pinBlock.length / blockSize + 1 : pinBlock.length / blockSize;
			byte[] pinData = new byte[outputSize * blocksSize];
			int i = 0;
			while (pinBlock.length - i * blockSize > 0) {
				if (pinBlock.length - i * blockSize > blockSize) {
					// outDataLen += cipher.doFinal(pinBlock, i * blockSize,
					// blockSize, pinData, i * outputSize);
					cipher.doFinal(pinBlock, i * blockSize, blockSize, pinData,
							i * outputSize);
				} else {
					// outDataLen += cipher.doFinal(pinBlock, i * blockSize,
					// pinBlock.length - i * blockSize,
					// pinData, i * outputSize);

					cipher.doFinal(pinBlock, i * blockSize, pinBlock.length - i
							* blockSize, pinData, i * outputSize);
				}
				i++;
			}

			return pinData;// cipher.doFinal(pinBlock)
		} catch (Exception e) {
			// DjLogUtil.writeErrorLog("解密失败", e);
		}
		return null;
	}

	public static byte[] desEncrypt(byte[] data, byte[] strKey) {

		try {
			SecretKey key = new SecretKeySpec(strKey, ALGORITHM_3DES); // DjConvert.str2Bcd(strKey)
			Cipher encryptCipher = Cipher.getInstance(DES_PADDING);
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);
			return encryptCipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] base64Decode(String inputByte) throws IOException {
		BASE64Decoder dec = new BASE64Decoder();// Base64.decodeBase64(inputByte);
		return dec.decodeBuffer(inputByte);
	}

	public static String base64EncodeString(byte[] inputByte)
			throws IOException {
		// return Base64.encodeBase64String(inputByte);
		BASE64Encoder enc = new BASE64Encoder();
		return enc.encode(inputByte);
	}

	/**
	 * 3des解密.
	 * 
	 * @param datas
	 *            待计算的数据
	 * @return 计算结果
	 */
	public static byte[] desDecrypt(byte[] data, byte[] strKey) {

		try {
			SecretKey key = new SecretKeySpec(strKey, ALGORITHM_3DES); // (DjConvert.str2Bcd(strKey),
																		// ALGORITHM_3DES);

			Cipher encryptCipher = Cipher.getInstance(DES_PADDING);
			encryptCipher.init(Cipher.DECRYPT_MODE, key);

			return encryptCipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String decrypt(String strMsg, RSAPrivateKey priKey) {

		try {
			String[] src = strMsg.split("&");
			byte[] keyTmp = DecryptedData(src[1].trim(), "UTF-8", priKey);
			byte[] md5key = new byte[24];
			System.arraycopy(keyTmp, 0, md5key, 0, 24);

			String result = new String(desDecrypt(base64Decode(src[0].trim()),
					md5key));

			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] rsaEncrypt(byte[] data, RSAPublicKey priKey) {
		if (priKey != null) {
			try {
				Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
				cipher.init(Cipher.ENCRYPT_MODE, priKey);
				return cipher.doFinal(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static String encrypt(String strMsg, RSAPublicKey pubKey) {
		try {
			byte[] keyTmp = new byte[24]; // 这里最好随机生成24个字节长度秘钥
			for (int i = 0; i < 24; i++)
				keyTmp[i] = (byte) ('a' + i);

			String result = base64EncodeString(desEncrypt(strMsg.getBytes("UTF-8"), keyTmp)) + "&" + base64EncodeString(rsaEncrypt(keyTmp, pubKey));
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String doCrypt(String flag,String src){
		if("C".equals(flag)){
			return encrypt(src, publicKey);
		}else{
			return decrypt(src, privateKey);
		}
	}

	public static void main(String[] args) {
		try {
			InputStream in_pub = new FileInputStream(new File("D:\\workspace\\fuMer_M\\resources\\pub.key"));
			InputStream in_pri = new FileInputStream(new File("D:\\workspace\\fuMer_M\\resources\\pri.key"));

			loadPublicKey(in_pub);
			loadPrivateKey(in_pri);

//			String src = "CstmInfo={01|3623231989|123||123456||}&EnCdMd=UTF-8&MchntCd=602020000001123&OdrDtTm=20141103144700&OdrId=123456789&OdrPhoneNum=1821254557&PosRes=123456&PriAcct=6226987456321231&TranAmtPos=000000000001&TranCurrCd=156&TranSubTp=00&TransSsn=12345678&TranTp=82&Version=1.0.0&VPCId=00168400cb13&MsgSign=kESDbdFeByc2TQVGDdbi0WAgRDMJ0EgakUm1fIG+xxbKi7t4rjevf3C6VsgPdjQ7U9emCuA3F57+0RhAF+IoeWBEdmb7VdOWPY9/fpPNRU663Raqzm9Vt1L63uTR+hdG1DTeZq/siJteQ+sOhL/lfmxOVsV9WIrL86GLsNaE/Vw=";
			String out1 = "oZ+KH4ZlFx0eLMNIpLrG0k+OqQSsVnh8AOFh7mFS0veWRcrSVRz7xOGqwMIaK+Lf"+
							"CExwNHHorThFLcPRajdL9fMmpqZ7ulMiWqoDyX4p6uYorTb43bEr7NzR0zVuiA6E"+
							"rLkD/rFYguIyIbPmgKC0p8IIhoBPFd5rBx/F3xzdT/f48ajaiK70o2KQpg5k6OnU"+
							"Tc0DXkfjs1Ug6QMevIz8Px/gxteV6qeQnI8/6pDJ/RdpuM8eOYjM29GFhEFkZxIG"+
							"HyWqjSnMov/OlTDTrYMhqTjmGoJOOenu2vmxxiHuvSrfWc3kktk+HI01BcSVtubZ"+
							"m7ceTGNdDhMdKMZUYpWNA1kA7x/nOhpc4pqNREBFk7+TSp7TYlfdbrXZLMbQk00U"+
							"pneKODPif/puVl6yFJWS4ZJebH2F3mRg0MikbiRAQURabgfoOgokCZCe4aHv3SZa"+
							"EPWpbw0S9RAVjQgD+1y2yG1U2P0XGB8DqHYqZUVagIbrr0wSjNFF2J4uRd/ZWHoo"+
							"ONVvgPwcUk6+3BW7sK2oRvhPypB3gBr7EPxv0gZvUNtGyefNjAIvHKth84xgtwd0"+
							"pVNUi8GTkVgnBfmjHq/LSf++ljShlqF6XINC+s7OAiIurgFjIqYbZ9Y0/seaVNcv"+
							"//7nTRg3sl7AzCG/lKKMYK/R5XqmjOWBWCruOzaBhMY9SgJfoGYhhvo9X3m73CY6"+
							"1HPOaILQnrRh0nUd19QGlaADyG1O0VBycGKkOWPA3KHRkUNWJMEfCTH2cfRMAY9P"+
							"NOzUgh8jNEKuDUt1y+qFa6BSxxj3W2ivY06Hkj0rWPRU/YNmZ75e8AvvLWhQP7UD"+
							"9dLmu2XQOGYqdAxJyLviJvQ5/SsviKxERMNePZX+SWPpzM8hWsfRIf/lw5xyFUi3"+
							"Zhr8CaDO8c02iQwmiLigS6BwcxtoR/QgiBE5lt6whwh+SDxcG0HTE7V++ipukSvD"+
							"Z6rlisl4A462q7pL1mbUfdDz5epb7AIJ6oFBtk1BkNU0ZsItixxKjGbYJTuus7cK&cPFtifdLE3wx8XFhUyCjN+m27acPgOgPjREToLT3r3iWEhWiX3NLyYSEzcowqFEZ"+
							"ezrZl6aq6rjhk66IDKNxPih960XYiKU5ggoVn8hNjfaG3YB4dV4URtqm+pm0TU1b"+
							"q3loPb3WXs4OMoJFjKdq+mMMwbIaeZrFGljzDa7cFd7fYv43oDvmYgSuYiKjzgga"+
							"RIzvwQThfHitAbpBbk9UKH7bcEJUhAu/uy0z2B3RS0gGCtqVWRv/xPEwJ4HnOdF8"+
							"eB9vba5moZBNssYdRBRTFlwhKhCqQiTacth67EMJDIIUWEOavHopT/qCqA7fgZg1"+
							"hBttso+oS9WA/53pG+h6kw==";
//			String out1 = encrypt(src, publicKey);
			String dest = decrypt(out1, privateKey);
//			System.out.println("Encode Result:" + out1);
			System.out.println("Decode Result:" + dest);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
