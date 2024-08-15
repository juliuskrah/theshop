package com.shoperal.core.business;

import java.io.IOException;
import java.net.URI;

/**
 * Contains methods to save files and/or images. Implementations decide how and
 * where to store files
 * 
 * @author Julius Krah
 *
 */
public interface StorageService {

	/**
	 * Saves an image
	 * 
	 * @param file
	 * @param contentType the mime-type
	 * @return URI location of the stored media
	 * @throws IOException when the image cannot be saved
	 */
	URI writeMedia(byte[] file, String contentType) throws IOException;

	/**
	 * Saves an image thumbnail
	 * 
	 * @param file
	 * @param contentType the mime-type
	 * @return URI location of the stored imageF
	 * @throws IOException when the image thumbnail cannot be created
	 */
	URI writeImageThumbnail(byte[] file, String contentType) throws IOException;

	/**
	 * Saves a file, this can be a regular file or an executable
	 * 
	 * @param file
	 * @param contentType the mime-type
	 * @return URI location of the stored file
	 * @throws IOException when the file cannot be saved
	 */
	URI writeFile(byte[] file, String contentType) throws IOException;

	/**
	 * Reads a file corresponding to the uri
	 * 
	 * @param uri
	 * @return the image
	 * @throws IOException when the image cannot be read
	 */
	byte[] readFile(URI uri) throws IOException;

	/**
	 * We generate a presigned URI for the client to upload to AWS S3
	 * 
	 * @param filename the name of the file
	 * @param contentType the mime-type of the file
	 * @return the presigned URI
	 */
	URI getPreSignedURI(String filename, String contentType);
}
