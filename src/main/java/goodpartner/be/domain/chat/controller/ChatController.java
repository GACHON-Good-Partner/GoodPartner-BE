package goodpartner.be.domain.chat.controller;

import goodpartner.be.domain.chat.entity.Chat;
import goodpartner.be.domain.chat.service.ChatService;
import goodpartner.be.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Log4j2
public class ChatController {
    private final ChatService chatService;

    //1.사용자 채팅 내역 조회
    @GetMapping("/chats")
    public ResponseEntity<ResponseDto<List<Chat>>> getUserChatHistory(@AuthenticationPrincipal UUID userId){
        List<Chat> userChatHistory = chatService.getUserChatHistory(userId);
        return ResponseEntity.ok(ResponseDto.response(200,"사용자 채팅 내역 조회 성공",userChatHistory));
    }

    //2.사용자 챗봇 질문하기
    @PostMapping("/chats")
    public ResponseEntity<ResponseDto<Chat>> askQuestion(@AuthenticationPrincipal UUID userId,@RequestParam String message){
        Chat chat = chatService.saveChatAndGenerateResponse(userId, message);
        return ResponseEntity.ok(ResponseDto.response(200,"사용자 챗봇 질문 및 응답 성공",chat));
    }

    //3.누적 질문수 조회
    @GetMapping("/chats/count")
    public ResponseEntity<ResponseDto<Long>> getTotalQuestions(){
        Long totalQuestions = chatService.getTotalQuestions();
        return ResponseEntity.ok(ResponseDto.response(200,"누적 질문 수 조회 성공",totalQuestions));
    }

    //4.최근 질문 3개 조회
    @GetMapping("/chats/latest")
    public ResponseEntity<ResponseDto<List<Chat>>> getLatestChats(){
        List<Chat> latestChats = chatService.getLatestChats();
        return ResponseEntity.ok(ResponseDto.response(200,"최근 질문 3개 조회 성공",latestChats));
    }
}
