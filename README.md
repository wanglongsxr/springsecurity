# springboot整合springsecurity 连载中...

## 项目介绍

springboot整合springsecurity 的练手项目     
项目管理工具为gradle

## 软件架构

* springboot
* springsecurity
* oauth2
* jwt
* springcloud
* gradle

## 使用说明

* **boot_security_demo**    
  springboot与springsecurity整合的入门程序，即表单登录，普通授权。内容包括用户认证功能，自定义鉴权成功页面，自定义鉴权失败页面，普通授权功能，会话管理功能等...

* **boot_security_remberme-code**    
  springboot与springsecurity整合的"记住我"功能和"验证码"功能，基于boot_security_demo的项目拓展。 
  此处的remember me功能是自定义数据库名.  
  验证码功能采用kaptcha插件

* **boot_security_permission**  
  springboot与springsecurity整合的动态权限认证功能，基于boot_security_demo的项目拓展。

* **boot_security_jwt**  
  springboot与springsecurity整合的jwt功能，基于boot_security_demo的项目拓展。

  如果项目本身是小型单体服务，可以考虑采用此技术做权限，但是带来的问题是很多的，token注销登录，token的续签问题，禁用用户等...处理的方式自己感觉远不如session的方式，至于jwt的具体优缺点，自己在博客有说明，请您权衡之下考虑。

  本项目实现的功能仅有登录认证，以及项目的token注销登录，token的续签。至于权限分配这里涉及到前端，本系统不做说明；

## 演示说明

**演示过程**

1. 登录前无法访问 /login-success

2. 登录访问 http://localhost:8080/login（注意：各个工程分别对应各个的端口号）

3. 账号密码 zhangsan/123    lisi/123

4. 校验权限时，url为：http://localhost:8080/r/r1;    http://localhost:8080/r/r2;

   zhangsan拥有p1和p2权限，李四拥有p1权限；

   p1权限对应r1资源，p2权限对应r2资源；

   无权限时会爆出403错误，forbidden

5. 一旦点“退出登录”，session失效，remrember me功能则失效

另：

1. 在测试jwt系统时，<strong style="color:red;">需要开启本地redis</strong>；

2. 测试请与postman结合使用；

   * 下图请求登录接口，后台生成token，并存储redis

     ![访问登录页](https://images.weserv.nl/?url=https://i0.hdslb.com/bfs/article/6be29754b64a9d73f8c8cd3160fa03236aa810eb.png)

   * 下图为访问首页接口，切记，带上token；

     ![post访问首页](https://images.weserv.nl/?url=https://i0.hdslb.com/bfs/article/0be0f0944b0bfc3013b39780e6288a78801a07c3.png)

## 解答与困惑

* CSDN博客：<a>https://blog.csdn.net/zz18435842675/category_9827319.html</a>    
  emmm...实在搞不懂的话，qq:2647351651    
  如果您觉得写的还不错或者对您有帮助，请不要吝啬你的小星星（star一下），它将是我的最大支持

