package org.wlpiaoyi.framework.lab.documet.utils;

import com.aspose.words.*;
import com.spire.pdf.FileFormat;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.wlpiaoyi.framework.lab.documet.License;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocParseUtils {

	static {
		try {
			License.CHECK();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 文件转换
	 * @param inputStream 输入流
	 * @param outputStream 输入流
	 * @param inputFormat
	 * @param outputFormat
	 */
	@SneakyThrows
	public static void parse(InputStream inputStream, OutputStream outputStream, int inputFormat, int outputFormat) {
		if(inputFormat == SaveFormat.PDF){
			if(outputFormat == SaveFormat.DOC){
				PdfParseToWordUtil.parse(inputStream, outputStream, FileFormat.DOC);
				return;
			}
			if(outputFormat == SaveFormat.DOCX){
				PdfParseToWordUtil.parse(inputStream, outputStream, FileFormat.DOCX);
				return;
			}
		}
		try{
			Document document = new Document(inputStream);
			switch (outputFormat){
				case SaveFormat.HTML:{
					HtmlSaveOptions saveOptions = new HtmlSaveOptions(outputFormat);
					saveOptions.setExportImagesAsBase64(true);
					saveOptions.setPrettyFormat(true);
					document.save(outputStream, saveOptions);
				}
				break;
				default:{
					document.save(outputStream, outputFormat);
				}
				break;
			}
		}finally {
			outputStream.flush();
			outputStream.close();
		}
	}

	public static void parse(File inputFile, File outputFile, int inputFormat, int outputFormat) throws FileNotFoundException {
		FileInputStream inputStream = new FileInputStream(inputFile);
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		DocParseUtils.parse(inputStream, outputStream, inputFormat, outputFormat);
	}

	@SneakyThrows
	public static void parse(String inputPath, String outputPath, int inputFormat, int outputFormat) throws FileNotFoundException {
		InputStream inputStream = createInputStream(inputPath);
		OutputStream outputStream = createOutputStream(outputPath);
		try{
			DocParseUtils.parse(inputStream, outputStream, inputFormat, outputFormat);
		}finally {
			if(inputStream != null) inputStream.close();
			if(outputStream != null) outputStream.close();
		}
	}

	public static void merge(String outPath, List<String> inputPaths) throws IOException, XmlException, InvalidFormatException {
		File outputFile = new File(outPath);
		List<File> inputFiles = new ArrayList<>();
		for (String inputPath : inputPaths){
			File inputFile = new File(inputPath);
			inputFiles.add(inputFile);
		}
		merge(outputFile, inputFiles);
	}

	public static void merge(File outputFile, List<File> inputFiles) throws IOException, XmlException, InvalidFormatException {
		OutputStream outputStream = new FileOutputStream(outputFile);
		List<InputStream> inputStreams = new ArrayList<>();
		try{
			for(File inputFile : inputFiles){
				inputStreams.add(new FileInputStream(inputFile));
			}
			merge(outputStream, inputStreams);
		}finally{
			for(InputStream inputStream : inputStreams){
				if(inputStream != null) inputStream.close();
			}
			if(outputStream != null) outputStream.close();

		}
	}

	public static void merge(OutputStream outputStream, List<InputStream> inputStreams) throws IOException, InvalidFormatException, XmlException {
		List<Closeable> closeables = new ArrayList<>();
		try{
			ArrayList<XWPFDocument> documentList = new ArrayList<>();
			for (int i = 0; i < inputStreams.size(); i++) {
				InputStream in = inputStreams.get(i);
				OPCPackage open = OPCPackage.open(in);
				closeables.add(open);
				XWPFDocument docInput = new XWPFDocument(open);
				closeables.add(docInput);
				if ( i < inputStreams.size()-1)  {
					//插入分页符
					docInput.createParagraph().createRun().addBreak(BreakType.PAGE);
				}
				documentList.add(docInput);
			}
			@Cleanup XWPFDocument docOutput  = documentList.get(0);
			for (int i = 0; i < documentList.size(); i++) {
				if (i != 0) {
					appendBody(docOutput, documentList.get(i));
				}
			}
			docOutput.write(outputStream);
		}finally {
			for(Closeable closeable : closeables){
				closeable.close();
			}
		}
	}

	public static void appendBody(XWPFDocument src, XWPFDocument append) throws InvalidFormatException, XmlException {
		CTBody src1Body = src.getDocument().getBody();
		CTBody src2Body = append.getDocument().getBody();

		List<XWPFPictureData> allPictures = append.getAllPictures();
		// 记录图片合并前及合并后的ID
		Map<String, String> map = new HashMap();
		for (XWPFPictureData picture : allPictures) {
			String before = append.getRelationId(picture);
			//将原文档中的图片加入到目标文档中
			String after = src.addPictureData(picture.getData(), org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG);
			map.put(before, after);
		}

		appendBody(src1Body, src2Body, map);

	}

	private static void appendBody(CTBody src, CTBody append, Map<String, String> map) throws XmlException {
		XmlOptions optionsOuter = new XmlOptions();
		optionsOuter.setSaveOuter();
		String appendString = append.xmlText(optionsOuter);

		String srcString = src.xmlText();
		String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
		String mainPart = srcString.substring(srcString.indexOf(">") + 1, srcString.lastIndexOf("<"));
		String sufix = srcString.substring(srcString.lastIndexOf("<"));
		String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));

		if (map != null && !map.isEmpty()) {
			//对xml字符串中图片ID进行替换
			for (Map.Entry<String, String> set : map.entrySet()) {
				addPart = addPart.replace(set.getKey(), set.getValue());
			}
		}
		//将两个文档的xml内容进行拼接
		CTBody makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + sufix);

		src.set(makeBody);
	}


	@SneakyThrows
	private static InputStream createInputStream(String path){
		if(path.startsWith("https://")){
			URL url = new URL(path);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(10 * 1000);
			return conn.getInputStream();
		}else if(path.startsWith("http://")){
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(10 * 1000);
			return conn.getInputStream();
		}else {
			return new FileInputStream(path);
		}
	}

	@SneakyThrows
	private static OutputStream createOutputStream(String path){
		if(path.startsWith("https://")){
			URL url = new URL(path);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(10 * 1000);
			return conn.getOutputStream();
		}else if(path.startsWith("http://")){
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(10 * 1000);
			return conn.getOutputStream();
		}else {
			return new FileOutputStream(path);
		}
	}

}
