package org.group.mmd.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.group.mmd.model.Image;
import org.springframework.stereotype.Service;

@Service("imageServie")
public class ImageServiceImpl implements ImageService {

	@Override
	public Image createImage(String filePath) {
		// TODO Auto-generated method stub
		
		
		File file = new File(".\\"+filePath+".jpg");
		System.out.println("File Path :" + filePath);
		byte[] imageData = new byte[(int) file.length()];

		try(FileInputStream fileInputStream = new FileInputStream(file)){
			fileInputStream.read(imageData);
		}catch(IOException e){
			e.printStackTrace();
		}

		//List<Image> depts = new ArrayList<Image>();
		Image image = new Image();
		image.setFileImage(imageData);
		
		
		return image;
	}

}
