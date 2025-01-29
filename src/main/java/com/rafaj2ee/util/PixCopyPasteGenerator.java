package com.rafaj2ee.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.SpringApplication;

public class PixCopyPasteGenerator {



	public static void main(String[] args) {
	    InputStream is = PixCopyPasteGenerator.class.getResourceAsStream("/keystore.p12");
	    if (is == null) {
	        System.out.println("Keystore not found!");
	    } else {
	        System.out.println("Keystore found!");
	    }
	    SpringApplication.run(PixCopyPasteGenerator.class, args);
	}


    public static String generatePixCopyPaste(String pixKey, String amount, String merchantCity, String merchantName) {
        StringBuilder payload = new StringBuilder();

        // ID 00 - Payload Format Indicator
        payload.append("00").append("02").append("01");

        // ID 26 - Merchant Account Information
        String merchantAccountInfo = "0014br.gov.bcb.pix01".toUpperCase() + formataramount(pixKey);
        payload.append("26").append(formataramount(merchantAccountInfo.length())).append(merchantAccountInfo);

        // ID 52 - Merchant Category Code (default: 0000 - não especificado)
        payload.append("52040000");

        // ID 53 - Transaction Currency (default: 986 - BRL)
        payload.append("5303986");

        // ID 54 - Transaction Amount
        if (amount != null && !amount.isEmpty()) {
            payload.append("54").append(formataramount(amount.length())).append(amount);
        }

        // ID 58 - Country Code (BR)
        payload.append("5802BR");

        // ID 59 - Merchant Name (Exemplo: Nome genérico)
        payload.append("59").append(formataramount(merchantName.length())).append(merchantName);

        // ID 60 - Merchant City (Exemplo: BRASILIA)
        payload.append("60").append(formataramount(merchantCity.length())).append(merchantCity);

        // ID 62 - Additional Data Field Template
        String txid = "***"; // Identificador de transação genérico
        payload.append("62").append(formataramount(4 + txid.length())).append("05").append(formataramount(txid.length())).append(txid);

        // ID 63 - CRC (calculado no final)
        String crc = calcularCRC(payload.toString() + "6304");
        payload.append("6304").append(crc);

        return payload.toString();
    }

    private static String calcularCRC(String dados) {
        int polynomial = 0x1021;
        int crc = 0xFFFF;

        byte[] bytes = dados.getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            crc ^= (b << 8);
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ polynomial;
                } else {
                    crc <<= 1;
                }
            }
        }
        crc &= 0xFFFF;
        return String.format("%04X", crc);
    }

    private static String formataramount(int length) {
        return String.format("%02d", length);
    }

    private static String formataramount(String amount) {
        return String.format("%02d", amount.length()) + amount;
    }
}
