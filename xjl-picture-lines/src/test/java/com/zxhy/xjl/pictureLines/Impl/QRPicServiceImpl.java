/**
 * 
 */
package com.zxhy.xjl.picturelines.Impl;

import java.io.File;

import com.zxhy.xjl.picturelines.service.QRPicService;
import com.zxhy.xjl.picturelines.util.ImageHelper;
import com.zxhy.xjl.picturelines.util.ImageUtil;
import com.zxhy.xjl.picturelines.util.QRCodeUtil;

/**
 * @author lenovo
 *
 */
public class QRPicServiceImpl implements QRPicService {



	@Override
	public void getQRPic(String text, String logoAddress,
			String newLogoAddress, boolean ifCompressed, String picAddress,
			int x, int y, float alpha,int n) {
		// TODO Auto-generated method stub
		 try {
			QRCodeUtil.encode(text, logoAddress, newLogoAddress, false);
		    ImageHelper.waterMark(picAddress, newLogoAddress, x, y, alpha);
			System.out.println("正在添加指纹...");
			File picFile = new File(picAddress);
			String picFileStr = picFile.getAbsolutePath();
			String destFile = picFileStr.substring(0,picFileStr.lastIndexOf("."))+"_bak.jpg"; 
					
			String fingerInfo =ImageUtil.produceFingerPrint(picFile, n);
			ImageUtil.insertFingerInfo(picFile, new File(destFile),
					fingerInfo);

			System.out.println(n + "重指纹生成完成！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void offlineCheck(String srcFile, String destFile, int n) {
		// TODO Auto-generated method stub
		ImageUtil.imageCheck(srcFile, destFile,n);
	}

	@Override
	public void onlineCheck() {
		// TODO Auto-generated method stub
		
	}




}
