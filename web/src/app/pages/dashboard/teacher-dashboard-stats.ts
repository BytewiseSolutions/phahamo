export interface TeacherDashboardStats {
  totalClasses: number;
  todayPeriods: number;
  pendingAttendance: number;
  missingMarks: number;
  todayLessons: import('../timetable/lesson').Lesson[];
  assessmentsMissingMarks: import('./teacher-assessment-task').TeacherAssessmentTask[];
}
