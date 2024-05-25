//  Student
package InterFace;

import java.io.Serializable;

// 学生类：定义书对象的属性及方法,必须定义为公共类
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    // 学生类属性

    //4.8注释
   /* public String studentID;
    public String studentName;*/

    // 将学生类属性设为私有 4.8加
    private String studentID;
    private String studentName;


    // 构造方法
    public Student(String id, String name){
        studentID = id;
        studentName = name;
    }
    // StudentInfo方法：返回学生信息
    public String StudentInfo(){
        return("学生ID: " + studentID + " 姓名: " + studentName);
    }


    //4.8加

    // Getter方法用于外部访问私有属性
    public String getStudentID() {
        return this.studentID;
    }

    public String getStudentName() {
        return this.studentName;
    }

    // Setter方法用于外部修改私有属性的值
    public void setStudentID(String studentID) {
        if (studentID == null || studentID.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty.");
        }
        this.studentID = studentID;
    }

    public void setStudentName(String studentName) {
        if (studentName == null || studentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Student Name cannot be null or empty.");
        }
        this.studentName = studentName;
    }

}