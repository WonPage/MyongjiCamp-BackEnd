package com.won.myongjiCamp.service;

import com.won.myongjiCamp.dto.request.BoardRequest;
import com.won.myongjiCamp.dto.request.BoardRequest.CompleteDto;
import com.won.myongjiCamp.dto.response.BoardResponse;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.model.board.Image;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.repository.CompleteRepository;
import com.won.myongjiCamp.repository.ImageRepository;
import com.won.myongjiCamp.repository.RecruitRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompleteService {

    final private CompleteRepository completeRepository;
    final private ImageRepository imageRepository;
    final private S3ImageService s3ImageService;
    final private RecruitRepository recruitRepository;

    @Transactional
    public BoardResponse.WriteCompleteResponseDto create(BoardRequest.CompleteDto completeDto, Member member, Long id) {
        RecruitBoard recruitBoard = recruitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (completeDto.getImages().size() > 5) {
            throw new IllegalArgumentException("최대 5개의 이미지만 업로드할 수 있습니다.");
        }

        List<MultipartFile> images = completeDto.getImages();
        List<String> imageUrls = new ArrayList<>();
        List<Image> saveImageList = new ArrayList<>();

        uploadS3(images, imageUrls, saveImageList);

        CompleteBoard completeBoard = getCompleteBoard(completeDto, member, saveImageList);
        connectBoard(recruitBoard, completeBoard);
        completeRepository.save(completeBoard);

        return new BoardResponse.WriteCompleteResponseDto(member.getId(), completeBoard.getId(), imageUrls);
    }

    private void connectBoard(RecruitBoard recruitBoard, CompleteBoard completeBoard) {
        recruitBoard.connectCompleteBoard(completeBoard);
        completeBoard.connectRecruitBoard(recruitBoard);
    }

    private CompleteBoard getCompleteBoard(CompleteDto completeDto, Member member, List<Image> saveImageList) {
        CompleteBoard completeBoard = CompleteBoard.builder()
                .title(completeDto.getTitle())
                .content(completeDto.getContent())
                .member(member)
                .build();

        for (Image image : saveImageList) {
            completeBoard.addImage(image);
        }

        return completeBoard;
    }

    private void uploadS3(List<MultipartFile> images, List<String> imageUrls, List<Image> saveImageList) {
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String imageUrl = s3ImageService.upload(image);
                imageUrls.add(imageUrl);
                saveImageList.add(Image.builder().url(imageUrl).build());
            }
        }
    }

    @Transactional
    public BoardResponse.WriteCompleteResponseDto update(Long id, BoardRequest.CompleteDto completeDto) {
        CompleteBoard completeBoard = completeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        List<MultipartFile> newImages = completeDto.getImages();
        List<String> newImageUrls = new ArrayList<>();
        List<Image> newImageList = new ArrayList<>();

        if (newImages.size() > 5) {
            throw new IllegalArgumentException("최대 5개의 이미지만 업로드할 수 있습니다.");
        }

        uploadS3(newImages, newImageUrls, newImageList);
        updateCompleteBoard(completeDto, completeBoard, newImageList);

        return new BoardResponse.WriteCompleteResponseDto(completeBoard.getMember().getId(), completeBoard.getId(),
                newImageUrls);
    }

    private void updateCompleteBoard(CompleteDto completeDto, CompleteBoard completeBoard, List<Image> newImageList) {
        completeBoard.changeTitle(completeDto.getTitle());
        completeBoard.changeContent(completeDto.getContent());

        deleteExistingImage(completeBoard);

        for (Image image : newImageList) {
            completeBoard.addImage(image);
        }

        completeBoard.updateModifiedDate();
    }

    private void deleteExistingImage(CompleteBoard completeBoard) {
        List<Image> existingImages = imageRepository.findByBoard(completeBoard);
        for (Image image : existingImages) {
            try {
                s3ImageService.deleteImageFromS3(image.getUrl());
            } catch (Exception e) {
                throw new IllegalArgumentException("이미지 삭제 실패: " + image.getUrl());
            }
        }
        completeBoard.getImages().clear();
    }

    @Transactional
    public void delete(Long id) {
        CompleteBoard completeBoard = completeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        List<Image> images = imageRepository.findByBoard(completeBoard);

        for (Image image : images) {
            try {
                s3ImageService.deleteImageFromS3(image.getUrl());
            } catch (Exception e) {
                throw new IllegalArgumentException("이미지 삭제 실패: " + image.getUrl());
            }
        }

        RecruitBoard recruitBoard = (RecruitBoard) completeBoard.getWriteRecruitBoard();

        if (recruitBoard != null) {
            recruitBoard.setWriteCompleteBoard(null);
        }

        completeRepository.deleteById(id);
    }
}

