package com.won.myongjiCamp.service;

import com.won.myongjiCamp.model.board.CompleteBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ImageManager {

    @Value("${file.dir}")
    private String storePath; //파일 저장할 경로

    //확장자 추출
    private String extractExt(String uploadFileName) {
        int pos = uploadFileName.lastIndexOf(".");
        return uploadFileName.substring(pos + 1);
    }

    //저장되는 파일 이름 결정 ( 같은 사진을 올리면 중복 오류
    public String createStoreFileName(String uploadFileName) {
        String ext = extractExt(uploadFileName); //확장자 가져오기
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    //저장되는 경로 반환
    public String getImagePath(String uploadFileName, String ext) {
        String imagePath = storePath + ext + "/" + uploadFileName;
        return imagePath;
    }

    //이미지 저장
    public void saveImage(MultipartFile multipartFile, CompleteBoard completeBoard) {
//        String st
//        if(!multipartFile.isEmpty()){
//            multipartFile.transferTo(getImagePath());
//        }
    }
}
