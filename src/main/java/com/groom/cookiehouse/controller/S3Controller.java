package com.groom.cookiehouse.controller;

import com.groom.cookiehouse.common.dto.BaseResponse;
import com.groom.cookiehouse.service.Image.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {

    private final S3Service awsS3Service;

    /**
     * Amazon S3에 이미지 업로드
     * @param multipartFiles img 파일들(여러 파일 업로드 가능)
     * @return 성공 시 200 Success와 함께 업로드 된 파일의 파일명 리스트 반환
     */
    @PostMapping("/image")
    public BaseResponse<List<String>> uploadImage(@RequestPart("imgFiles") List<MultipartFile> multipartFiles) {
        List<String> uploadedFileNames = awsS3Service.uploadImage(multipartFiles);
        return BaseResponse.ok(uploadedFileNames);
    }

    /**
     * Amazon S3에 이미지 업로드 된 파일을 삭제
     *
     * @param fileName img 파일 하나 삭제
     * @return 성공 시 200 Success
     */
    @DeleteMapping("/image")
    public BaseResponse<Void> deleteImage(@RequestParam String fileName) {
        awsS3Service.deleteImage(fileName);
        return BaseResponse.ok();
    }
}
