// MyRMIClient
package Client;
import InterFace.*;
import java.rmi.Naming;//这个类提供了在RMI注册表中查找和绑定远程对象的方法。
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List; // 确保导入了List
// 客户端类，调用Remote对象实现和学生系统的交互，注意只能通过调用远程方法得到其返回的结果。若直接在实现类中使用print，则会打印到服务器端
public class RMIClient {

	public static void ShowFunc(){
		System.out.println("------------学生信息管理系统------------\n功能选项如下：");
		System.out.println("1: 添加学生对象");
		System.out.println("2: 查询指定ID号的学生对象");
		System.out.println("3: 按姓名查询符合条件的学生对象列表（支持模糊匹配）");
		System.out.println("4: 删除指定ID号的学生对象");
		System.out.println("5: 修改指定ID号的学生名称");
		System.out.println("6: 打印学生列表");
		System.out.println("7: 退出系统");
		System.out.println("请输入1到7之间的序号,并且按照提示操作");
		System.out.println("--------------------------------------");
	}

	// main方法：根据序号选择相应的操作，远程调用服务器端提供的方法
	public static void main(String args[]) {
		try {
			String RMI_NAME = "rmi://127.0.0.1:9527/StudentInfoManageSystem";
			StudentSystemInt MyInt = (StudentSystemInt) Naming.lookup(RMI_NAME);
			//当客户端通过lookup方法请求一个远程对象时，RMI注册表会查找与指定服务名绑定的远程对象。如果找到了，远程对象（或其存根）被序列化并通过网络传输给客户端
			System.out.println("查询注册中心成功!");
			// 应使用try块包裹input对象，否则会出现leak警告
			try (Scanner input = new Scanner(System.in)) {
				int pick = 7;
				ShowFunc();
				while ((pick = input.nextInt()) !=7){
					switch (pick){
						case 1:{
							while(true) {
								System.out.println("（添加学生）请输入学生ID和姓名:");
								String studentID = input.next();
								String studentName = input.next();
								Student student = new Student(studentID, studentName);
								boolean flag = MyInt.add(student);
								if(flag == true){
									System.out.println("添加成功！是否继续添加？(y/n)");
									String choice = input.next();
									if(choice.equals("y")) {
										continue;
									}
									else {
										break;
									}
								}
								else{
									System.out.println("添加失败，已存在同ID学生，是否继续添加？(y/n)");
									String choice = input.next();
									if(choice.equals("y")) {
										continue;
									}
									else {
										break;
									}
								}
							}
						}
						break;

						case 2:{
							System.out.println("（查找学生）请输入学生ID:");
							String studentID = input.next();
							Student student = MyInt.queryByID(studentID);
							if(student == null){
								System.out.println("未找到ID为" + String.valueOf(studentID) + "的学生");
							}
							else{
								System.out.println("查找成功! 学生信息如下:");
								System.out.println(student.StudentInfo());
							}
						}
						break;

						case 3:{
							System.out.println("（查找学生）请输入姓名或姓名关键字:");
							String studentName = input.next();
							List<Student> resultList = MyInt.queryByName(studentName);//这里4.8把ArrayList 改为了List
							if(resultList.size() == 0){
								System.out.println("未找到姓名为" + studentName + "或姓名包含" + studentName+ "的学生");
							}
							else {
								System.out.println("查找成功! 学生信息如下:");
								for (Student b : resultList) {
									System.out.println(b.StudentInfo());
								}
							}
						}
						break;

						case 4:{
							while(true) {
								System.out.println("（删除学生）请输入学生ID:");
								String studentID = input.next();
								boolean flag = MyInt.delete(studentID);
								if(flag == true){
									System.out.println("删除成功！是否继续删除？(y/n)");
									String choice = input.next();
									if(choice.equals("y")) {
										continue;
									}
									else {
										break;
									}
								}
								else{
									System.out.println("删除失败，不存在ID为" + String.valueOf(studentID) + "的学生，是否继续删除？(y/n)");
									String choice = input.next();
									if(choice.equals("y")) {
										continue;
									}
									else {
										break;
									}
								}
							}
						}
						break;

						case 5:{
							while(true) {
								System.out.println("（修改姓名）请输入学生ID:");
								String studentID = input.next();
								System.out.println("（修改姓名）请输入新的姓名:");
								String newName = input.next();
								boolean flag = MyInt.alter(studentID, newName);
								if(flag == true){
									System.out.println("修改成功！是否继续修改？(y/n)");
									String choice = input.next();
									if(choice.equals("y")) {
										continue;
									}
									else {
										break;
									}
								}
								else{
									System.out.println("修改失败，不存在ID为" + String.valueOf(studentID) + "的学生，是否继续修改？(y/n)");
									String choice = input.next();
									if(choice.equals("y")) {
										continue;
									}
									else {
										break;
									}
								}
							}
						}
						break;

						case 6:{
							System.out.println("学生列表如下:");
							System.out.println(MyInt.printStudentList());
						}
						break;
					}
					System.out.println("\n请输入功能序号[1]―[7]:");
				}
				// 若输入7，则退出
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} /*catch (Exception e) {
			e.printStackTrace();
		}*/ //4.8注释+添加
		catch (java.rmi.NotBoundException e) {
			System.err.println("服务未绑定，请检查服务端是否已启动，以及服务名是否正确。");
		} catch (java.rmi.ConnectException e) {
			System.err.println("无法连接到服务端，请检查服务端IP地址和端口配置，确保网络通畅。");
		} catch (Exception e) {
			System.err.println("发生未知错误：" + e.getMessage());
			e.printStackTrace();
		}
	}
}