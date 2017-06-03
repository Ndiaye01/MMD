package org.group.mmd.model;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Image {
	
	@Id
	@Column(name = "image_id")
	@SequenceGenerator(name="seq",sequenceName="oracle_sequence")        
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Integer imageId;
	
	@Column(name = "file_image", unique = false, nullable = false, length = 100000)
	private byte[] fileImage;

	public Integer getImageId() {
		return imageId;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public byte[] getFileImage() {
		return fileImage;
	}

	public void setFileImage(byte[] fileImage) {
		this.fileImage = fileImage;
	}

	@Override
	public String toString() {
		return "Image [imageId=" + imageId + ", fileImage=" + Arrays.toString(fileImage) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(fileImage);
		result = prime * result + ((imageId == null) ? 0 : imageId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Image other = (Image) obj;
		if (!Arrays.equals(fileImage, other.fileImage))
			return false;
		if (imageId == null) {
			if (other.imageId != null)
				return false;
		} else if (!imageId.equals(other.imageId))
			return false;
		return true;
	}
	
	

}
