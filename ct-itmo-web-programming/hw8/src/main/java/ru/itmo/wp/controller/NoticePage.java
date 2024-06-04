package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.itmo.wp.form.NoticeCredentials;
import ru.itmo.wp.service.NoticeService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/notice")
public class NoticePage extends Page {

    private final NoticeService noticeService;

    public NoticePage(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public String getNotice(Model model) {
        model.addAttribute("noticeForm", new NoticeCredentials());
        return "NoticePage";
    }

    @PostMapping
    public String postNotice(@Valid @ModelAttribute("noticeForm") NoticeCredentials noticeForm,
                             BindingResult bindingResult,
                             HttpSession httpSession) {
        if (bindingResult.hasErrors()) {
            return "NoticePage";
        }
        noticeService.saveNoticeCredentials(noticeForm);
        setMessage(httpSession, "Notice was created");
        return "redirect:/";
    }
}
