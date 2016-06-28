package com.zxhy.xjl.picturelines.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;


public class ImageUtil {

	
	public static final boolean SRC_NOT_NULL=true;
	public static final boolean ILLEGAL_INPUT=true;
	public static final boolean DEST_DISTORT=true;
	public static final boolean DEST_NORMAL=true;
	public static final boolean DEST_PRINT_OK=true;

   /**
    * 验证图片真伪
    * @param srcFileStr
    * @param destFileStr
    * @param n
    * @return
    */
	public static boolean imageCheck(String srcFileStr,String destFileStr,int n){
		File srcFile = new File(srcFileStr);
		File destFile = new File(destFileStr);
		if (!srcFile.isFile())
			System.out.println("文件不存在！");
		else {

	
			if (destFile.isFile())
				if (isTamper(destFile, n))
					{System.out.println("是真实图片，没有篡改!");
			            return true;
					}
				else
					{System.out.println("是非法图片，请注意信息安全！");
                        return false;
					}
			else {

				System.out.println("添加指纹...");
				String fingerInfo = produceFingerPrint(srcFile, n);
				int picbytes = insertFingerInfo(srcFile, destFile,
						fingerInfo);

				System.out.println(n + "重指纹生成完成！");
				return true;
			}

			// int n = Integer.parseInt(in.nextLine());

		}
		return false;
		
		
	}
	
	/**
	 * 
	 * @param file 待验证图片地址
	 * @param n    指纹重数
	 * @return     1-没有篡改0-篡改
	 */
	public static boolean isTamper(File file, int n) {
		String fingerInfo = produceFingerPrint(file, n);
		String hiddenFingerInfo = readFingerInfo(file, n);
		return fingerInfo.equals(hiddenFingerInfo);
	}

	/**
	 * 生成图片指纹
	 * 
	 * @param filename
	 *            源文件
	 * 
	 * @return 图片指纹
	 * 
	 * @param n
	 *            指纹重数
	 */
	public static String produceFingerPrint(File file, int n) {
		BufferedImage source = ImageHelper.readPNGImage(file);// 读取文件
		// int realhei = source.getHeight();
		// int realwid =source.getWidth();
		StringBuffer hashCode = new StringBuffer();
		int width = 8;
		int height = 8;
		// 加n次指纹
		for (int o = 1; o <= n; o++) {
			// 第一步，缩小尺寸
			// 将图片缩小到8x8的尺寸，总共64个像素
			BufferedImage thumb = ImageHelper.thumb(source, width, height,
					false, n);

			// 第二步，色彩
			// 将缩小后的图片，转为64级灰度
			int[] pixels = new int[width * height];
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					pixels[i * height + j] = ImageHelper.rgbToGray(thumb
							.getRGB(i, j));
				}
			}
			// 第三步，计算平均像素
			int avgPixel = ImageHelper.average(pixels);
			// 第四步，比较像素的灰度
			int[] comps = new int[width * height];
			for (int i = 0; i < comps.length; i++) {
				if (pixels[i] >= avgPixel) {
					comps[i] = 1;
				} else {
					comps[i] = 0;
				}
			}
			// 第五步，计算哈希值

			for (int i = 0; i < comps.length; i += 4) {
				int result = comps[i] * (int) Math.pow(2, 3) + comps[i + 1]
						* (int) Math.pow(2, 2) + comps[i + 2]
						* (int) Math.pow(2, 1) + comps[i + 3];
				hashCode.append(ImageHelper.binaryToHex(result));
			}
		}

		return hashCode.toString();
	}

	/**
	 * 插入指纹
	 * 
	 * @param inputFile
	 *            输入文件
	 * @param outputFile
	 *            输出文件
	 * @param fingerInfo
	 *            指纹信息
	 * @return
	 */
	public static int insertFingerInfo(File inputFile, File outputFile,
			String fingerInfo) {
		try {
			InputStream is = new FileInputStream(inputFile);
			int bytes = is.available();
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(
					outputFile));
			byte[] b = new byte[bytes];
			int n = is.read(b, 0, bytes);
			is.close();
			dos.write(b, 0, n);
			dos.writeUTF(fingerInfo);
			dos.close();
			return bytes;
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		}
		return 0;
	}

	/**
	 * 验证指纹
	 * 
	 * 
	 * @param inputFile
	 *            输入文件
	 * @param n
	 *            指纹重数
	 * @return
	 */
	public static String readFingerInfo(File inputFile, int n) {

		try {
			InputStream inputStream = new FileInputStream(inputFile);
			DataInputStream dis = new DataInputStream(inputStream);
			long yyy = dis.skip(inputStream.available() - (18 + 16 * (n - 1)));
			String result = dis.readUTF();
			dis.close();
			return result;
		} catch (IOException e) {

		}
		return "";
	}

}
