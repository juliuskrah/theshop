package com.shoperal.core.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.net.URI;

import com.shoperal.core.business.StorageService;
import com.shoperal.core.utility.Profiles;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Julius Krah
 */
@WithMockUser
@WebMvcTest(controllers = SimpleFileController.class, properties = "spring.profiles.active=" + Profiles.LOCAL)
public class SimpleFileControllerTest {
        @Autowired
        private MockMvc mvc;

        @MockBean(name = "fileSystemStorageService")
        private StorageService storageService;

        @Test
        void testUploadImage() throws Exception {
                given(this.storageService.writeMedia(any(byte[].class), any()))
                                .willReturn(URI.create("/files/images/random_file_name"));
                var file = "image-bytes".getBytes();
                var mvcResult = this.mvc.perform(put("/upload-media").content(file).with(csrf())) //
                                .andExpect(request().asyncStarted()) //
                                .andDo(log()).andReturn();
                this.mvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk()) //
                                .andExpect(content().string("\"/files/images/random_file_name\""));
        }

        @Test
        void testUploadFile() throws Exception {
                given(this.storageService.writeFile(any(byte[].class), any()))
                                .willReturn(URI.create("/files/uncategorized/random_file_name"));
                var file = new MockMultipartFile("file", "software.exe", "application/octet-stream", "image-bytes".getBytes());
                var mvcResult = this.mvc.perform(multipart("/upload-file").file(file).with(csrf())) //
                                .andExpect(request().asyncStarted()) //
                                .andDo(log()).andReturn();
                this.mvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk()) //
                                .andExpect(content().string("\"/files/uncategorized/random_file_name\""));
        }

        @Test
        @DisplayName("upload image when content-type is provided")
        void testUploadWhenContentType() throws Exception {
                var argument = ArgumentCaptor.forClass(String.class);
                var file = "image-content-type-bytes".getBytes();
                var mvcResult = this.mvc.perform(put("/upload-media")
                                .content(file).contentType(MediaType.IMAGE_PNG) //
                                .param("contentType", "video/avi").with(csrf())) //
                                .andExpect(request().asyncStarted()) //
                                .andDo(log()).andReturn();
                this.mvc.perform(asyncDispatch(mvcResult)) //
                                .andExpect(status().isOk());
                verify(this.storageService).writeMedia(any(byte[].class), argument.capture());
                assertThat("video/avi").isEqualTo(argument.getValue());
        }

        @Test
        @DisplayName("upload image when no content-type is provided")
        void testUploadWhenNoContentType() throws Exception {
                var argument = ArgumentCaptor.forClass(String.class);
                var file = "image-no-content-type-bytes".getBytes();
                var mvcResult = this.mvc.perform(put("/upload-media") //
                                .content(file).contentType(MediaType.IMAGE_PNG).with(csrf())) //
                                .andExpect(request().asyncStarted()) //
                                .andDo(log()).andReturn();
                this.mvc.perform(asyncDispatch(mvcResult)) //
                                .andExpect(status().isOk());
                verify(this.storageService).writeMedia(any(byte[].class), argument.capture());
                assertThat("image/png;charset=UTF-8").isEqualTo(argument.getValue());
        }

        @Test
        @DisplayName("upload image will throw IOExeption")
        void testUploadImageThrows() throws Exception {
                given(this.storageService.writeMedia(any(byte[].class), any())) //
                                .willThrow(new IOException("Encountered an error while saving file"));
                var file = "image-bytes".getBytes();
                var mvcResult = this.mvc.perform(put("/upload-media").content(file).with(csrf())) //
                                .andExpect(request().asyncStarted()) //
                                .andDo(log()).andReturn();
                this.mvc.perform(asyncDispatch(mvcResult)).andExpect(status().isUnprocessableEntity()) //
                                .andExpect(content().json(
                                                "{statusCode:422, message: \"Encountered an error while saving file\"}",
                                                false));
        }

        @Test
        @DisplayName("upload image will throw InvalidMediaTypeException")
        void testUploadImageWrongContentType() throws Exception {
                var file = "image-bytes".getBytes();
                this.mvc.perform(put("/upload-media").content(file) //
                                .param("contentType", "image/png/wind").with(csrf())) //
                                .andExpect(request().asyncNotStarted()) //
                                .andExpect(status().isBadRequest()) //
                                .andExpect(jsonPath("$.statusCode").value(400))
                                .andDo(log()).andReturn();
        }

        @Test
        void testLoadMedia() throws Exception {
                var argument = ArgumentCaptor.forClass(URI.class);
                this.mvc.perform(get("/files/media/thumb/random_file_name")) //
                                .andExpectAll(status().isOk(), //
                                              content().contentType(MediaType.IMAGE_JPEG), //
                                              header().exists("Content-Disposition")) //
                ;
                verify(this.storageService).readFile(argument.capture());
                assertThat(argument.getValue()).isEqualTo(URI.create("/files/media/thumb/random_file_name"));
        }

        @Test
        void testLoadFile() throws Exception {
                var argument = ArgumentCaptor.forClass(URI.class);
                this.mvc.perform(get("/files/uncategorized/random_file_name")) //
                                .andExpect(status().isOk()) //
                                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM)) //
                                .andExpect(header().exists("Content-Disposition"));
                verify(this.storageService).readFile(argument.capture());
                assertThat(argument.getValue()).isEqualTo(URI.create("/files/uncategorized/random_file_name"));
        }
}
