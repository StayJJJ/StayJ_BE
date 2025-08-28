// com.backend.service.ImageFileService.java
package com.backend.service;

import com.backend.repository.GuesthouseRepository;
import com.backend.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImageFileService {
    private final GuesthouseRepository guesthouseRepository;
    private final RoomRepository roomRepository;

    // 기본값 C:/upload
    @Value("${app.upload-root:C:/upload}")
    private String uploadRoot;

    /**
     * files 와 keys 를 같은 순서로 받아 저장.
     * - key = "cover"      -> Guesthouse.photoId max + 1
     * - key = "room-<idx>" -> Room.photoId max + 1
     * 저장 경로:
     *   C://upload/guesthouse/{photoId}.png
     *   C://upload/room/{photoId}.png
     */
    public synchronized List<Map<String, Object>> saveAll(List<MultipartFile> files, List<String> keys)
            throws IOException {
        if (files == null || keys == null || files.size() != keys.size()) {
            throw new IllegalArgumentException("files와 keys의 길이가 일치해야 합니다.");
        }

        // 현재 글로벌 max 값 조회 (테이블 전체 기준)
        int nextGh = Optional.ofNullable(guesthouseRepository.findMaxPhotoId()).orElse(0) + 1;
        int nextRm = Optional.ofNullable(roomRepository.findMaxPhotoId()).orElse(0) + 1;

        List<Map<String, Object>> out = new ArrayList<>(files.size());

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String key = keys.get(i);

            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("비어있는 파일이 있습니다. index=" + i);
            }
            String ct = file.getContentType();
            if (ct == null || !ct.startsWith("image/")) {
                throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
            }

            boolean isCover = "cover".equals(key);
            int assignedId = isCover ? nextGh++ : nextRm++;

            // 디렉터리: cover -> guesthouse, room-* -> room
            Path dir = Paths.get(uploadRoot, isCover ? "guesthouses" : "rooms");
            Files.createDirectories(dir);

            // 확장자는 요구대로 항상 .png (원본 변환 X 주의)
            Path target = dir.resolve(assignedId + ".png").normalize();

            // 디렉터리 탈출 방지
            if (!target.startsWith(dir)) {
                throw new SecurityException("허용되지 않은 경로입니다.");
            }

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("key", key);
            item.put("id", assignedId);         // ← photoId
            item.put("url", target.toString()); // 필요 시 URL 매핑
            out.add(item);
        }

        return out;
    }
}
