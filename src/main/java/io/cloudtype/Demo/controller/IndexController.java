package io.cloudtype.Demo.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;

@Controller
public class IndexController {
    private final WebClient webClient;

    public IndexController() {
        this.webClient = WebClient.create("http://localhost:5000");
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/predict")
    public String predict() {
        return "predict";
    }

    @PostMapping("/predict")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일을 선택해주세요.");
        }
        try {
            // Flask 서버로 파일 전송
            String response = webClient.post()
                    .uri("/predict")
                    .body(BodyInserters.fromMultipartData("file", file.getResource()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();  // 동기 방식으로 응답 대기

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("파일 업로드 실패: " + e.getMessage());
        }
    }

}
