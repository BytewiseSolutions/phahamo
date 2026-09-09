import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AcademicYear } from '../academic-year';

@Component({
  selector: 'app-academic-year-view',
  standalone: false,
  templateUrl: './academic-year-view.html',
  styleUrl: './academic-year-view.scss'
})
export class AcademicYearView {

  @Input() academicYear: AcademicYear | null = null;

  @Output() back = new EventEmitter<void>();

  onBack(): void {
    this.back.emit();
  }
}
