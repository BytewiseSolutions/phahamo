export interface Birthday {
  personId: number;
  name: string;
  type: 'STUDENT' | 'STAFF';
  className: string | null;
  date: string;
  today: boolean;
}
