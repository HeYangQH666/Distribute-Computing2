package InterFace;
import java.util.List;

public interface StudentDao {
    void saveStudent(Student student);
    boolean deleteStudent(String studentId);
    boolean updateStudentName(String studentId, String newName);
    Student findStudentById(String studentId);
    List<Student> findAllStudents();
    List<Student> findStudentsByName(String name); // 添加这行代码
}
