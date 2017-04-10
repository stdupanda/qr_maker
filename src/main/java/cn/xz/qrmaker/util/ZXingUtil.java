package cn.xz.qrmaker.util;

import java.awt.Image;
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
public class ZXingUtil {
	
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
		hints.put(EncodeHintType.MARGIN, margin);
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE, width, height, hints);
		return MatrixToImageWriter.toBufferedImage(bitMatrix);
	}
}
