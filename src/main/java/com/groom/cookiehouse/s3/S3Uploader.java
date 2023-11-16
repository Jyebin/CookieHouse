package com.groom.cookiehouse.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.groom.cookiehouse.exception.ErrorCode;
import com.groom.cookiehouse.exception.model.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {
    @Value("${BUCKET_NAME}")
    private String bucket; //이미지를 저장할 곳
    private final AmazonS3 amazonS3;


    public List<String> s3MultipleUploadOfFileNullSafe( //파일을 업로드. 업로드된 파일의 url 리스트 반환
            String domainName, List<MultipartFile> multipartFileList) {
        List<String> fileUrlList = new ArrayList<>();
        //domainName : S3에 업로드될 폴더의 경로 지정
        //multipartFileList : 업로드할 파일의 list

        if (multipartFileList != null) {
            for (MultipartFile multipartFile : multipartFileList) {
                if (!multipartFile.isEmpty()) {
                    fileUrlList.add(s3UploadOfFile(domainName, multipartFile));
                }
            }
        }
        return fileUrlList;
    }

    public String s3UploadOfFile(String domainName, MultipartFile multipartFile) { //단일 파일 업로드
        if (multipartFile.isEmpty() ||
                !StringUtils.startsWithIgnoreCase(multipartFile.getContentType(), "image")) { //파일이 비어있거나 이미지가 아닌 경우 예외 발생
            throw new CustomException(ErrorCode.REQUEST_VALIDATION_EXCEPTION,"올바른 파일이 아닙니다.");
        }

        String fileName = createFileName(multipartFile.getOriginalFilename());

        return s3Upload(domainName, fileName, multipartFile);
    }

    private String s3Upload(String folderPath, String fileNm, MultipartFile multipartFile) { //파일을 업로드

        File uploadFile = convertThrow(multipartFile); //s3에 저장될 폴더의 경로 지정

        String storeKey = folderPath + "/" + fileNm; //S3에 저장될 위치 + 저장파일명

        String imageUrl = putS3(uploadFile, storeKey); //업로드

        removeNewFile(uploadFile); //파일로 전환 + 로컬에 생성된 파일 제거

        return imageUrl;
    }


    private String putS3(File uploadFile, String storeKey) { //파일을 s3에 업로드
        amazonS3.putObject(new PutObjectRequest(bucket, storeKey, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, storeKey).toString();
    }

    private void removeNewFile(File targetFile) { //로컬에 생성된 파일 삭제
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    private File convertThrow(MultipartFile multipartFile) { //multipartFile을 로컬 파일로 변환

        //파일 저장 이름
        String storeFileName = createFileName(multipartFile.getOriginalFilename());

        File convertFile = new File(System.getProperty("user.dir") + "/" + storeFileName);

        try {
            if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
                try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                    fos.write(multipartFile.getBytes());
                }
            }
            return convertFile;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INVALID_FILE_EXCEPTION,"유효하지 않은 파일입니다.");
        }
    }

    private String createFileName(String originalFileName) { //파일 이름 생성
        return UUID.randomUUID() + "_" + originalFileName;
    }

    public void deleteS3Image(String imageUrl){ //s3에 업로드된 이미지 삭제
        if(StringUtils.hasText(imageUrl)){
            String key=imageUrl.replace(amazonS3.getUrl(bucket, "").toString(), "").substring(1);
            amazonS3.deleteObject(bucket,key);
        }
    }
}
