package InterFace;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.DriverManager;
public class StudentDaoImpl implements StudentDao {

    private String url = "jdbc:mysql://127.0.0.1:3306/student_management_db?useSSL=false&serverTimezone=UTC";
    private String username = "root";
    private String password = "root";

    @Override
    public void saveStudent(Student student) {
        String sql = "INSERT INTO students (student_id, name) VALUES (?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, student.getStudentID());
            pstmt.setString(2, student.getStudentName());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Student with ID: " + student.getStudentID() + " was added successfully.");
            } else {
                System.out.println("No rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Student findStudentById(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String studentName = rs.getString("name");
                    return new Student(studentId, studentName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 如果没有找到学生，返回 null
    }
    @Override
    public List<Student> findAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String studentName = rs.getString("name");
                students.add(new Student(studentId, studentName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students; // 返回学生列表
    }

    @Override
    public boolean deleteStudent(String studentId) {
        // 实现从数据库删除学生的逻辑

        boolean isDeleted = false;
        String sql = "DELETE FROM students WHERE student_id = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            int affectedRows = pstmt.executeUpdate();
            isDeleted = affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isDeleted;
    }



    @Override
    /*public void updateStudentName(String studentId, String newName) {
        String sql = "UPDATE students SET name = ? WHERE student_id = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, studentId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Student name updated successfully.");
            } else {
                System.out.println("Student not found with ID: " + studentId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
    public boolean updateStudentName(String studentId, String newName) {
        String sql = "UPDATE students SET name = ? WHERE student_id = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, studentId);
            int affectedRows = pstmt.executeUpdate();
            // 如果影响的行数大于0，表示更新操作成功
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            // 发生异常时返回 false
            return false;
        }
    }
    @Override
    public List<Student> findStudentsByName(String name) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE name LIKE ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String studentId = rs.getString("student_id");
                    String studentName = rs.getString("name");
                    students.add(new Student(studentId, studentName));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

}

