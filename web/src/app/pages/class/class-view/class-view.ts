import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SchoolClass } from '../class';

@Component({
  selector: 'app-class-view',
  standalone: false,
  templateUrl: './class-view.html',
  styleUrl: './class-view.scss'
})
export class ClassView {
  @Input() schoolClass: SchoolClass | null = null;
  @Output() back = new EventEmitter<void>();

  activeTab: 'subjects' | 'students' = 'subjects';

  onBack(): void {
    this.back.emit();
  }

}
