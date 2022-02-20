package com.how2java.tmall.dao;
  
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.how2java.tmall.pojo.User;
 
public interface UserDAO extends JpaRepository<User,Integer>{
//    UserDAO 新增加findByName(String name)方法
     User findByName(String name);
//    增加get(String name, String password)方法
     User getByNameAndPassword(String name, String password);
}