package com.how2java.tmall.dao;
  
import java.util.List;
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.pojo.PropertyValue;
 
public interface PropertyValueDAO extends JpaRepository<PropertyValue,Integer>{
//    DAO类，除了继承JpaRepository 还提供根据产品查询：
    List<PropertyValue> findByProductOrderByIdDesc(Product product);
//    和根据产品和属性获取PropertyValue对象
    PropertyValue getByPropertyAndProduct(Property property, Product product);
 
}