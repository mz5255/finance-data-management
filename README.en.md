# finance-data-management

#### Description
财务数据管理

#### Software Architecture
Software architecture description

#### Installation
port  8888
启动类只有一个在 application 层
# 每个模块的思想
1、finance-application   对外展示层 只做业务的拼接 而不做业务的实现
2、finance-common 工具类
3、finance-data-source  数据源
4、finance-module 业务代码的实现
    4.1 module-auth 权限管理
    4.2 module-user 用户管理
    4.3 module-system 系统性能展示
    4.4 module- finance 财务模块管理
5、finance-starter 自定义 starter 的实现

#### Instructions

1.  xxxx
2.  xxxx
3.  xxxx

#### Contribution

1.  Fork the repository
2.  Create Feat_xxx branch
3.  Commit your code
4.  Create Pull Request


#### Gitee Feature

1.  You can use Readme\_XXX.md to support different languages, such as Readme\_en.md, Readme\_zh.md
2.  Gitee blog [blog.gitee.com](https://blog.gitee.com)
3.  Explore open source project [https://gitee.com/explore](https://gitee.com/explore)
4.  The most valuable open source project [GVP](https://gitee.com/gvp)
5.  The manual of Gitee [https://gitee.com/help](https://gitee.com/help)
6.  The most popular members  [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
