package com.how2java.tmall.web;
 
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//控制器
@Controller
public class AdminPageController {
//    访问地址 admin,就会客户端跳转到 admin_category_list去。
    @GetMapping(value="/admin")
    public String admin(){
        return "redirect:admin_category_list";
    }
//    访问地址 admin_category_list 就会访问 admin/listCategory.html 文件。
    @GetMapping(value="/admin_category_list")
    public String listCategory(){
        return "admin/listCategory";
    }
//    增加一个对/admin_category_edit的映射，然后服务端跳转到 editCategory.html 去
    @GetMapping(value="/admin_category_edit")
    public String editCategory(){
        return "admin/editCategory";

    }
    @GetMapping(value="/admin_order_list")
    public String listOrder(){
        return "admin/listOrder";

    }
    @GetMapping(value="/admin_product_list")
    public String listProduct(){
        return "admin/listProduct";

    }

    @GetMapping(value="/admin_product_edit")
    public String editProduct(){
        return "admin/editProduct";

    }
    @GetMapping(value="/admin_productImage_list")
    public String listProductImage(){
        return "admin/listProductImage";

    }

    @GetMapping(value="/admin_property_list")
    public String listProperty(){
        return "admin/listProperty";

    }

    @GetMapping(value="/admin_property_edit")
    public String editProperty(){
        return "admin/editProperty";

    }

    @GetMapping(value="/admin_propertyValue_edit")
    public String editPropertyValue(){
        return "admin/editPropertyValue";

    }

    @GetMapping(value="/admin_user_list")
    public String listUser(){
        return "admin/listUser";

    }
}