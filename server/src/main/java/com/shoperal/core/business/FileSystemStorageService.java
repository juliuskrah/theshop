package com.shoperal.core.business;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import com.shoperal.core.ApplicationProperties;
import com.shoperal.core.utility.Profiles;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import lombok.NonNull;

/**
 * Implementation of StorageService that saves files on the local filesystem.
 * There is a cloud drive implementation
 * 
 * @author Julius Krah
 */
@Component("fileSystemStorageService")
@Profile(Profiles.LOCAL)
public class FileSystemStorageService implements StorageService {
	private final Path basePath;
	@Value("#{servletContext.contextPath}")
	private String servletContextPath;

	public FileSystemStorageService(ApplicationProperties properties) throws IOException {
		Assert.notNull(properties.getFileStoreBaseLocation(), 
			"FileStoreBaseLocation cannot be null, add `shoperal.file-store-base-location` to your configuration file");
		basePath = properties.getFileStoreBaseLocation().getFile().toPath();
		Files.createDirectories(basePath.resolve("media"));
		Files.createDirectories(basePath.resolve("images/thumb"));
		Files.createDirectories(basePath.resolve("uncategorized"));
	}

	@Override
	public URI writeMedia(byte[] file, String contentType) throws IOException {
		var fileName = UUID.randomUUID().toString();
		writeFile(file, basePath.resolve("media").resolve(fileName));
		return URI.create("/files/media/" + fileName);
	}

	@Override
	public URI writeImageThumbnail(byte[] file, String contentType) throws IOException {
		var fileName = UUID.randomUUID().toString();
		writeFile(file, basePath.resolve("images/thumb").resolve(fileName));
		return URI.create("/files/images/thumb/" + fileName);
	}

	@Override
	public URI writeFile(byte[] file, String contentType) throws IOException {
		var fileName = UUID.randomUUID().toString();
		writeFile(file, basePath.resolve("uncategorized").resolve(fileName));
		return URI.create("/files/uncategorized/" + fileName);
	}

	private Path writeFile(byte[] file, Path path) throws IOException {
		return Files.write(path, file);
	}

	@Override
	public byte[] readFile(@NonNull URI uri) throws IOException {
		var regex = "/files/";
		var filePath = uri.getPath().replace(regex, "");
		var path = basePath.resolve(filePath);
		return Files.readAllBytes(path);
	}

	@Override
	public URI getPreSignedURI(String filename, String contentType) {
		return URI.create(servletContextPath + "/upload-media");
	}

}
