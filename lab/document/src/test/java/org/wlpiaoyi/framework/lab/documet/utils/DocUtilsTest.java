package org.wlpiaoyi.framework.lab.documet.utils;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.wlpiaoyi.framework.lab.documet.License;
import org.wlpiaoyi.framework.lab.documet.config.Constants;

import java.io.File;
import java.util.ArrayList;

@Slf4j
public class DocUtilsTest {



	static {
		try {
			License.CHECK();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		File file = new File(Constants.DEFAULT_TARGET_TMP_GENERATE);
		if(!file.exists()){
			file.mkdirs();
		}
	}

	/**
	 * docx转换demo
	 * @throws Exception
	 */
	@Test
	public void parseDocx5Format() throws Exception {
		// load the file to be converted
		String inputPath = "C:\\Home\\Document\\Temp\\3.docx";
		DocParseUtils.parse(inputPath,  Constants.DEFAULT_TARGET_TMP_GENERATE + "\\out.3.doc.html", SaveFormat.DOCX, SaveFormat.HTML);
		DocParseUtils.parse(inputPath,  Constants.DEFAULT_TARGET_TMP_GENERATE + "\\out.3.doc.pdf", SaveFormat.DOCX, SaveFormat.PDF);
		DocParseUtils.parse(Constants.DEFAULT_TARGET_TMP_GENERATE + "\\out.3.doc.html",  Constants.DEFAULT_TARGET_TMP_GENERATE + "\\out.3.doc.html.pdf", SaveFormat.HTML, SaveFormat.PDF);
//		inputPath = "C:\\Home\\Document\\Temp\\1.pdf";
//		DocParseUtils.parse(inputPath,  Constants.DEFAULT_TARGET_TMP_GENERATE + "\\out.1.pdf2.html", SaveFormat.PDF, SaveFormat.HTML);
//		PdfParseToWordUtil.parseToHTML(inputPath,  Constants.DEFAULT_TARGET_TMP_GENERATE + "\\out.1.pdf.html");
//		DocParseUtils.parse(Constants.DEFAULT_TARGET_TMP_GENERATE + "\\out.1.pdf.docx",  Constants.DEFAULT_TARGET_TMP_GENERATE + "\\out.1.pdf.docx.html", SaveFormat.DOCX, SaveFormat.HTML);

	}

}
