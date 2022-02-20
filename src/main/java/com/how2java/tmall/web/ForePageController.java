package com.how2java.tmall.web;
 
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import javax.servlet.http.HttpSession;
 
@Controller
public class ForePageController {
    @GetMapping(value="/")
    public String index(){
        return "redirect:home";
    }
    @GetMapping(value="/home")
    public String home(){
        return "fore/home";
    }
//    增加 register地址对应的映射。
//
//    这个类很简单，就是单纯用来做服务端跳转用的，为了使学习更顺滑，把后面会用到的也都先贴出来了
//    ，都不难理解的。（就像属性管理，把后台页面跳转都写进 AdminPageController 一样）
    @GetMapping(value="/register")
    public String register(){
        return "fore/register";
    }
    @GetMapping(value="/alipay")
    public String alipay(){
        return "fore/alipay";
    }
    @GetMapping(value="/bought")
    public String bought(){
        return "fore/bought";
    }
    @GetMapping(value="/buy")
    public String buy(){
        return "fore/buy";
    }
    @GetMapping(value="/cart")
    public String cart(){
        return "fore/cart";
    }
    @GetMapping(value="/category")
    public String category(){
        return "fore/category";
    }
    @GetMapping(value="/confirmPay")
    public String confirmPay(){
        return "fore/confirmPay";
    }
    @GetMapping(value="/login")
    public String login(){
        return "fore/login";
    }
    @GetMapping(value="/orderConfirmed")
    public String orderConfirmed(){
        return "fore/orderConfirmed";
    }
    @GetMapping(value="/payed")
    public String payed(){
        return "fore/payed";
    }
    @GetMapping(value="/product")
    public String product(){
        return "fore/product";
    }
    @GetMapping(value="/registerSuccess")
    public String registerSuccess(){
        return "fore/registerSuccess";
    }
    @GetMapping(value="/review")
    public String review(){
        return "fore/review";
    }
    @GetMapping(value="/search")
    public String searchResult(){
        return "fore/search";
    }
//    @GetMapping("/forelogout")
//    public String logout( ) {
//        return "redirect:home";
//    }
//  导致ForePageController.logout()方法被调用
//  1. 在session中去掉"user"
//  2. 客户端跳转到首页:
    @GetMapping("/forelogout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:home";
    }

}