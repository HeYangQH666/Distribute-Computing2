// MyRMIServer
package Server;
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import InterFace.*;

//Skeleton是必须的，它作为服务器端的一个对象，负责接收来自客户端的调用，解包请求，调用服务器上的远程对象方法，然后将结果打包发回客户端

// 服务器类
public class RMIServer {
    // RMI服务器IP地址
    public static final String RMI_HOST = "127.0.0.1";
    // RMI服务端口
    public static final int RMI_PORT = 9527;
    // RMI服务名称
    public static final String RMI_NAME = "rmi://" + RMI_HOST + ":" + RMI_PORT + "/StudentInfoManageSystem";
    // main方法：注册实现类到注册中心
    public static void main(String[] args) throws Exception {
        try {
            LocateRegistry.createRegistry(RMI_PORT);  // 创建注册中心监听来自端口9527的请求
            Naming.rebind(RMI_NAME, new StudentSystemImpl());  // 将rmi服务名绑定至远程对象,rebind(name, obj)：将一个名字和一个远程对象绑定在一起，如果该名字已经被绑定，则现有绑定被替换。
            System.out.println("RMI服务启动成功，服务地址为" + RMI_NAME);
        } /*catch (Exception e) {
            e.printStackTrace();
        }*///4.8修改
        catch (java.rmi.server.ExportException e) {
            System.err.println("端口已被占用，服务启动失败。请检查端口" + RMI_PORT + "是否被其他应用使用。");
        } catch (Exception e) {
            System.err.println("服务启动时发生未知错误：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
