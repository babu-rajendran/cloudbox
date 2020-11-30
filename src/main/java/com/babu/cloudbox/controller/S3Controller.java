package com.babu.cloudbox.controller;

import com.babu.cloudbox.exception.StorageException;
import com.babu.cloudbox.model.Metadata;
import com.babu.cloudbox.model.User;
import com.babu.cloudbox.service.AmazonClient;
import com.babu.cloudbox.service.MetadataService;
import com.babu.cloudbox.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
public class S3Controller implements HandlerExceptionResolver {

    private AmazonClient amazonClient;

    @Autowired
    private UserService userService;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    S3Controller(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    @GetMapping("/storage/delete-file/{id}")
    public ModelAndView deleteFile(@PathVariable String id) {
        Metadata metadata = metadataService.findMetadataById(Integer.valueOf(id));
        String fullFilePath = metadata.getFolder() + "/" + metadata.getFilename();
        this.amazonClient.deleteFileFromS3Bucket(fullFilePath);
        metadataService.deleteMetadata(metadata.getId());
        return new ModelAndView("redirect:/home/home");
    }

    @GetMapping("/storage/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> retrieveFile(@PathVariable String filename) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        byte[] data = amazonClient.getFile(auth.getName(), filename);

        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @PostMapping("/storage/upload-file")
    public ModelAndView uploadFile(@RequestPart(value = "file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file.");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String fileUrl = amazonClient.uploadFile(file, auth.getName());

        User user = userService.findUserByEmail(auth.getName());

        String fileName = file.getOriginalFilename();
        String description = request.getParameterValues("description")[0];

        if (!fileUrl.isBlank()) {
            Metadata metadata = metadataService.findMetadataByFilename(auth.getName(), fileName);
            if (metadata == null) {
                metadataService.saveMetadata(createMetadata(user, auth, fileName, description));
            } else {
                metadata.setDescription(description);
                metadataService.saveMetadata(metadata);
            }
        }

        return new ModelAndView("redirect:/home/home");

    }

    private Metadata createMetadata(User user, Authentication auth, String fileName, String description) {
        Metadata metadata = new Metadata();
        metadata.setFilename(fileName);
        metadata.setDescription(description);
        metadata.setFolder(auth.getName());
        metadata.setFirstname(user.getFirstname());
        metadata.setLastname(user.getLastname());
        metadata.setUploaddate(new Date());
        return metadata;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView model = new ModelAndView();
        if (ex instanceof MaxUploadSizeExceededException) {
            model.setViewName("errors/size_exceeded");
        } else if (ex instanceof StorageException) {
            if (ex.getMessage().equals("Failed to store empty file.")) {
                model.setViewName("errors/empty_file");
            }
        }
        return model;
    }
}
