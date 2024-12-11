package goodpartner.chat.controller;


import goodpartner.chat.application.dto.response.ChatResponse;
import goodpartner.chat.service.ChatService;
import goodpartner.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class ChatController {
    private final ChatService chatService;

    //1.사용자 채팅 내역 조회
    @GetMapping("/chats")
    public ResponseEntity<ResponseDto<List<ChatResponse>>> getUserChatHistory(@AuthenticationPrincipal String userId){
        List<ChatResponse> userChatHistory = chatService.getUserChatHistory(userId);
        return ResponseEntity.ok(ResponseDto.response(200,"사용자 채팅 내역 조회 성공",userChatHistory));
    }

    //2.사용자 챗봇 질문하기
    @PostMapping("/chats")
    public ResponseEntity<ResponseDto<ChatResponse>> askQuestion(@AuthenticationPrincipal String userId,@RequestParam String message){
        ChatResponse response = chatService.saveChatAndGenerateResponse(userId, message);
        return ResponseEntity.ok(ResponseDto.response(200,"사용자 챗봇 질문 및 응답 성공", response));
    }

    //3.누적 질문수 조회
    @GetMapping("/chats/count")
    public ResponseEntity<ResponseDto<Long>> getTotalQuestions(){
        Long totalQuestions = chatService.getTotalQuestions();
        return ResponseEntity.ok(ResponseDto.response(200,"누적 질문 수 조회 성공",totalQuestions));
    }

    //4.최근 질문 3개 조회
    @GetMapping("/chats/latest")
    public ResponseEntity<ResponseDto<List<ChatResponse>>> getLatestChats(@AuthenticationPrincipal String userId){
        List<ChatResponse> latestChats = chatService.getLatestChats(userId);
        return ResponseEntity.ok(ResponseDto.response(200,"최근 질문 3개 조회 성공",latestChats));
    }

    @GetMapping("/chats/health-check")
    public ResponseEntity<String> healthCheck(){
        return ResponseEntity.ok("health-check");
    }
}
