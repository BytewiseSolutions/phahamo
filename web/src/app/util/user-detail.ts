import { User } from './user';
import {Student} from '../pages/student/student';
import {Teacher} from '../pages/teacher/teacher';

export interface UserDetail extends User {
  student?: Student | null;
  teacher?: Teacher | null;
}
