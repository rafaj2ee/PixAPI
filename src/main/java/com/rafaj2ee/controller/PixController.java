package com.rafaj2ee.controller;

import com.rafaj2ee.DTO.PixDTO;  // Atualizada a importação
import com.rafaj2ee.model.PurchaseTransaction;
import com.rafaj2ee.service.PixService;
import com.rafaj2ee.service.QRCodeService;
import com.rafaj2ee.util.PixCopyPasteGenerator;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;

@RestController
@RequestMapping("/api/v1/pix")
public class PixController {

    @Autowired
    private PixService pixService;

    @Autowired
    private QRCodeService qrCodeService;

    @PostMapping
    public PurchaseTransaction saveTransaction(@Valid @RequestBody PixDTO dto) throws Exception {
        return pixService.saveTransaction(dto);
    }

    @GetMapping("/generate")
    public String generatePixCode(@RequestParam @NotBlank(message = "Pix key cannot be blank.") String pixKey,
                                  @RequestParam @NotBlank(message = "Amount cannot be blank.") 
                                  @Pattern(regexp = "\\d+(\\.\\d{1,2})?", message = "Amount must be a valid decimal number.") String amount,
                                  @RequestParam @NotBlank(message = "Merchant city cannot be blank.") String merchantCity,
                                  @RequestParam @NotBlank(message = "Merchant name cannot be blank.") String merchantName) {
        // Validate and convert amount
        BigDecimal amountValue;
        try {
            amountValue = new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Amount must be a valid decimal number.");
        }

        // Generate Pix Copy and Paste code
        return PixCopyPasteGenerator.generatePixCopyPaste(pixKey, amountValue.toString(), merchantCity, merchantName);
    }

    @GetMapping("/generate-qr")
    public ResponseEntity<String> generateQRCode(@RequestParam @NotBlank(message = "Pix key cannot be blank.") String pixKey,
                                                 @RequestParam @NotBlank(message = "Amount cannot be blank.") 
                                                 @Pattern(regexp = "\\d+(\\.\\d{1,2})?", message = "Amount must be a valid decimal number.") String amount,
                                                 @RequestParam @NotBlank(message = "Merchant city cannot be blank.") String merchantCity,
                                                 @RequestParam @NotBlank(message = "Merchant name cannot be blank.") String merchantName) throws WriterException, IOException {
        // Validate and convert amount
        BigDecimal amountValue;
        try {
            amountValue = new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Amount must be a valid decimal number.");
        }

        // Generate Pix Copy and Paste code
        String pixCode = PixCopyPasteGenerator.generatePixCopyPaste(pixKey, amountValue.toString(), merchantCity, merchantName);

        // Generate QR Code
        byte[] qrCode = qrCodeService.generateQRCode(pixCode, 300, 300);
        String qrCodeBase64 = Base64Utils.encodeToString(qrCode);

        // Generate HTML response with copy button
        String htmlResponse = "<html><body>"
            + "<h2>Pix QR Code</h2>"
            + "<img src='data:image/png;base64," + qrCodeBase64 + "' alt='Pix QR Code'/>"
            + "<p>" + pixCode + "</p>"
            + "<button onclick='copyToClipboard()'>Copy</button>"
            + "<script>"
            + "function copyToClipboard() {"
            + "  var copyText = document.createElement('textarea');"
            + "  copyText.value = \"" + pixCode + "\";"
            + "  document.body.appendChild(copyText);"
            + "  copyText.select();"
            + "  document.execCommand('copy');"
            + "  document.body.removeChild(copyText);"
            + "  alert('Pix code copied to clipboard!');"
            + "}"
            + "</script>"
            + "</body></html>";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return ResponseEntity.ok().headers(headers).body(htmlResponse);
    }

    @GetMapping("/generate-qr-from-code")
    public ResponseEntity<String> generateQRCodeFromCode(@RequestParam @NotBlank(message = "Pix code cannot be blank.") String pixCode) throws WriterException, IOException {
        // Generate QR Code
        byte[] qrCode = qrCodeService.generateQRCode(pixCode, 300, 300);
        String qrCodeBase64 = Base64Utils.encodeToString(qrCode);

        // Generate HTML response with copy button
        String htmlResponse = "<html><body>"
            + "<h2>Pix QR Code</h2>"
            + "<img src='data:image/png;base64," + qrCodeBase64 + "' alt='Pix QR Code'/>"
            + "<p>" + pixCode + "</p>"
            + "<button onclick='copyToClipboard()'>Copy</button>"
            + "<script>"
            + "function copyToClipboard() {"
            + "  var copyText = document.createElement('textarea');"
            + "  copyText.value = \"" + pixCode + "\";"
            + "  document.body.appendChild(copyText);"
            + "  copyText.select();"
            + "  document.execCommand('copy');"
            + "  document.body.removeChild(copyText);"
            + "  alert('Pix code copied to clipboard!');"
            + "}"
            + "</script>"
            + "</body></html>";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return ResponseEntity.ok().headers(headers).body(htmlResponse);
    }
}
