// com.backend.controller.ImageController.java
package com.backend.controller;

import com.backend.dto.request.UploadMultiForm;
import com.backend.service.ImageFileService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.*;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageFileService imageFileService;

    public record UploadResp(String key, Integer id, String url) {}

    @Operation(summary = "멀티 이미지 업로드")
    @PostMapping(
        value = "/upload-multi",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody( // <-- Swagger 전용(실제 바인딩엔 영향 없음)
    	    content = @Content(
    	        mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
    	        schema = @Schema(implementation = UploadMultiForm.class)
    	    )
    	)
    public List<UploadResp> uploadMulti(
        @ModelAttribute UploadMultiForm form
    ) throws IOException {

        var files = form.getFiles();
        var keys  = form.getKeys();
        var result = imageFileService.saveAll(files, keys);

        return result.stream()
            .map(m -> new UploadResp(
                (String)  m.get("key"),
                (Integer) m.get("id"),
                (String)  m.get("url")))
            .toList();
    }
}
