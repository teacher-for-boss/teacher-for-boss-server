package kr.co.teacherforboss.domain.vo.mailVO;

import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.util.AES256Util;

public class ExchangeMail extends Mail {
    public ExchangeMail(int ownedTP, int appliedTP, TeacherInfo teacherInfo) {
        super("exchange", "[티쳐 포 보스] 환전 신청");
        super.values.put("email", getEmail(teacherInfo));
        super.values.put("ownedTP", getOwnedTP(ownedTP));
        super.values.put("appliedTP", getAppliedTP(appliedTP));
        super.values.put("remainingTP", getRemainingTP(ownedTP, appliedTP));
        super.values.put("charge", getCharge(appliedTP));
        super.values.put("finalMoney", getFinalMoney(appliedTP));
        super.values.put("accountNumber", getAccountNumber(teacherInfo));
        super.values.put("bank", getBank(teacherInfo));
        super.values.put("accountHolder", getAccountHolder(teacherInfo));
    }

    private String getEmail(TeacherInfo teacherInfo) {
        return teacherInfo.getMember().getEmail();
    }
    private String getOwnedTP(int ownedTP) {
        return String.valueOf(ownedTP);
    }

    private String getAppliedTP(int appliedTP) {
        return String.valueOf(appliedTP);
    }

    private String getRemainingTP(int ownedTP, int appliedTP) {
        return String.valueOf(ownedTP - appliedTP);
    }

    private String getCharge(int appliedTP) {
        return String.valueOf(appliedTP * 100 * 0.1);
    }

    private String getFinalMoney(int appliedTP) {
         return String.valueOf(appliedTP * 100 * 0.9);
    }

    private String getAccountNumber(TeacherInfo teacherInfo) {
        return AES256Util.decrypt(teacherInfo.getAccountNumber());
    }

    private String getBank(TeacherInfo teacherInfo) {
        return teacherInfo.getBank();
    }

    private String getAccountHolder(TeacherInfo teacherInfo) {
        return teacherInfo.getAccountHolder();
    }
}
