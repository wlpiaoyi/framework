package org.wlpiaoyi.framework.lab.documet;

//import com.spire.pdf.exporting.xps.schema.Resources;
import com.spire.additions.xps.schema.Resources;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * <p>
 * `aspose-words`授权处理
 * </p>
 *
 * @author zhengqing
 * @description
 * @date 2020/12/30$ 11:33$
 */
@Slf4j
public class License {

	public static final void CHECK() throws Exception {
		/*
		* 实现匹配文件授权 -> 去掉头部水印 `Evaluation Only. Created with Aspose.Words. Copyright 2003-2018 Aspose Pty Ltd.` |
		* 							  `Evaluation Only. Created with Aspose.Cells for Java. Copyright 2003 - 2020 Aspose Pty Ltd.`
		*/
		InputStream inputStream = Resources.class.getResourceAsStream("/license.xml");
		com.aspose.words.License license = new com.aspose.words.License();
		license.setLicense(inputStream);
	}

}
