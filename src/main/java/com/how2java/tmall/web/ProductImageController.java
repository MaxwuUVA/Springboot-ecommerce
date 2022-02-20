package com.how2java.tmall.web;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
 
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
 
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.ProductImage;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.ProductImageService;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.util.ImageUtil;
  
@RestController
public class ProductImageController {
    @Autowired ProductService productService;
    @Autowired ProductImageService productImageService;
    @Autowired CategoryService categoryService;
     
    @GetMapping("/products/{pid}/productImages")
    public List<ProductImage> list(@RequestParam("type") String type, @PathVariable("pid") int pid) throws Exception {
        Product product = productService.get(pid);
 
        if(ProductImageService.type_single.equals(type)) {
            List<ProductImage> singles =  productImageService.listSingleProductImages(product);
            return singles;
        }
        else if(ProductImageService.type_detail.equals(type)) {
            List<ProductImage> details =  productImageService.listDetailProductImages(product);
            return details;
        }
        else {
            return new ArrayList<>();
        }
    }
//    增加产品图片分单个和详情两种，其区别在于增加所提交的type类型不一样。
//    这里就对单个的进行讲解，详情图片的处理同理。
//            1. 选中图片后，会导致 getSingleFile 函数被调用，vue就拿到了文件
//2. 点击提交按钮会导致addSingle函数被调用
//3. 与分类上传图片类似， axios,js 上传图片要用 FormData 的方式
//4. 上传到路径 productImages，并带上type和pid参数
//5. ProductImageController 的add方法被调用
//6. 首先根据pid 和 type 创建 ProductImage 对象，并插入数据库
//7. 然后根据类型指定保存文件的路径：productSingle
//8. 接着根据产品图片对象的id，作为文件名，把图片保存到对应的位置
//9. 像分类上传图片一样，要通过 ImageUtil.change2jpg进行类型强制转换,以确保一定是jpg图片
//10. 如果是单个图片，还要创建 small 和 middle 两种不同大小的图片，用的是 ImageUtil.resizeImage 函数
//    以上就是单个图片的处理。

    @PostMapping("/productImages")
    public Object add(@RequestParam("pid") int pid, @RequestParam("type") String type, MultipartFile image, HttpServletRequest request) throws Exception {
        ProductImage bean = new ProductImage();
        Product product = productService.get(pid);
        bean.setProduct(product);
        bean.setType(type);
         
        productImageService.add(bean);
        String folder = "img/";
        if(ProductImageService.type_single.equals(bean.getType())){
            folder +="productSingle";
        }
        else{
            folder +="productDetail";
        }
        File  imageFolder= new File(request.getServletContext().getRealPath(folder));
        File file = new File(imageFolder,bean.getId()+".jpg");
        String fileName = file.getName();
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            image.transferTo(file);
            BufferedImage img = ImageUtil.change2jpg(file);
            ImageIO.write(img, "jpg", file);           
        } catch (IOException e) {
            e.printStackTrace();
        }
         
        if(ProductImageService.type_single.equals(bean.getType())){
            String imageFolder_small= request.getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle= request.getServletContext().getRealPath("img/productSingle_middle");    
            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            f_small.getParentFile().mkdirs();
            f_middle.getParentFile().mkdirs();
            ImageUtil.resizeImage(file, 56, 56, f_small);
            ImageUtil.resizeImage(file, 217, 190, f_middle);
        }      
         
        return bean;
    }
//     1. 点击删除超链
//2. vue 上的 deleteBean 函数被调用，访问 productImages/id 路径
//3. ProductImageController的delete方法被调用
//4. 根据id获取ProductImage 对象
//5. 借助productImageService，删除数据
//6. 如果是单个图片，那么删除3张正常，中等，小号图片
//7. 如果是详情图片，那么删除一张图片
    @DeleteMapping("/productImages/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request)  throws Exception {
        ProductImage bean = productImageService.get(id);
        productImageService.delete(id);
 
        String folder = "img/";
        if(ProductImageService.type_single.equals(bean.getType()))
            folder +="productSingle";
        else
            folder +="productDetail";
 
        File  imageFolder= new File(request.getServletContext().getRealPath(folder));
        File file = new File(imageFolder,bean.getId()+".jpg");
        String fileName = file.getName();
        file.delete();
        if(ProductImageService.type_single.equals(bean.getType())){
            String imageFolder_small= request.getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle= request.getServletContext().getRealPath("img/productSingle_middle");
            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            f_small.delete();
            f_middle.delete();
        }
 
        return null;
    }
     
}