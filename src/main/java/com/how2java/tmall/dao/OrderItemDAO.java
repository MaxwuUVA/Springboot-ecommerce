package com.how2java.tmall.dao;
  
import java.util.List;
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.User;
 
public interface OrderItemDAO extends JpaRepository<OrderItem,Integer>{
    List<OrderItem> findByOrderOrderByIdDesc(Order order);
//    修改OrderItemService，增加根据产品获取OrderItem的方法：
    List<OrderItem> findByProduct(Product product);
//    为OrderItemDAO新增方法findByUserAndOrderIsNull
//    如果未登录，那么点击立即购买之前会弹出模态登录窗口，关于这个功能的详细介绍在模态登录，在此不做赘述。
//    登录之后，点击立即购买，会访问地址
    List<OrderItem> findByUserAndOrderIsNull(User user);
}