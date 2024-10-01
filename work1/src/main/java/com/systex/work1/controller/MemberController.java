package com.systex.work1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.systex.work1.model.MemberModel;
import com.systex.work1.model.MemberRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    // 顯示登入頁面
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("member", new MemberModel());
        return "login"; // 返回 login.jsp 頁面
    }

    // 處理登入請求
    @PostMapping("/login")
    public ModelAndView handleLogin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        
        MemberModel member = null;

        try{
            member = memberRepository.findByUsername(username);
        }catch(Exception e){
            model.addAttribute("error", "找不到用戶");
            return new ModelAndView("login", "member", new MemberModel());
        }

        if (member != null && member.getPassword().equals(password)) {
            session.setAttribute("loggedInUser", member); // 將用戶對象存入 session
            return new ModelAndView("redirect:/index.jsp"); // 登入成功後重定向到主頁
        } else {
            model.addAttribute("error", "用戶名或密碼錯誤");
            return new ModelAndView("login", "member", new MemberModel());
        }
    }

    // 顯示註冊頁面
    @GetMapping("/register")
    public ModelAndView showRegisterPage() {
        return new ModelAndView("register", "member", new MemberModel()); // 返回 register.jsp 頁面
    }

    // 處理註冊請求
    @PostMapping("/register")
    public ModelAndView handleRegister(@ModelAttribute MemberModel member, Model model) {
        // 檢查用戶名是否已存在
        if (memberRepository.findByUsername(member.getUsername()) != null) {
            model.addAttribute("error", "用戶名已存在");
            return new ModelAndView("register", "member", new MemberModel());// 如果用戶名已存在，返回註冊頁面
        }

        try {
            memberRepository.save(member); // 創建新用戶
        } catch (Exception e) {
            model.addAttribute("error", "註冊失敗，請重試");
            return new ModelAndView("register", "member", new MemberModel());// 如果用戶名已存在，返回註冊頁面
        }

        return new ModelAndView("redirect:/login", "member", new MemberModel()); // 註冊成功後重定向到登入頁面
    }

    // 處理登出請求
    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.removeAttribute("loggedInUser"); // 刪除 session 中的用戶對象
        return "redirect:/login"; // 登出後重定向到登入頁面
    }
}
