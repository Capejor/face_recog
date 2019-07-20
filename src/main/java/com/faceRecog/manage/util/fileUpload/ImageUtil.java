package com.faceRecog.manage.util.fileUpload;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


/**
 * 图片处理工具
 * 
 * @author zhangyuxiang
 *
 */
public class ImageUtil {
	
	
	
	/**
	 * 图片压缩
	 * 
	 * @param srcImage
	 *            源图片文件路径 （如：srcImage="G:/32/2015101713.jpg"）
	 * @param tarImage
	 *            目的图片文件路径 （如：tarImage="G:/32/2015101713_720_720.jpg"）
	 * @param maxPixel
	 *            转换的像素 （如：maxPixel=720）
	 * @param
	 */
	public static void transformer(String srcImage, String tarImage,
			int maxPixel) {
		// 源图片文件
		File srcImageFile = new File(srcImage);
		// 目的图片文件
		File tarImageFile = new File(tarImage);
		// 生成图片转化对象
		AffineTransform transform = new AffineTransform();
		// 通过缓存读入缓存对象
		BufferedImage image = null;
		try {
			image = ImageIO.read(srcImageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String srcImgPath = srcImageFile.getAbsoluteFile().toString();
        System.out.println(srcImgPath);
        String subfix = "jpg";
		subfix = srcImgPath.substring(srcImgPath.lastIndexOf(".")+1,srcImgPath.length());
		
		int imageWidth = image.getWidth();// 原图片的高度
		int imageHeight = image.getHeight();// 原图片的宽度
		int changeWidth = 0;// 压缩后图片的高度
		int changeHeight = 0;// 压缩后图片的宽度
		double scale = 0;// 定义小图片和原图片比例
		if (maxPixel != 0) {
			if (imageWidth > imageHeight) {
				changeWidth = maxPixel;
				scale = (double) changeWidth / (double) imageWidth;
				changeHeight = (int) (imageHeight * scale);
			} else {
				changeHeight = maxPixel;
				scale = (double) changeHeight / (double) imageHeight;
				changeWidth = (int) (imageWidth * scale);
			}
		}
		// 生成转换比例
		transform.setToScale(scale, scale);
		// 生成压缩图片缓冲对象
		BufferedImage basll =null;
		if(subfix.equals("png")){
			basll = new BufferedImage(changeWidth, changeHeight, BufferedImage.TYPE_INT_RGB);
		}else{
			basll = new BufferedImage(changeWidth, changeHeight, BufferedImage.TYPE_3BYTE_BGR);
		}
		/*BufferedImage basll = new BufferedImage(changeWidth, changeHeight,
				BufferedImage.TYPE_INT_ARGB);*/
		
		/*// 生成缩小图片
		transOp.filter(image, basll);*/
		Graphics2D graphics = basll.createGraphics();
		graphics.setBackground(new Color(255,255,255));
		graphics.setColor(new Color(255,255,255));
		graphics.fillRect(0, 0, changeWidth, changeHeight);
		graphics.drawImage(image.getScaledInstance(changeWidth, changeHeight, Image.SCALE_SMOOTH), 0, 0, null);
		try {
			// 输出缩小图片
			ImageIO.write(basll, "jpg", tarImageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
