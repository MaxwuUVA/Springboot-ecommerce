package com.how2java.tmall.web;
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
 
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.PropertyValue;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.service.PropertyValueService;
// 准备PropertyValueController类, 提供查询所有的和修改功能。
@RestController
public class PropertyValueController {
    @Autowired PropertyValueService propertyValueService;
    @Autowired ProductService productService;
//    1. 通过产品管理界面的设置属性，跳到编辑页面：admin_propertyValue_edit
//2. 获取 editPropertyValue.html 页面
//3. html中的js代码获取到 pid
//4. axios 通过 products/pid/propertyValues 提交ajax 请求
//5. PropertyValueController 的 list方法相应这个请求
//6. list 方法中 首先进行初始化：propertyValueService.init(product); ， 然后把这个产品对应的属性值都取出来返回的浏览器
//7. 浏览器拿到 json 数组， 遍历到视图上
    @GetMapping("/products/{pid}/propertyValues")
    public List<PropertyValue> list(@PathVariable("pid") int pid) throws Exception {
        Product product = productService.get(pid);
        propertyValueService.init(product);
        List<PropertyValue> propertyValues = propertyValueService.list(product);
        return propertyValues;
    }
         
    @PutMapping("/propertyValues")
    public Object update(@RequestBody PropertyValue bean) throws Exception {
        propertyValueService.update(bean);
        return bean;
    }
     
}