package com.backend.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(name = "UploadMultiForm", description = "멀티 이미지 업로드 폼")
public class UploadMultiForm {

    @Schema(description = "업로드할 파일 목록")
    @ArraySchema(
        schema = @Schema(type = "string", format = "binary")
    )
    private List<MultipartFile> files; 

    @Schema(description = "각 파일의 키(예: cover, room-0, room-1...)")
    @ArraySchema(
        schema = @Schema(type = "string")
    )
    private List<String> keys;
}
