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
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.fuiou.mer.model.TIvrOrderInf;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mgr.http.httpClient.HttpClientHelper;

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
			String out1 = "7CulMeD9zpztHmWXdcYgIGHllQuvM/vgKjv9Im6D4u27CpkAjRzZgCKA5XujuYrU"+
							"qKA4CTlwdG3s3OtHpGeC3QdMbJGbSh5rB0xskZtKHmu0AhPLRiAy9QLAHk3SV+5j"+
							"dTjQmOy5VyOoz/C0jeO/duyhs+mYxXfvZxEFTbO8bRIuvNlBUwqDazoroh2lXI2N"+
							"FUr7vH8bH/UyqYeC3XZMQQdMbJGbSh5r6Ts2YQuVy8JzoBenVi5y6HYft+GYWID4"+
							"VXg1I8S6ZdR/QpPWpwGZFaHLAX/fF4izE//CRMhYgAVriAzEm6Y+abPmQPrUgU9c"+
							"9UKh9Q1vWKvdc9fckxmzDK2ksSjKexJQwUTzHQVTAsvulzQeGNCn2cJ7+my/fJGn&NG56RdX/LxslBSfal5+W/9dQI21VuGDr4p8GV4pdRBPDMUrBji1ZPCD8F4eq8qJr"+
							"nPliAlb/HdjdmmVGgrnnj+ELRLW8xqnPfsRnsqXdDEdxMDMPk2RF5lSnnkLmCovB"+
							"LMRZfSULhLirIYjYVobk23/VvaJnbF3GLkpmpxpoaub+9vyRpx69YjrKKOYSRPri"+
							"qD1qHw+8T1BwtZBVrBjbJ2qdBdflqAC0Ogq31+bNETfp6kCXX6yHl+howvA2JzNN"+
							"/w1j82HWq6kp6FViKadBfhlqoUp9Kp2Ipu0oxUidwiUAjkHYSuAPpW1GgOQfIwJB"+
							"/F1VkR7t/kyl5+5Djd/aOg==";
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
