package com.how2java.tmall.web;

import com.how2java.tmall.comparator.*;
import com.how2java.tmall.pojo.*;
import com.how2java.tmall.service.*;
//import com.how2java.tmall.service.ProductService;
//import com.how2java.tmall.service.UserService;
import com.how2java.tmall.util.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;
 
@RestController
public class ForeRESTController {
//    registerPage.html 的 axios.js 提交数据到路径 foreregister,导致ForeRESTController.register()方法被调用
//
//    1. 通过参数User获取浏览器提交的账号密码
//    2. 通过HtmlUtils.htmlEscape(name);把账号里的特殊符号进行转义
//    3. 判断用户名是否存在
//    3.1 如果已经存在，就返回Result.fail,并带上 错误信息
//    3.2 如果不存在，则加入到数据库中，并返回 Result.success()
//
//    注： 为什么要用 HtmlUtils.htmlEscape？ 因为有些同学在恶意注册的时候，
//    会使用诸如 <script>alert('papapa')</script> 这样的名称，会导致网页打开就弹出一个对话框。 那么在转义之后，就没有这个问题了。
//    注： 密码为什么没有加密？ User表还有个 salt字段，为什么没有使用。
//    咳咳。。。 是这样的，目前这里仅仅实现简单的用户注册功能，后续还在这个基础上改造成用 Shiro 来实现用户验证，加密等等。
//    注： Result 这个类，第一次使用，是在订单管理 发货功能讲解里用到的，当发货成功后，会返回 Result对象。 Result 对象是一种常见的
//    RESTFUL 风格返回的 json 格式，
//    里面可以有错误代码，错误信息和数据。 这样就比起以前那样，仅仅返回数据附加了更多的信息，方便前端人员识别和显示给用户可识别信息。
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderService orderService;

    @GetMapping("/forehome")
    public Object home() {
        List<Category> cs = categoryService.list();
        productService.fill(cs);
        productService.fillByRow(cs);
        categoryService.removeCategoryFromProduct(cs);
        return cs;
    }
    @PostMapping("/foreregister")
    public Object register(@RequestBody User user) {
        String name =  user.getName();
        String password = user.getPassword();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);

        if(exist){
            String message ="用户名已经被使用,不能使用";
            return Result.fail(message);
        }

        user.setPassword(password);

        userService.add(user);

        return Result.success();
    }
//    loginPage.html的 axios.js 提交数据到路径 forelogin,导致ForeRESTController.login()方法被调用
//
//    1. 账号密码注入到 userParam 对象上
//    2. 把账号通过HtmlUtils.htmlEscape进行转义
//    3. 根据账号和密码获取User对象
//    3.1 如果对象为空，则返回错误信息
//    3.2 如果对象存在，则把用户对象放在 session里，并且返回成功信息
//    注 为什么要用 HtmlUtils.htmlEscape？ 因为注册的时候，ForeRESTController.register()，就进行了转义，所以这里也需要转义。
//    有些同学在恶意注册的时候，会使用诸如 <script>alert('papapa')</script> 这样的名称，会导致网页打开就弹出一个对话框。
//    那么在转义之后，就没有这个问题了。
    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name =  userParam.getName();
        name = HtmlUtils.htmlEscape(name);

        User user =userService.get(name,userParam.getPassword());
        if(null==user){
            String message ="账号密码错误";
            return Result.fail(message);
        }
        else{
            session.setAttribute("user", user);
            return Result.success();
        }
    }
//    导致ForeRESTController.product() 方法被调用
//    1. 获取参数pid
//    2. 根据pid获取Product 对象product
//    3. 根据对象product，获取这个产品对应的单个图片集合
//    4. 根据对象product，获取这个产品对应的详情图片集合
//    5. 获取产品的所有属性值
//    6. 获取产品对应的所有的评价
//    7. 设置产品的销量和评价数量
//    8. 把上述取值放在 map 中
//    9. 通过 Result 把这个 map 返回到浏览器去
//
//    为什么要用Map呢？ 因为返回出去的数据是多个集合，而非一个集合，所以通过 map返回给浏览器，浏览器更容易识别
    @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable("pid") int pid) {
        Product product = productService.get(pid);

        List<ProductImage> productSingleImages = productImageService.listSingleProductImages(product);
        List<ProductImage> productDetailImages = productImageService.listDetailProductImages(product);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);

        List<PropertyValue> pvs = propertyValueService.list(product);
        List<Review> reviews = reviewService.list(product);
        productService.setSaleAndReviewNumber(product);
        productImageService.setFirstProductImage(product);

        Map<String,Object> map= new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", reviews);

        return Result.success(map);
    }
//    在上一步的ajax访问路径/forecheckLogin会导致ForeRESTController.checkLogin()方法被调用。
//    获取session中的"user"对象
//    如果不为空，即表示已经登录，返回 Result.success()
//    如果为空，即表示未登录，返回 Result.fail("未登录");
    @GetMapping("forecheckLogin")
    public Object checkLogin( HttpSession session) {
        User user =(User)  session.getAttribute("user");
        if(null!=user)
            return Result.success();
        return Result.fail("未登录");
    }
//    1. 获取参数cid
//    2. 根据cid获取分类Category对象 c
//    3. 为c填充产品
//    4. 为产品填充销量和评价数据
//    5. 获取参数sort
//    5.1 如果sort==null，即不排序
//    5.2 如果sort!=null，则根据sort的值，从5个Comparator比较器中选择一个对应的排序器进行排序
//    6. 返回对象 c
    @GetMapping("forecategory/{cid}")
    public Object category(@PathVariable int cid,String sort) {
        Category c = categoryService.get(cid);
        productService.fill(c);
        productService.setSaleAndReviewNumber(c.getProducts());
        categoryService.removeCategoryFromProduct(c);

        if(null!=sort){
            switch(sort){
                case "review":
                    Collections.sort(c.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(c.getProducts(),new ProductDateComparator());
                    break;

                case "saleCount" :
                    Collections.sort(c.getProducts(),new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(c.getProducts(),new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(c.getProducts(),new ProductAllComparator());
                    break;
            }
        }

        return c;
    }
//    searchPage.html 中的请求提交后，导致ForeRESTController.search()方法被调用
//1. 获取参数keyword
//2. 根据keyword进行模糊查询，获取满足条件的前20个产品
//3. 为这些产品设置销量和评价数量
//4. 返回这个产品集合
    @PostMapping("foresearch")
    public Object search( String keyword){
        if(null==keyword)
            keyword = "";
        List<Product> ps= productService.search(keyword,0,20);
        productImageService.setFirstProductImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }
    @GetMapping("forebuyone")
    public Object buyone(int pid, int num, HttpSession session) {
        return buyoneAndAddCart(pid,num,session);
    }
//    通过在产品页点击立即购买步骤访问的地址 /forebuyone 导致ForeRESTController.buyone()方法被调用，然后调用 buyoneAndAddCart 方法。
//
//    1. 获取参数pid
//    2. 获取参数num
//    3. 根据pid获取产品对象p
//    4. 从session中获取用户对象user
//
//    接下来就是新增订单项OrderItem， 新增订单项要考虑两个情况
//    a. 如果已经存在这个产品对应的OrderItem，并且还没有生成订单，即还在购物车中。 那么就应该在对应的OrderItem基础上，调整数量
//    a.1 基于用户对象user，查询没有生成订单的订单项集合
//    a.2 遍历这个集合
//    a.3 如果产品是一样的话，就进行数量追加
//    a.4 获取这个订单项的 id
//
//    b. 如果不存在对应的OrderItem,那么就新增一个订单项OrderItem
//    b.1 生成新的订单项
//    b.2 设置数量，用户和产品
//    b.3 插入到数据库
//    b.4 获取这个订单项的 id
//
//    5.返回当前订单项id
//    6. 在页面上，拿到这个订单项id，就跳转到 location.href="buy?oiid="+oiid;
//    buy 是结算页面，现在还没有做，在下一个知识点就会做了。
//
//    因为增加到购物车的逻辑和这个是一样的，所以都重构到 buyoneAndAddCart 方法里了，方便后续增加购物车行为。
    private int buyoneAndAddCart(int pid, int num, HttpSession session) {
        Product product = productService.get(pid);
        int oiid = 0;

        User user =(User)  session.getAttribute("user");
        boolean found = false;
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            if(oi.getProduct().getId()==product.getId()){
                oi.setNumber(oi.getNumber()+num);
                orderItemService.update(oi);
                found = true;
                oiid = oi.getId();
                break;
            }
        }

        if(!found){
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setProduct(product);
            oi.setNumber(num);
            orderItemService.add(oi);
            oiid = oi.getId();
        }
        return oiid;
    }
//    在 buyPage.html中，访问路径： "forebuy?oiid="+oiid;
//
//    http://127.0.0.1:8080/tmall_springboot/forebuy?oiid=1
//
//
//            导致ForeRESTController.buy()方法被调用
//   1. 通过字符串数组获取参数oiid
//    为什么这里要用字符串数组试图获取多个oiid，而不是int类型仅仅获取一个oiid? 因为根据购物流程环节与表关系，结算页面还需要显示在购物车中选中的多条OrderItem数据，所以为了兼容从购物车页面跳转过来的需求，要用字符串数组获取多个oiid
//   2. 准备一个泛型是OrderItem的集合ois
//   3. 根据前面步骤获取的oiids，从数据库中取出OrderItem对象，并放入ois集合中
//   4. 累计这些ois的价格总数，赋值在total上
//   5. 把订单项集合放在session的属性 "ois" 上
//   6. 把订单集合和total 放在map里
//   7. 通过 Result.success 返回
    @GetMapping("forebuy")
    public Object buy(String[] oiid,HttpSession session){
        List<OrderItem> orderItems = new ArrayList<>();
        float total = 0;

        for (String strid : oiid) {
            int id = Integer.parseInt(strid);
            OrderItem oi= orderItemService.get(id);
            total +=oi.getProduct().getPromotePrice()*oi.getNumber();
            orderItems.add(oi);
        }

        productImageService.setFirstProductImagesOnOrderItems(orderItems);

        session.setAttribute("ois", orderItems);

        Map<String,Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
        return Result.success(map);
    }
//    上一步访问地址/foreaddCart导致ForeRESTController.addCart()方法被调用
//    addCart()方法和立即购买中的 ForeRESTController.buyone()步骤做的事情是一样的，因为都是调用 buyoneAndAddCart 方法。
    @GetMapping("foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid,num,session);
        return Result.success();
    }
//    访问地址/forecart导致ForeRESTController.cart()方法被调用
//  1. 通过session获取当前用户
//    所以一定要登录才访问，否则拿不到用户对象,会报错
//  2. 获取为这个用户关联的订单项集合 ois
//  3. 设置图片
//  4. 返回这个订单项集合
    @GetMapping("forecart")
    public Object cart(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user);
        productImageService.setFirstProductImagesOnOrderItems(ois);
        return ois;
    }
    @GetMapping("forechangeOrderItem")
    public Object changeOrderItem( HttpSession session, int pid, int num) {
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");

        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            if(oi.getProduct().getId()==pid){
                oi.setNumber(num);
                orderItemService.update(oi);
                break;
            }
        }
        return Result.success();
    }
    @GetMapping("foredeleteOrderItem")
    public Object deleteOrderItem(HttpSession session,int oiid){
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        orderItemService.delete(oiid);
        return Result.success();
    }
//    提交订单访问路径 /forecreateOrder, 导致ForeRESTController.createOrder 方法被调用
//      1. 从session中获取user对象
//      2. 根据当前时间加上一个4位随机数生成订单号
//      3. 根据上述参数，创建订单对象
//      4. 把订单状态设置为等待支付
//      5. 从session中获取订单项集合 ( 在结算功能的ForeRESTController.buy() ，订单项集合被放到了session中 )
//            7. 把订单加入到数据库，并且遍历订单项集合，设置每个订单项的order，更新到数据库
//      8. 统计本次订单的总金额
//      9. 返回总金额
    @PostMapping("forecreateOrder")
    public Object createOrder(@RequestBody Order order,HttpSession session){
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(OrderService.waitPay);
        List<OrderItem> ois= (List<OrderItem>)  session.getAttribute("ois");

        float total =orderService.add(order,ois);

        Map<String,Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);

        return Result.success(map);
    }
//    1. 在上一步确认访问按钮提交数据到/payed, 返回payed.html
//    与 register.html 相仿，payed.html也包含了header.html, top.html, simpleSearch.html， footer.html 等公共页面。
//    中间是支付成功业务页面 payedPage.html
//    2. payedPage.html
//    显示订单中的地址，邮编，收货人，手机号码等等
//            并且提交forepayed地址
//    3. forepayed地址导致 payed方法被调用：
//            1.1 获取参数oid
//            1.2 根据oid获取到订单对象order
//            1.3 修改订单对象的状态和支付时间
//            1.4 更新这个订单对象到数据库
//            1.5 返回订单
    @GetMapping("forepayed")
    public Object payed(int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }
    @GetMapping("forebought")
    public Object bought(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        List<Order> os= orderService.listByUserWithoutDelete(user);
        orderService.removeOrderFromOrderItem(os);
        return os;
    }
    @GetMapping("foreconfirmPay")
    public Object confirmPay(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.cacl(o);
        orderService.removeOrderFromOrderItem(o);
        return o;
    }
    @GetMapping("foreorderConfirmed")
    public Object orderConfirmed( int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.waitReview);
        o.setConfirmDate(new Date());
        orderService.update(o);
        return Result.success();
    }
    @PutMapping("foredeleteOrder")
    public Object deleteOrder(int oid){
        Order o = orderService.get(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return Result.success();
    }
//    在评价产品页面点击提交评价，就把数据提交到了/foredoreview路径，导致ForeRESTController.doreview方法被调用
//1. ForeRESTController.doreview()
//        1.1 获取参数oid
//1.2 根据oid获取订单对象o
//1.3 修改订单对象状态
//1.4 更新订单对象到数据库
//1.5 获取参数pid
//1.6 根据pid获取产品对象
//1.7 获取参数content (评价信息)
//1.8 对评价信息进行转义，道理同注册ForeRESTController.register()
//            1.9 从session中获取当前用户
//1.10 创建评价对象review
//1.11 为评价对象review设置 评价信息，产品，时间，用户
//1.12 增加到数据库
//1.13.返回成功
//2. reviewPage.html
//    提交评价后， 通过调用 vue.showReviews = true; 把产品列表显示出来（默认是隐藏的）
    @GetMapping("forereview")
    public Object review(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.removeOrderFromOrderItem(o);
        Product p = o.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(p);
        productService.setSaleAndReviewNumber(p);
        Map<String,Object> map = new HashMap<>();
        map.put("p", p);
        map.put("o", o);
        map.put("reviews", reviews);

        return Result.success(map);
    }
}