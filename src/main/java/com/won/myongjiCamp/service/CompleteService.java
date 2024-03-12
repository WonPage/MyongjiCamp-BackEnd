package com.won.myongjiCamp.service;

import com.won.myongjiCamp.dto.request.CompleteDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.model.board.Image;
import com.won.myongjiCamp.repository.CompleteRepository;
import com.won.myongjiCamp.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompleteService {

    final private ImageManager imageManager;
    final private CompleteRepository completeRepository;
    final private ImageRepository imageRepository;

    @Transactional
    public void create(CompleteDto completeDto, Member member) throws IOException {
        CompleteBoard completeBoard = new CompleteBoard();
        completeBoard.setTitle(completeDto.getTitle());
        completeBoard.setContent(completeDto.getContent());
        completeBoard.setMember(member);
        completeBoard.setImages(imageManager.saveImages(completeDto.getImages(), completeBoard));
        completeRepository.save(completeBoard);
    }

    @Transactional
    public void update(Long id, CompleteDto completeDto) throws IOException {
        CompleteBoard completeBoard = completeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        completeBoard.setTitle(completeDto.getTitle());
        completeBoard.setContent(completeDto.getContent());
        List<MultipartFile> images = completeDto.getImages();
        //사진 받고 있던 거면 냅두고 없으면 추가 없어진 건 삭제
        if (!images.isEmpty()) {
            List<Image> findImages = imageRepository.findByBoard(completeBoard);
            Set<String> imageNames = images.stream()
                    .map(MultipartFile::getOriginalFilename)
                    .collect(Collectors.toSet());

            findImages.removeIf(findImage -> {
                boolean toDelete = !imageNames.contains(findImage.getStoredFileName());
                if (toDelete) {
                    imageRepository.delete(findImage); // 이미지 삭제
                }
                return toDelete;
            });
            List<MultipartFile> newImages = new ArrayList<>();
            // 새로운 이미지 추가
            for (MultipartFile image : images) {
                if (findImages.stream().noneMatch(findImage -> Objects.equals(findImage.getStoredFileName(), image.getOriginalFilename()))) {
                    newImages.add(image);
                }
            }
            completeBoard.setImages(imageManager.saveImages(newImages, completeBoard));
        }
    }

    @Transactional
    public void delete(Long id) {
        completeRepository.deleteById(id);
    }
}
