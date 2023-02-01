package stirling.software.SPDF.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.spire.pdf.PdfCompressionLevel;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.exporting.PdfImageInfo;
import com.spire.pdf.graphics.PdfBitmap;

import stirling.software.SPDF.utils.PdfUtils;
//import com.spire.pdf.*;
@Controller
public class PasswordController {

	private static final Logger logger = LoggerFactory.getLogger(PasswordController.class);

	@GetMapping("/add-password")
	public String addPasswordForm(Model model) {
		model.addAttribute("currentPage", "add-password");
		return "add-password";
	}

	@PostMapping("/add-password")
	public ResponseEntity<byte[]> compressPDF(@RequestParam("fileInput") MultipartFile fileInput,
            @RequestParam("password") String password,
            @RequestParam(defaultValue = "128",value = "keyLength") int keyLength,
            @RequestParam(defaultValue = "false",value = "canAssembleDocument") boolean canAssembleDocument,
            @RequestParam(defaultValue = "false",value = "canExtractContent") boolean canExtractContent,
            @RequestParam(defaultValue = "false",value = "canExtractForAccessibility") boolean canExtractForAccessibility,
            @RequestParam(defaultValue = "false",value = "canFillInForm") boolean canFillInForm,
            @RequestParam(defaultValue = "false",value = "canModify") boolean canModify,
            @RequestParam(defaultValue = "false",value = "canModifyAnnotations") boolean canModifyAnnotations,
            @RequestParam(defaultValue = "false",value = "canPrint") boolean canPrint,
            @RequestParam(defaultValue = "false",value = "canPrintFaithful") boolean canPrintFaithful) throws IOException {

		PDDocument  document = PDDocument.load(fileInput.getBytes());
        AccessPermission ap = new AccessPermission();
        ap.setCanAssembleDocument(canAssembleDocument);
        ap.setCanExtractContent(canExtractContent);
        ap.setCanExtractForAccessibility(canExtractForAccessibility);
        ap.setCanFillInForm(canFillInForm);
        ap.setCanModify(canModify);
        ap.setCanModifyAnnotations(canModifyAnnotations);
        ap.setCanPrint(canPrint);
        ap.setCanPrintFaithful(canPrintFaithful);
        StandardProtectionPolicy spp = new StandardProtectionPolicy(password, password, ap);
        spp.setEncryptionKeyLength(keyLength);

        spp.setPermissions(ap);
        
        document.protect(spp);

     // Save the rearranged PDF to a ByteArrayOutputStream
	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	document.save(outputStream);

	// Close the original document
	document.close();

	// Prepare the response headers
	HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.APPLICATION_PDF);
	headers.setContentDispositionFormData("attachment", "compressed.pdf");
	headers.setContentLength(outputStream.size());

	// Return the response with the PDF data and headers
	return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
	}

}
