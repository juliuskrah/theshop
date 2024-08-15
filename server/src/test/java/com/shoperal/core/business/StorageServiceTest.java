package com.shoperal.core.business;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Comparator;

import com.shoperal.core.ApplicationProperties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.TestInstantiationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Julius Krah
 */
@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
public class StorageServiceTest {

	private FileSystemStorageService storageService;
	FileSystemResource resource = new FileSystemResource(System.getProperty("user.home") + "/shoperal/test/files");

	@BeforeAll
	void setupOnce() {
		deleteAllFiles(resource.getFile().toPath());
		ApplicationProperties properties = new ApplicationProperties();
		properties.setFileStoreBaseLocation(resource);
		try {
			storageService = new FileSystemStorageService(properties);
		} catch (IOException e) {
			throw new TestInstantiationException("Cannot instantiate Storage Service", e);
		}
	}

	@Test
	@DisplayName("when given file then save to file system")
	void testFileSystemFileSave() throws IOException {
		var resource = new ClassPathResource("files/downloadable-file.pdf");
		var bytes = Files.readAllBytes(resource.getFile().toPath());
		var uri = storageService.writeFile(bytes, null);
		assertThat(uri.getPath()).startsWith("/files/uncategorized/");
		var path = filePath(uri, "uncategorized");
		assertThat(Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)).isTrue();
	}

	@Test
	@DisplayName("when given media then save to file system")
	void testFileSystemImageSave() throws IOException {
		var resource = new ClassPathResource("images/product-category.png");
		var bytes = Files.readAllBytes(resource.getFile().toPath());
		var uri = storageService.writeMedia(bytes, null);
		assertThat(uri.getPath()).startsWith("/files/media/");
		var path = filePath(uri, "media");
		assertThat(Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)).isTrue();
	}

	@Test
	@DisplayName("when given image then save thumbnail to file system")
	void testFileSystemImageThumbnailSave() throws IOException {
		var resource = new ClassPathResource("images/product-category.png");
		var bytes = Files.readAllBytes(resource.getFile().toPath());
		var uri = storageService.writeImageThumbnail(bytes, null);
		assertThat(uri.getPath()).startsWith("/files/images/thumb/");
		var path = filePath(uri, "images/thumb");
		assertThat(Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)).isTrue();
	}

	@Test
	@DisplayName("when given url fetch image")
	void testFindFileByUrl() throws IOException {
		var text = "Shoperal® is a SaaS Platform for ecommerce merchants";
		var path = resource.getFile().toPath().resolve("testing.txt");
		Files.writeString(path, text, UTF_8, CREATE, TRUNCATE_EXISTING);
		
		var bytes = storageService.readFile(URI.create("/files/testing.txt"));
		assertThat(new String(bytes)).contains(text);
	}

	@Test
	void testFindPresignedUri() {
		var uri = storageService.getPreSignedURI(null, null);
		assertThat(uri).isEqualTo(URI.create("null/upload-media"));
	}

	private static void deleteAllFiles(Path path) {
		try {
			Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		} catch (IOException ex) {
			log.warn("Unable to delete directory", ex);
		}
	}

	private Path filePath(URI uri, String path) {
		String[] segments = uri.getPath().split("/");
		String idStr = segments[segments.length - 1];
		return this.resource.getFile().toPath().resolve(path).resolve(idStr);
	}
}
