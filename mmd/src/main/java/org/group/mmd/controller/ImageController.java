package org.group.mmd.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.group.mmd.model.Image;
import org.group.mmd.repository.ImageRepository;
import org.group.mmd.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {

	@Autowired
	private ImageRepository imageRepository; // Service which will do all
												// data
												// retrieval/manipulation
												// work
	@Autowired
	private ImageService imageServie;

	@SuppressWarnings("rawtypes")
	@CrossOrigin
	@RequestMapping(value = "/images", method = RequestMethod.GET)
	public List<Image> listAllUsers() throws IOException, SQLException {

		/*String path = ".\\Strawberry.jpg";

		File file = new File(path);
		byte[] imageData = new byte[(int) file.length()];

		FileInputStream fileInputStream = new FileInputStream(file);
		fileInputStream.read(imageData);
		fileInputStream.close();

		//List<Image> depts = new ArrayList<Image>();
		Image image = new Image();
		image.setFileImage(imageData);
		imageRepository.save(image);*/

		List<Image> images = new ArrayList<Image>();

		Iterable<Image> iterable = imageRepository.findAll();
		iterable.forEach(images::add);
		System.out.println(images.size());
		return images;

	}
	@RequestMapping(method = RequestMethod.POST,value="/add/{filePath}")
	@Transactional
	public void addImage(@PathVariable String filePath){
		imageRepository.save(imageServie.createImage(filePath));
		
	}

}
