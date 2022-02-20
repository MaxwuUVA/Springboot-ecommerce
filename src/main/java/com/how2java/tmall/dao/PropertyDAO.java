package com.how2java.tmall.dao;
  
import java.util.List;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Property;
 
public interface PropertyDAO extends JpaRepository<Property,Integer>{
//    PropertyDAO 继承了 JpaRepository。提供常见的CRUD。
//    除此之外，还提供了根据分类进行查询:
    Page<Property> findByCategory(Category category, Pageable pageable);
//    修改PropertyDAO ，增加通过分类获取所有属性集合的方法
    List<Property> findByCategory(Category category);
}