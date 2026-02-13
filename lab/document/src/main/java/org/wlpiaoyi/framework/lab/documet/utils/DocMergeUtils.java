package org.wlpiaoyi.framework.lab.documet.utils;

import com.aspose.words.SaveFormat;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DocMergeUtils {


	/**
	 * 合并文件
	 * @param inputPaths
	 * @param outputPath
	 * @param format
	 */
	@SneakyThrows
	public static void merge(List<String> inputPaths,
								String outputPath,
								int format) {
		if (inputPaths == null || inputPaths.isEmpty()) throw new FileNotFoundException("没有找到输入文件");
		if (outputPath == null || outputPath.length() == 0) throw new FileNotFoundException("没有输出流");
		List<File> inputFiles = new ArrayList<>();
		for(String inputPath : inputPaths){
			inputFiles.add(new File(inputPath));
		}
		FileOutputStream outputStream = new FileOutputStream(outputPath);
		merge(inputFiles, outputStream, format);
	}

	/**
	 * 合并文件
	 * @param inputFiles
	 * @param outputStream
	 * @param format
	 */
	@SneakyThrows
	public static void merge(List<File> inputFiles,
								OutputStream outputStream,
								int format) {

		if (inputFiles == null || inputFiles.isEmpty()) throw new FileNotFoundException("没有找到输入文件");
		if (outputStream == null) throw new FileNotFoundException("没有输出流");

		switch (format) {
			case SaveFormat.PDF: {
			}
			break;
			default:
				throw new Exception("不支持当前格式");
		}
		Document document = null;
		PdfCopy copy = null;
		try {

			document = new Document(new PdfReader(new FileInputStream(inputFiles.get(0))).getPageSize(1));
			copy = new PdfCopy(document, outputStream);
			document.open();
			for (File inputFile : inputFiles) {
				// 如果PDF文件不存在，则跳过
				if (!inputFile.exists()) {
					continue;
				}
				// 读取需要合并的PDF文件
				PdfReader reader = new PdfReader(new FileInputStream(inputFile));
				// 获取PDF文件总页数
				int n = reader.getNumberOfPages();
				for (int j = 1; j <= n; j++) {
					document.newPage();
					PdfImportedPage page = copy.getImportedPage(reader, j);
					copy.addPage(page);
				}
			}
		}finally {
			if (copy != null) {
				try {
					copy.close();
				} catch (Exception ex) {
					log.error("", ex);
				}
			}
			if (document != null) {
				try {
					document.close();
				} catch (Exception ex) {
					log.error("", ex);
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception ex) {
					log.error("", ex);
				}
			}
		}
	}

}
