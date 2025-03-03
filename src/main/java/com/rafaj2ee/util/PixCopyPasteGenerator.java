package com.rafaj2ee.util;

import java.nio.charset.StandardCharsets;

public class PixCopyPasteGenerator {

    public static String generatePixCopyPaste(String chave, String amount, 
                                             String merchantCity, String merchantName) {
        if(chave.startsWith("55")) {
        	chave = "+"+chave;
        }
        StringBuilder payload = new StringBuilder();

        // ID 00 - Payload Format Indicator (Versão 01)
        payload.append("000201");

        // ID 26 - Merchant Account Information (GUI + Chave PIX)
        String gui = "0014br.gov.bcb.pix";
        String campoChave = "01" + formatarTamanhoValor(chave.length()) + chave;
        String merchantAccountInfo = gui + campoChave;
        payload.append("26" + formatarTamanhoValor(merchantAccountInfo.length()) + merchantAccountInfo);

        // ID 52 - Merchant Category Code (0000 = Não especificado)
        payload.append("52040000");

        // ID 53 - Moeda (986 = BRL)
        payload.append("5303986");

        // ID 54 - Valor da transação (opcional)
        if (amount != null && !amount.isEmpty()) {
            payload.append("54" + formatarTamanhoValor(amount.length()) + amount);
        }

        // ID 58 - Código do país (BR)
        payload.append("5802BR");

        // ID 59 - Nome do recebedor
        payload.append("59" + formatarTamanhoValor(merchantName.length()) + merchantName);

        // ID 60 - Cidade do recebedor
        payload.append("60" + formatarTamanhoValor(merchantCity.length()) + merchantCity);

        // ID 62 - Dados adicionais (TXID genérico)
        String txid = "***";
        String additionalData = "05" + formatarTamanhoValor(txid.length()) + txid;
        payload.append("62" + formatarTamanhoValor(additionalData.length()) + additionalData);

        // Calcula CRC16
        String dadosParaCRC = payload.toString() + "6304";
        String crc = calcularCRC(dadosParaCRC);
        payload.append("6304" + crc);

        return payload.toString();
    }

    private static String formatarTamanhoValor(int length) {
        return String.format("%02d", length);
    }

    private static String calcularCRC(String dados) {
        int polynomial = 0x1021;
        int crc = 0xFFFF;

        byte[] bytes = dados.getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            crc ^= (b & 0xFF) << 8;
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
}