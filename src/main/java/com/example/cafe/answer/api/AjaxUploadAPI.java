package com.example.cafe.answer.api;

import com.example.cafe.answer.model.BoardDAO;
import com.example.cafe.answer.service.BoardService;
import com.example.cafe.common.UploadFileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("api/upload")
public class AjaxUploadAPI {
	private static final String UPLOAD_PATH = "c:/upload/";
	
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardDAO boardDao;


    /** 🔥 파일 업로드 */
    @PostMapping("/ajax_upload")
    public ResponseEntity<String> ajaxUpload(@RequestParam("file") MultipartFile file) throws Exception {
        // ✅ 파일 저장
        String filePath = UploadFileUtils.UploadFile(UPLOAD_PATH, file.getOriginalFilename(), file.getBytes());

        // ✅ 파일명만 추출
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

        return ResponseEntity.ok(fileName); // 🔥 파일명만 반환
    }


    /** 🔥 파일 다운로드 */
    @GetMapping("/display_file")
    public ResponseEntity<byte[]> displayFile(@RequestParam("file_name") String fileName) {
        try (InputStream in = new FileInputStream(UPLOAD_PATH + fileName)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.add("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            return new ResponseEntity<>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete_file")
    public ResponseEntity<String> deleteFile(@RequestParam("file_name") String fileName) {
        try {
            // ✅ 1. 파일명 디코딩
            String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
            
            // ✅ 2. 파일 경로 보완 (연/월/일 폴더 구조 반영)
            File uploadDir = new File("C:/upload/");
            File targetFile = findFileInSubdirectories(uploadDir, decodedFileName);

            // ✅ 3. 파일이 실제 존재하는지 확인
            if (targetFile == null || !targetFile.exists()) {
                System.out.println("⚠️ 파일을 찾을 수 없음: " + decodedFileName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일을 찾을 수 없습니다.");
            }

            // ✅ 4. 파일 삭제
            if (targetFile.delete()) {
                System.out.println("✅ 파일 삭제 성공: " + targetFile.getAbsolutePath());

                // ✅ 5. DB에서도 파일 삭제
                try {
                    boardDao.delete_attach(decodedFileName); // DB에서는 파일명만 삭제
                    return ResponseEntity.ok("파일 및 DB 기록 삭제 완료");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("파일 삭제 성공, 그러나 DB에서 삭제 실패: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 삭제 실패");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 삭제 중 오류 발생");
        }
    }

    /** 🔥 파일을 폴더 구조에서 찾는 유틸리티 메서드 */
    private File findFileInSubdirectories(File directory, String fileName) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    File found = findFileInSubdirectories(file, fileName);
                    if (found != null) return found;
                } else if (file.getName().equals(fileName)) {
                    return file;
                }
            }
        }
        return null;
    }








}
