export interface Remark {
  studentId: number;
  studentFirstName: string;
  studentLastName: string;
  admissionNumber: string;
  classTeacherRemark: string | null;
  classTeacherRemarkByName: string | null;
  classTeacherRemarkAt: string | null;
  principalRemark: string | null;
  principalRemarkByName: string | null;
  principalRemarkAt: string | null;
  locked: boolean;
  canEditClassTeacherRemark: boolean;
}
