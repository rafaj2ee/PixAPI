package com.rafaj2ee.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.WriterException;
import com.rafaj2ee.dto.PixDTO;
import com.rafaj2ee.model.Counter;
import com.rafaj2ee.model.Pix;
import com.rafaj2ee.service.CounterService;
import com.rafaj2ee.service.PixService;
import com.rafaj2ee.service.QRCodeService;
import com.rafaj2ee.util.PixCopyPasteGenerator;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/api/v1/pix")
public class PixController {

    @Autowired
    private PixService pixService;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private CounterService counterService;
    
    @PostMapping
    public Pix savePix(@Valid @RequestBody PixDTO dto) throws Exception {
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
            Counter counter = counterService.findOrCreateCounterByName("generate");
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
                                                 @RequestParam @NotBlank(message = "Merchant name cannot be blank.") String merchantName
                                                 , HttpServletRequest request) throws WriterException, IOException {
        // Validate and convert amount
        BigDecimal amountValue;
        try {
        	
            amountValue = new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Amount must be a valid decimal number.");
        }
//        String ipAddress = request.getRemoteAddr();
//        String userAgent = request.getHeader("User-Agent");
//        System.out.println("IP do requisitante: " + ipAddress);
//        System.out.println("User-Agent: " + userAgent);
//        System.out.println(request.getRemoteUser());
//        System.out.println(request.getRemoteHost());
//        System.out.println(request.getUserPrincipal());
//        System.out.println(request.getLocalAddr());
//        System.out.println(request.getLocalName());
//        System.out.println(request.getLocale().getDisplayName());
        // Generate Pix Copy and Paste code
//        String pixCode = PixCopyPasteGenerator.generatePixCopyPaste(pixKey, amountValue.toString(), merchantCity, merchantName);

        // Generate QR Code
        Counter counter = counterService.findOrCreateCounterByName("generate-qr");
        PixDTO dto = new PixDTO();
        dto.setAmount(amountValue);
        dto.setDescription("Pix QR Generated");
        dto.setMerchantCity(merchantCity);
        dto.setMerchantName(merchantName);
        dto.setPixKey(pixKey);        
        dto.setTransactionDate(LocalDate.now().toString());
        String qrCodeBase64 = null;
        String pixCode = null;
        try {
        	pixCode = pixService.saveTransaction(dto).getPixCode();
	        byte[] qrCode = qrCodeService.generateQRCode(pixCode, 300, 300);
	        qrCodeBase64 = Base64Utils.encodeToString(qrCode);

		} catch (Exception e) {
			log.error(e.getMessage()+e.getCause());
		}
        // Generate HTML response with copy button
        String htmlResponse = "<html><body>"
            + "<h2>Pix QR Code</h2>"
            + "<img src='data:image/png;base64," + qrCodeBase64 + "' alt='Pix QR Code'/>"
            + "<p>" + pixCode + "</p>"
            + "<button onclick='copyToClipboard()'>Copy</button><br>"
            + "<p>Developed by Rafael Nascimento Lima <a href=\"mailto:rafaj2ee@gmail.com\">rafaj2ee@gmail.com</a></p>"
            + "<a href=\"https://wa.me/5511972331487\">Send WhatsApp message</a><br>"
            + "<p>Accessed "+counter.getCount()+" times</p><br>"
            + "<script>"
            + "function copyToClipboard() {"
            + "  var copyText = document.createElement('textarea');"
            + "  copyText.value = \"" + pixCode + "\";"
            + "  document.body.appendChild(copyText);"
            + "  copyText.select();"
            + "  document.execCommand('copy');"
            + "  document.body.removeChild(copyText);"
            + "  alert('Pix code copied to clipboard! " + pixCode + "');"
            + "}"
            + "</script>"
            + "</body></html>";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return ResponseEntity.ok().headers(headers).body(htmlResponse);
    }

    @GetMapping("/generate-qr-content")
    public ResponseEntity<String> generateQRCodeFromCode(@RequestParam @NotBlank(message = "Pix code cannot be blank.") String content) throws WriterException, IOException {
        // Generate QR Code
        byte[] qrCode = qrCodeService.generateQRCode(content, 300, 300);
        String qrCodeBase64 = Base64Utils.encodeToString(qrCode);
        Counter counter = counterService.findOrCreateCounterByName("generate-qr-content");
        // Generate HTML response with copy button
        String htmlResponse = "<html><body>"
            + "<h2>QR Content</h2>"
            + "<img src='data:image/png;base64," + qrCodeBase64 + "' alt='Pix QR Code'/>"
            + "<p>" + content + "</p>"
            + "<button onclick='copyToClipboard()'>Copy</button><br>"
            + "<p>Developed by Rafael Nascimento Lima <a href=\"mailto:rafaj2ee@gmail.com\">rafaj2ee@gmail.com</a></p>"
            + "<a href=\"https://wa.me/5511972331487\">Send WhatsApp message</a><br>"
            + "<p>Accessed "+counter.getCount()+" times</p><br>"
            + "<script>"
            + "function copyToClipboard() {"
            + "  var copyText = document.createElement('textarea');"
            + "  copyText.value = \"" + content + "\";"
            + "  document.body.appendChild(copyText);"
            + "  copyText.select();"
            + "  document.execCommand('copy');"
            + "  document.body.removeChild(copyText);"
            + "  alert('Content copied to clipboard! "+content+"');"
            + "}"
            + "</script>"
            + "</body></html>"; 

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return ResponseEntity.ok().headers(headers).body(htmlResponse);
    }
}
