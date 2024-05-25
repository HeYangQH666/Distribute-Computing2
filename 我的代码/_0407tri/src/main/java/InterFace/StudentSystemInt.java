// StudentSystemInfo
package InterFace;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List; // 确保导入了List

// 接口定义类，定义所有Remote方法
// 接口类必须继承Remote类
// 所有方法需要声明java.rmi.RemoteException异常
public interface StudentSystemInt extends Remote{
    boolean add(Student b) throws RemoteException;    // 增添一个学生对象，返回添加成功/失败标志
    Student queryByID(String studentID) throws RemoteException;    // 查询指定ID的学生对象，返回该学生对象
    List<Student> queryByName(String name) throws RemoteException;    // 按姓名查询符合条件的学生对象，返回学生对象列表 4.8:改了ArrayList
    boolean delete(String StudentID) throws RemoteException;    // 删除指定ID的学生对象，返回删除成功/失败标志
    boolean alter(String StudentID, String newName) throws RemoteException;    // 删除指定ID的学生对象，返回删除成功/失败标志
    StringBuilder printStudentList() throws RemoteException;    // 打印所有学生对象列表，在服务器端执行
}