package org.group.mmd.repository;

import java.io.File;

import org.group.mmd.model.Image;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import oracle.sql.BFILE;

public interface ImageRepository extends CrudRepository<Image, Integer> {

	// additional custom sql queries.
	//public String tableName = "";
	
	/*@Query(value="insert into images (id, image_path) VALUES (?1, ?2)",nativeQuery = true)
	public void insertImageEntry(String id,File imageName);*/
}
