import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { SubjectClassAssignment } from '../../subject-class-assignment/subject-class-assignment';

@Component({
  selector: 'app-my-classes-page',
  standalone: false,
  templateUrl: './my-classes-page.html',
  styleUrl: './my-classes-page.scss'
})
export class MyClassesPage implements OnInit {

  assignments: SubjectClassAssignment[] = [];
  loading = false;
  errorMessage = '';

  constructor(private backend: BackendService, private router: Router) { }

  ngOnInit(): void {
    this.loadAssignments();
  }

  loadAssignments(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<SubjectClassAssignment[]>('/subject-class-assignments/my-assignments').subscribe({
      next: (assignments) => {
        this.assignments = assignments;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load your classes.';
        this.loading = false;
      }
    });
  }

  onOpenClass(assignment: SubjectClassAssignment): void {
    this.router.navigate(['/my-classes', assignment.id]);
  }
}