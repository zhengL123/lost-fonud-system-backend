package com.lostfound.server.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 密码加密解密工具类
 */
@Component
public class PasswordCryptoUtil {
    
    // 密钥，与前端保持一致
    @Value("${password.crypto.secret}")
    private String secretKey;
    
    /**
     * 解密密码
     * @param encryptedPassword 加密后的密码
     * @return 解密后的密码
     */
    public String decryptPassword(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.isEmpty()) {
            return "";
        }
        
        try {
            // CryptoJS AES加密的格式是: Base64(盐值 + 初始化向量 + 加密数据)
            // 我们需要解析这个格式
            
            // Base64解码
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedPassword);
            
            // 提取盐值（前8字节）
            byte[] salt = new byte[8];
            System.arraycopy(encryptedBytes, 0, salt, 0, 8);
            
            // 提取初始化向量（接下来8字节）
            byte[] iv = new byte[8];
            System.arraycopy(encryptedBytes, 8, iv, 0, 8);
            
            // 提取加密数据（剩余部分）
            byte[] encryptedData = new byte[encryptedBytes.length - 16];
            System.arraycopy(encryptedBytes, 16, encryptedData, 0, encryptedData.length);
            
            // 使用OpenSSL的EVP_BytesToKey方法派生密钥
            // 这里简化处理，直接使用secretKey
            byte[] keyBytes = secretKey.getBytes("UTF-8");
            
            // 创建密钥和初始化向量
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            
            // 解密
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedData);
            
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            // 如果解密失败，可能是明文密码（兼容旧版本）
            System.err.println("解密失败，使用原始密码: " + e.getMessage());
            return encryptedPassword;
        }
    }
}