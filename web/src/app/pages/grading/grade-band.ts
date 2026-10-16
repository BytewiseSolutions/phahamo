export interface GradeBand {
  id?: number;
  minScore: number;
  maxScore: number;
  grade: string;
  remark: string;
}

export interface GradeResolveResult {
  score: number;
  grade: string;
  remark: string;
}
