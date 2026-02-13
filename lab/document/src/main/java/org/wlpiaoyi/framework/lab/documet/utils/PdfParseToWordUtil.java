package org.wlpiaoyi.framework.lab.documet.utils;

import com.spire.doc.Document;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.widget.PdfPageCollection;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;

@Slf4j
class PdfParseToWordUtil {
	// 涉及到的路径
	// 1、pdf所在的路径，真实测试种是从外部引入的

	/**
	 * 2、如果是大文件，需要进行切分，保存的子pdf路径
	 */
	static String splitPath = "./split_icss/";

	/**
	 * 3、如果是大文件，需要对子pdf文件一个一个进行转化
	 */
	static String docPath = "./doc_icss/";

	static synchronized void parseToDoc(String inputPath, String outputPath) throws FileNotFoundException {
		parse(new FileInputStream(inputPath), new FileOutputStream(outputPath), FileFormat.DOC);
	}
	static synchronized void parseToDocx(String inputPath, String outputPath) throws FileNotFoundException {
		parse(new FileInputStream(inputPath), new FileOutputStream(outputPath), FileFormat.DOCX);
	}
	static synchronized void parseToHTML(String inputPath, String outputPath) throws IOException {
		try (PDDocument document = PDDocument.load(new File(inputPath))) {
			PDFTextStripper stripper = new PDFTextStripper();
			String text = stripper.getText(document);

			String html = "<!DOCTYPE html>\n" +
					"<html>\n" +
					"<head><meta charset='UTF-8'><title>Converted PDF</title></head>\n" +
					"<body><pre>" + text.replace("&", "&amp;").replace("<", "&lt;") + "</pre></body>\n" +
					"</html>";

			try (FileWriter writer = new FileWriter(outputPath)) {
				writer.write(html);
			}
		}
	}

	static synchronized void parse(InputStream inputStream, OutputStream outputStream, com.spire.pdf.FileFormat format) {
		// outputPath:最终生成的doc所在的目录，默认是和引入的一个地方，开源时对外提供下载的接口。
		boolean result = false;
		try {
			// 0、在输入的路径下新建文件夹
			boolean flag1 = create();

			if (flag1) {
				// 1、加载pdf
				PdfDocument pdf = new PdfDocument();
				pdf.loadFromStream(inputStream);
				PdfPageCollection num = pdf.getPages();

				// 2、如果pdf的页数小于11，那么直接进行转化
				if (num.getCount() <= 10) {
					pdf.saveToStream(outputStream, format);
				}
				// 3、否则输入的页数比较多，就开始进行切分再转化
				else {
					// 第一步：将其进行切分,每页一张pdf
					pdf.split(splitPath + "test{0}.pdf", 0);

					// 第二步：将切分的pdf，一个一个进行转换
					File[] fs = getSplitFiles(splitPath);
					for (File f : fs) {
						PdfDocument sonpdf = new PdfDocument();
						sonpdf.loadFromFile(f.getAbsolutePath());
						sonpdf.saveToFile(docPath + f.getName().substring(0, f.getName().length() - 4) + "." + format.getName(), format);
					}
					// 第三步：对转化的doc文档进行合并，合并成一个大的word
					try {
						result = merge(docPath, outputStream, format);
						log.debug(String.valueOf(result));
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			} else {
				log.debug("输入的不是pdf文件");
			}
		} finally {
			// 4、把刚刚缓存的split和doc删除
			if (result) {
				clearFiles(splitPath);
				clearFiles(docPath);
			}
		}
		log.debug("转换成功");
	}

	private static boolean create() {
		File f = new File(splitPath);
		File f1 = new File(docPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		if (!f.exists()) {
			f1.mkdirs();
		}
		return true;
	}

	/**
	 * 取得某一路径下所有的pdf
	 * @param path
	 * @return
	 */
	private static File[] getSplitFiles(String path) {
		File f = new File(path);
		File[] fs = f.listFiles();
		if (fs == null) {
			return null;
		}
		return fs;
	}

	public static void clearFiles(String path) {
		File file = new File(path);
		if (file.exists()) {
			deleteFile(file);
		}
	}

	public static void deleteFile(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteFile(files[i]);
			}
		}
		file.delete();
	}

	private static boolean merge(String docPath, OutputStream outputStream, com.spire.pdf.FileFormat format) {
		File[] fs = getSplitFileList(docPath);
		com.spire.doc.FileFormat docf = com.spire.doc.FileFormat.Docx;
		switch (format){
			case DOC:{
				docf = com.spire.doc.FileFormat.Doc;
			}
			break;
			case DOCX:{
				docf = com.spire.doc.FileFormat.Docx;
			}
			break;
		}
		Document document = new Document(docPath + "test0." + docf.name());

		for (int i = 1; i < fs.length; i++) {
			document.insertTextFromFile(docPath + "test" + i + "." + docf.name(), docf);
		}
		// 第四步：对合并的doc进行保存2
		document.saveToStream(outputStream, docf);
		return true;
	}

	/**
	 * 取得某一路径下所有的pdf
	 */
	private static File[] getSplitFileList(String path) {
		File f = new File(path);
		return f.listFiles();
	}
}
