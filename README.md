# remote-control
Android端控制PC
功能: 远程鼠标的控制，自定义按键映射, 普通文本，dos消息的发送，远程电源控制，远程文件下载与本地文件上传...
## 启动步骤
```
1.运行RemoteServer里面的jar文件
2.手机端输入对应PC的IP地址和端口号
tips: 两者必须在一个局域网内
```
## 基本实现简述
初始建立连接时，使用的是TCP协议来进行的，里面其它所有的事件，消息收发都是使用的UDP协议，因为这些数据丢包是可以容忍的。另外,文件上传与下载是使用的TCP协议, 普通消息一次包就可发送过去，文件比较大时必须分片的发送，并且文件发送丢包的话，已经接收的数据与后续需要传输的数据就都没太大意义了,TCP能保证数据包的可达. 消息的发送，连接的建立等都是交给线程池去进行操作的, 而消息的接收单独使用了一个线程. 
## 参考
https://github.com/varunon9/Remote-Control-PC.git
