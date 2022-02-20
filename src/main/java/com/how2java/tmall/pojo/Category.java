package com.how2java.tmall.pojo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
 
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//实体类
@Entity
//对应表名category
@Table(name = "category")
//jpa默认hibernate忽略
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
 
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")   
    int id;
     
    String name;
//    Category新增两个瞬时字段products和productsByRow。
//    products比较好理解，代表一个分类下有多个产品。
//    productsByRow这个属性的类型是List<List<Product>> productsByRow。
//    即一个分类又对应多个 List<Product>，提供这个属性，是为了在首页竖状导航的分类名称右边显示推荐产品列表
    @Transient
    List<Product> products;
    @Transient
    List<List<Product>> productsByRow;
     
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
     
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Product> getProducts() {
        return products;
    }
    public void setProducts(List<Product> products) {
        this.products = products;
    }
    public List<List<Product>> getProductsByRow() {
        return productsByRow;
    }
    public void setProductsByRow(List<List<Product>> productsByRow) {
        this.productsByRow = productsByRow;
    }
    @Override
    public String toString() {
        return "Category [id=" + id + ", name=" + name + "]";
    }
}