import { School } from './school';

export interface SchoolRegistration {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phone: string;
  school: Pick<School, 'schoolName' | 'schoolAddress' | 'schoolContactEmail' | 'schoolContactPhone'>;
}
