package cn.xz.qrmaker.util;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * zxing 自定义工具类
 * @author gsx
 *
 */
public class MyZXingUtil {

	/**
	 * 去掉白边，但是会变动大小，待改善。。。。
	 * @param matrix
	 * @return
	 */
	private static BitMatrix deleteWhite(BitMatrix matrix) {
		int[] rec = matrix.getEnclosingRectangle();// left,top,width,height 
		int resWidth = rec[2] + 1;
		int resHeight = rec[3] + 1;
		
		BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
		resMatrix.clear();
		for (int i = 0; i < resWidth; i++) {
			for (int j = 0; j < resHeight; j++) {
				if (matrix.get(i + rec[0], j + rec[1]))
					resMatrix.set(i, j);
			}
		}
		return resMatrix;
	}
	
	/**
	 * 生成二维码图像
	 * @param url 网址
	 * @param width 宽度
	 * @param height 高度
	 * @param margin 白边,0-4
	 * @return BufferedIamge
	 * @throws WriterException
	 */
	public static Image getImage(String url,int width, int height, int margin) throws WriterException{
		MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.MARGIN, margin);
		BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE, width, height, hints);
		bitMatrix = deleteWhite(bitMatrix);
		try {
			com.google.zxing.client.j2se.MatrixToImageWriter.writeToPath(
					bitMatrix, "jpg", new File("g:/ILMerge/"+new Date().getTime()+".jpg").toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return MatrixToImageWriter.toBufferedImage(bitMatrix);
	}
	
}
