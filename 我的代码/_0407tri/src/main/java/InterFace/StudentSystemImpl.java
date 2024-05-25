// StudentSystem
package InterFace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;//4.7加
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import java.util.List;//4.8加
import java.util.stream.Collectors;//4.8加

// Remote对象实现类，必须继承UnicastRemoteObject类，必须覆写接口中的全部抽象方法
// 在其中定义学生系统相关的数据结构和操纵方法
public class StudentSystemImpl extends UnicastRemoteObject implements StudentSystemInt {
    private StudentDao studentDao;
    public StudentSystemImpl() throws RemoteException {    // 必须定义构造方法处理RemoteException
        super();
        this.studentDao = new StudentDaoImpl(); // 初始化 StudentDao 实现 4.9
    }

    //4.8加
    private static final Logger logger = LoggerFactory.getLogger(StudentSystemImpl.class);

    private static final long serialVersionUID = 1L;    // 必须声明static final serialVersionUID field of type long
    //private ArrayList<Student> studentList = new ArrayList<Student>();    // 初始化学生列表为空，这里一定要用new ArrayList<Student>()，否则会报错空指针//4.7去
    private HashMap<String, Student> studentMap = new HashMap<>();//4.7加
    public boolean add(Student student) throws RemoteException{    // 增添一个学生对象，返回添加成功/失败标志

        //4.7注释
       /* for(Student b :studentList){    // 检查是否有同ID学生，若是则返回false
            if(b.studentID.equals(student.studentID)){
                return false;
            }
        }
        studentList.add(student);
        return true;*/
        try {
            //4.7加
            if (studentMap.containsKey(student.getStudentID())) {
                logger.warn("Attempted to add a student with existing ID: {}", student.getStudentID());
                return false; // 学生ID已存在
            } else {
                studentMap.put(student.getStudentID(), student);
                studentDao.saveStudent(student);
                logger.info("Student added successfully with ID: {}", student.getStudentID());
                return true;
            }
        }catch (Exception e) {
            logger.error("Error adding student: {}", e.getMessage());
            throw e;
        }
    }

    public Student queryByID(String StudentID) throws RemoteException{    // 查询指定ID的学生对象，返回该学生对象
       //4.8注释
        /*for(Student b :studentList){
            if(b.studentID.equals(StudentID)){
                return b;
            }
        }
        return null; */
        //4.8加
        //return studentMap.get(StudentID);
        try {
            Student student = studentMap.get(StudentID);
            if (student != null) {
                logger.info("Student queried successfully by ID from cache: {}", StudentID);
                return student;
            } else {
                // 在缓存未命中时尝试从数据库中查询
                student = studentDao.findStudentById(StudentID);
                if (student != null) {
                    // 将从数据库中检索到的学生添加到缓存中
                    studentMap.put(student.getStudentID(), student);
                    logger.info("Student queried successfully by ID from database: {}", StudentID);
                    return student;
                } else {
                    logger.warn("No student found with ID: {}", StudentID);
                    return null;
                }
            }
        } catch (Exception e) {
            logger.error("Error querying student by ID: {}", e.getMessage());
            throw e;
        }

    }



//下面原来是ArrayList改成了List
    public List<Student> queryByName(String name) throws RemoteException{    // 按姓名查询符合条件的学生对象，返回学生对象列表
        //4.8注释
        /*ArrayList<Student> resultList = new ArrayList<Student>();
        for(Student b :studentList){
            if(b.studentName.indexOf(name, 0) != -1){
                resultList.add(b);
            }
        }
        return resultList;*/
        //4.8加了又去
        /*ArrayList<Student> resultList = new ArrayList<Student>();
        for(Student student : studentMap.values()){ // 遍历HashMap的值集合
            if(student.getStudentName().indexOf(name) != -1){
                resultList.add(student);
            }
        }
        return resultList;*/
        //4.8又加
        // 使用parallelStream()来并行处理数据
        /*return studentMap.values().parallelStream()
                .filter(student -> student.getStudentName().contains(name)) // 过滤出符合条件的学生
                .collect(Collectors.toList()); // 收集结果到一个列表
*/
        try {
            List<Student> resultList = studentMap.values().parallelStream()
                    .filter(student -> student.getStudentName().contains(name))
                    .collect(Collectors.toList());

            if (resultList.isEmpty()) {
                // 如果缓存中没有找到，则尝试从数据库中查询
                resultList = studentDao.findStudentsByName(name);
                if (!resultList.isEmpty()) {
                    // 如果数据库中找到了，更新内存缓存
                    resultList.forEach(student -> studentMap.put(student.getStudentID(), student));
                    logger.info("Students queried successfully by name containing: {} from the database", name);
                } else {
                    logger.info("No student found with name containing: {} either in cache or database", name);
                }
                //logger.info("No student found with name containing: {}", name);
            } else {
                logger.info("Students queried successfully by name containing: {} from cache", name);
            }
            //return new ArrayList<>(resultList);
            return resultList;
        } catch (Exception e) {
            logger.error("Error querying student by name: {}", e.getMessage());
            //throw e;
            throw new RemoteException("Error querying student by name", e);
        }
    }

    public boolean delete(String StudentID) throws RemoteException{    // 删除指定ID的学生对象，返回删除成功/失败标志

        //4.8注释
        /*for(Student b :studentList){
            if(b.studentID.equals(StudentID)){
                studentList.remove(b);
                return true;
            }
        }
        return false;*/

        //4.8加
       /* if (studentMap.containsKey(StudentID)) {
            studentMap.remove(StudentID);
            return true;
        } else {
            return false;
        }*/

        try {
            if (studentMap.containsKey(StudentID)) {
                // 先从数据库中删除学生
                studentDao.deleteStudent(StudentID);
                // 如果数据库中删除成功，则从内存缓存中删除
                studentMap.remove(StudentID);
                logger.info("Student deleted successfully: {}", StudentID);
                return true;
            } else {
                // 在缓存中未找到学生，尝试从数据库中删除，可能是缓存未命中
                boolean isDeleted = studentDao.deleteStudent(StudentID);
                if (isDeleted) {
                    logger.info("Student deleted from database but was not in the cache: {}", StudentID);
                    return true;
                } else {
                    logger.warn("Attempted to delete a non-existing student with ID: {}", StudentID);
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error("Error deleting student: {}", e.getMessage());
            throw e;
        }

    }

    public boolean alter(String StudentID, String newName) throws RemoteException{    // 修改指定ID的学生对象名称，返回修改成功/失败标志

        //4.8注释
       /* for(Student b :studentList){
            if(b.studentID.equals(StudentID)){
                b.studentName = newName;
                return true;
            }
        }
        return false;*/
        //4.8加
        /*if (studentMap.containsKey(StudentID)) {
            Student stu = studentMap.get(StudentID);
            stu.setStudentName("newName"); // 假设你有setter方法或直接访问public属性
            return true;
        } else {
            return false;
        }*/

        try {
            if (studentMap.containsKey(StudentID)) {
                // 首先更新数据库中的学生名字
                boolean isUpdated = studentDao.updateStudentName(StudentID, newName);
                if (isUpdated) {
                    // 如果数据库更新成功，则同步更新内存中的缓存
                    Student student = studentMap.get(StudentID);
                    student.setStudentName(newName);
                    logger.info("Student name altered successfully for ID: {} to newName: {}", StudentID, newName);
                    return true;
                } else {
                    // 如果数据库中没有找到该ID的学生，或者更新失败
                    logger.warn("Failed to alter student name in the database for ID: {}", StudentID);
                    return false;
                }
            } else {
                // 在缓存中未找到学生ID
                logger.warn("Attempted to alter a non-existing student with ID: {}", StudentID);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error altering student name: {}", e.getMessage());
            throw e;
        }

    }

    public StringBuilder printStudentList() throws RemoteException{    // 打印所有学生对象列表，若暂无对象则打印none.

        //4.8注释
        /*StringBuilder str = new StringBuilder();
        StringBuilder none = new StringBuilder();
        if(studentList.size() == 0) {
            none.append("none.");
            return none;
        }
        else {
            for(Student b :studentList){
                str.append(b.StudentInfo());
                str.append('\n');
            }
            return str;
        }
    }*/
     //4.8加
        /*StringBuilder sb = new StringBuilder();
        for (Student stu : studentMap.values()) {
            sb.append(stu.StudentInfo()).append("\n");
        }
        return sb.length() == 0 ? new StringBuilder("none") : sb;
*/
        StringBuilder sb = new StringBuilder();
        try {
            // 从数据库获取所有学生记录
            List<Student> students = studentDao.findAllStudents();

            if (students.isEmpty()) {
                sb.append("none.");
            } else {
                // 遍历学生记录并构建结果字符串
                for (Student student : students) {
                    sb.append(student.StudentInfo()).append("\n");
                }
            }
        } catch (Exception e) {
            logger.error("Error printing student list: {}", e.getMessage());
            throw new RemoteException("Error printing student list", e);
        }

        return sb;

}
}