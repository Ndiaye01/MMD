package org.group.mmd.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.group.mmd.model.Image;
import org.group.mmd.repository.ImageRepository;
import org.group.mmd.service.DaoService;
import org.group.mmd.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.builders.GlobalDocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.searchers.GenericFastImageSearcher;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import net.semanticmetadata.lire.searchers.ImageSearcher;
import net.semanticmetadata.lire.utils.FileUtils;
import net.semanticmetadata.lire.utils.LuceneUtils;
import oracle.sql.BFILE;

@RestController
@CrossOrigin
public class ImageController {
	
	public static final String imageDirectoryPath = "D://images";
	public static final String indexPath = "D://index/";
	public static final Integer SUCCESS = 0;
	public static final Integer ERROR = 1;
	

	@Autowired
	private ImageRepository imageRepository; // Service which will do all
												// data
												// retrieval/manipulation
												// work
	@Autowired
	private ImageService imageServie;
	
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String getHello(){
		return "hey...from Webservice";
	}
	
	@RequestMapping(value = "/create/{indexName}/{tableName}", method = RequestMethod.GET)
	public static Integer odciIndexCreate(@PathVariable String indexName,@PathVariable String tableName)  {

		/* creating of index starts */
		System.out.println("new changes...");
		System.out.println("odciIndexCreate : start ");
		
		/*File f = null;
		// Checking if 'imageDirectoryPath' is there and if it is a directory.
		if (!imageDirectoryPath.isEmpty() && !("".equals(imageDirectoryPath))) {
			f = new File(imageDirectoryPath);
			System.out.println("Indexing images in " + imageDirectoryPath);
			if (f.exists() && f.isDirectory())
				passed = true;
		}
		if (!passed) {
			System.out.println("No directory given as first argument.");
			System.out.println("Run \"Indexer <directory>\" to index files of a directory.");
			// System.exit(1);
			return ERROR;
		}
		// Getting all images from a directory and its sub directories.
		try {
			ArrayList<String> images = FileUtils.readFileLines(new File(imageDirectoryPath), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ERROR;
		}*/

		// Creating a CEDD document builder and indexing all files.
		GlobalDocumentBuilder globalDocumentBuilder = new GlobalDocumentBuilder(CEDD.class);
		// Creating an Lucene IndexWriter
		IndexWriter iw;
		try {
			iw = LuceneUtils.createIndexWriter(indexPath.concat(indexName), true, LuceneUtils.AnalyzerType.WhitespaceAnalyzer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ERROR;
		}

		// creating of index ends 

		 //populate the index if the base table is not empty : Start 
		// 1. Query the base table

		// Oracle format
		// odciIndexInfo.IndexCols(1).TableSchema+"."+odciIndexInfo.IndexCols(1).TableName
		// Java format
		System.out.println("Table Name passed : "+tableName);
		try {
			
		
		ResultSet resultSet = DaoService.getFromDB(tableName);
		// 2. Iterate over the resultset
		while (resultSet.next()) {
			// 2.1 for each entry fetch rowId and the BFILE column
			String rowId = resultSet.getString("rowId");
			// 2.2 from the BFILE column fetch the path and create an BufferedImage
			BFILE src_lob = (BFILE)resultSet.getObject("image_path");
				// Open the BFILE: 
		    	src_lob.openFile();
		    	// Get a handle to stream the data from the BFILE: 
			InputStream inputStream = src_lob.getBinaryStream();
			BufferedImage bufferedImg = ImageIO.read(inputStream);
				// Close the BFILE:
				src_lob.closeFile();
			// 2.3 store image, rowid in the lucene document.
			Document document = globalDocumentBuilder.createDocument(bufferedImg, rowId); //  now that's an index entry.
			// 2.3 add document to indexWriter
			iw.addDocument(document);
		}
		
		
		 //populate the index if the base table is not empty : End 

		// Iterating through images building the low level features
		/*for (Iterator<String> it = images.iterator(); it.hasNext();) {
			String imageFilePath = it.next();
			System.out.println("Indexing " + imageFilePath);
			try {
				BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));
				// imageFilePath being the identifier of this index table
				// outside of the database.
				Document document = globalDocumentBuilder.createDocument(img, imageFilePath);
				iw.addDocument(document);
			} catch (Exception e) {
				System.err.println("Error reading image or indexing it.");
				e.printStackTrace();
			}
		}*/

		// closing the IndexWriter
		LuceneUtils.closeWriter(iw);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ERROR;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ERROR;
		}
		
		
		System.out.println("odciIndexCreate : end ");
		System.out.println("Finished indexing.");
		return SUCCESS;
			

	}

	@SuppressWarnings("rawtypes")/**/
	@RequestMapping(value = "/start/{indexName}/{imgPath}", method = RequestMethod.GET)
	public List<String> odciIndexStart(@PathVariable String indexName,@PathVariable String imgPath) throws IOException {
		//imgPath = "D://images//flower2.jpg";
		String formattedImgPath = imgPath.replace("cc", ":").replace("fs", "//").replaceAll("pp", ".");
		indexName = indexPath.concat(indexName);
		System.out.println("odciIndexStart : start ");
		System.out.println("indexName after concatenation : "+indexName);
		//System.out.println("bfile param : "+entry.toString());
		List<String> rowList = null;
		//System.out.println("odciIndexStart : entry ->"+ fileUpload);
		System.out.println("formatted image path : "+ indexName);
		try {
			rowList = MostLikely.getSimilarImages1(indexName,formattedImgPath);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("odciIndexStart : end");
		List<Image> tempList = new ArrayList<Image>();
		//tempList.add("try");
		return rowList;
		//return "Wiil do now";

	}
	
	@SuppressWarnings("rawtypes")/**/
	@RequestMapping(value = "/insert/{indexName}/{imgPath}/{rowId}", method = RequestMethod.GET)
	public Integer odciIndexInsert(@PathVariable String indexName,@PathVariable String imgPath,@PathVariable String rowId) throws IOException {
		System.out.println("odciIndexInsert : start ");
		System.out.println("indexName lire : "+indexName);
		System.out.println("imagePath lire : "+imgPath);
		System.out.println("rowId lire : "+rowId);
		imgPath = imgPath.replace("cc", ":").replace("fs", "//").replaceAll("pp", ".");
		String indexPathName = indexPath.concat(indexName);
		System.out.println("imagePath after unmarshalling lire : "+imgPath);
		System.out.println("indexPath after concatenation lire : "+indexPathName);
		// Creating a CEDD document builder and indexing all files.
		GlobalDocumentBuilder globalDocumentBuilder = new GlobalDocumentBuilder(CEDD.class);
		// Creating an Lucene IndexWriter
		IndexWriter iw = LuceneUtils.createIndexWriter(indexPath.concat(indexName), false);
		IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(indexPathName)));
		BufferedImage img = ImageIO.read(new FileInputStream(imgPath));
		Document document = globalDocumentBuilder.createDocument(img, rowId); //  now that's an index entry.
		// 2.3 add document to indexWriter
		iw.addDocument(document);
		iw.close();
		System.out.println("odciIndexInsert : end");
		return SUCCESS;
		

	}
	
	@SuppressWarnings("rawtypes")/**/
	@RequestMapping(value = "/drop/{indexName}", method = RequestMethod.GET)
	public Integer odciIndexDrop(@PathVariable String indexName) throws IOException{
		System.out.println("REST odciIndexDrop : start");
		IndexWriter iw = LuceneUtils.createIndexWriter(indexPath.concat(indexName), false);
		iw.deleteAll();
		iw.close();
		org.apache.commons.io.FileUtils.deleteDirectory(new File(indexPath.concat(indexName)));
		
		System.out.println("REST odciIndexDrop : start");
		return SUCCESS;
		
	}
	@SuppressWarnings("rawtypes")/**/
	@RequestMapping(value = "/delete/{indexName}/{rwid}", method = RequestMethod.GET)
	public Integer odciIndexDelete(@PathVariable String indexName,@PathVariable String rwid) throws IOException{
		System.out.println("REST odciIndexDelete : start");
		IndexWriter iw = LuceneUtils.createIndexWriter(indexPath.concat(indexName), false);
		iw.deleteDocuments(new Term(DocumentBuilder.FIELD_NAME_IDENTIFIER,rwid));
		iw.close();
		//org.apache.commons.io.FileUtils.deleteDirectory(new File(indexPath.concat(indexName)));
		
		System.out.println("REST odciIndexDelete : start");
		return SUCCESS;
		
	}
	
	@SuppressWarnings("rawtypes")/**/
	@RequestMapping(value = "/update/{indexName}/{imgPath}/{rowId}", method = RequestMethod.GET)
	public Integer odciIndexUpdate(@PathVariable String indexName,@PathVariable String imgPath,@PathVariable String rowId) throws IOException {
		System.out.println("odciIndexUpdate : start ");
		System.out.println("indexName lire : "+indexName);
		System.out.println("imagePath lire : "+imgPath);
		System.out.println("rowId lire : "+rowId);
		imgPath = imgPath.replace("cc", ":").replace("fs", "//").replaceAll("pp", ".");
		String indexPathName = indexPath.concat(indexName);
		System.out.println("imagePath after unmarshalling lire : "+imgPath);
		System.out.println("indexPath after concatenation lire : "+indexPathName);
		// Creating a CEDD document builder and indexing all files.
		GlobalDocumentBuilder globalDocumentBuilder = new GlobalDocumentBuilder(CEDD.class);
		// Creating an Lucene IndexWriter
		IndexWriter iw = LuceneUtils.createIndexWriter(indexPath.concat(indexName), false);
		IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(indexPathName)));
		BufferedImage img = ImageIO.read(new FileInputStream(imgPath));
		Document document = globalDocumentBuilder.createDocument(img, rowId); //  now that's an index entry.
		// 2.3 add document to indexWriter
		iw.addDocument(document);
		iw.close();
		System.out.println("odciIndexUpdate : end");
		return SUCCESS;
		

	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/images123", method = RequestMethod.GET)
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
	@RequestMapping(method = RequestMethod.POST,headers = "content-type=multipart/*",value="/upload")
	@Transactional
	public List<byte[]> uploadImage(@RequestParam("imageFile")MultipartFile file) throws IOException{
		System.out.println("uploadImage : start");
		System.out.println("Received File :"+file.getOriginalFilename() +" , Type : "+file.getContentType());
		
		byte[] payload = file.getBytes();
		ByteArrayInputStream in = new ByteArrayInputStream(payload);
		BufferedImage bImageFromConvert = ImageIO.read(in);
		ImageIO.write(bImageFromConvert, "jpg", new File(imageDirectoryPath+"/"+file.getOriginalFilename()));
		System.out.println("uploadImage : end");
		
		Integer id = testInsert("images",file.getOriginalFilename());
			
		List<byte[]> list = getBfileDB(id);
		return list;
	}
	
	@RequestMapping(method = RequestMethod.POST,value="/testInsert/{tableName}/{imgName}")
	@Transactional
	public Integer testInsert(@PathVariable String tableName,@PathVariable String imgName){
		//imgName = imgName.concat(".").concat("jpg");
		//String sql = "insert into "+tableName+" values(10,BFILENAME ('DIR_IMAGES','"+imgName+"'))";
		//System.out.println("sql : "+sql);
		Integer id = DaoService.getIdForInsert();
		DaoService.insertDB(tableName,id,imgName);
		return id;
	}
	
	@RequestMapping(method = RequestMethod.POST,value="/searchStart/{id}")
	@Transactional
	public List<byte[]> getBfileDB(@PathVariable Integer id) throws IOException{
		System.out.println("getBfileDB : start");
		List<byte[]> list = new ArrayList<byte[]>();
		BFILE res = DaoService.getBfileDB(id);
		List<byte[]> bfiles = DaoService.getSearchDB(res);
		System.out.println("Rest end : bfile size ->"+bfiles.size());
		System.out.println("getBfileDB : end");
		InputStream in;
		byte[] arr = null;
		/*for(InputStream bfile : bfiles){
			arr= IOUtils.toByteArray(bfile);
			System.out.println("bfile name : "+arr);
			//System.out.println(bfile.getBinaryStream());
			list.add(arr);
			
		}*/
		return bfiles;
		
	}
	
	@RequestMapping(method = RequestMethod.POST,value="/add/{filePath}")
	@Transactional
	public void addImage(@PathVariable String filePath){
		imageRepository.save(imageServie.createImage(filePath));
		
	}
	

}

@Controller
@CrossOrigin
class FrontEnd{
	
	@RequestMapping("/first")
    public String hello(Model model, @RequestParam(value="name", required=false, defaultValue="World") String name) {
        model.addAttribute("name", name);
        return "index";
    }
}

