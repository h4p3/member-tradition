package com.h4p3.view;

import com.h4p3.annotation.Auth;
import com.h4p3.entity.LoginEntity;
import com.h4p3.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member/")
public class MemberView {

    private final MemberService memberService;

    public MemberView(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody LoginEntity loginEntity) {
        var token = memberService.login(loginEntity);
        return ResponseEntity.ok(token);
    }

    @GetMapping("sayHi")
    @Auth("READ")
    public ResponseEntity<String> sayHi() {
        return ResponseEntity.ok(memberService.sayHi());
    }


}
