package kr.co.teacherforboss.domain.vo.mailVO;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.TeacherInfo;

import java.time.format.DateTimeFormatter;

public class TeacherAuditMail extends Mail {

    public TeacherAuditMail(Member member, TeacherInfo teacherInfo) {
        super("teacher-audit", "[티쳐 포 보스] 티처 심사 요청");
        super.values.put("phone", getPhone(member));
        super.values.put("email", getEmail(member));
        super.values.put("businessNumber", getBusinessNumber(teacherInfo));
        super.values.put("representative", getRepresentative(teacherInfo));
        super.values.put("openDate", getOpenDate(teacherInfo));
        super.values.put("field", getField(teacherInfo));
        super.values.put("career", getCareer(teacherInfo));
    }

    private String getPhone(Member member) { return member.getPhone(); }
    private String getEmail(Member member) {
        return member.getEmail();
    }
    private String getBusinessNumber(TeacherInfo teacherInfo) {
        return teacherInfo.getBusinessNumber();
    }
    private String getRepresentative(TeacherInfo teacherInfo) {
        return teacherInfo.getRepresentative();
    }
    private String getOpenDate(TeacherInfo teacherInfo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (teacherInfo.getOpenDate() != null)
            return teacherInfo.getOpenDate().format(formatter);
        else return "";
    }
    private String getField(TeacherInfo teacherInfo) {
         return teacherInfo.getField();
    }
    private String getCareer(TeacherInfo teacherInfo) {
        return teacherInfo.getCareer().toString();
    }
}
