import {ParentRelationship} from './parent-relationship';

export interface Parent {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
}

export interface ParentPayload {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  relationship?: ParentRelationship;
}
