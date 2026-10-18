import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Term } from '../../terms/term';

type Tab = 'student' | 'attendance' | 'academic' | 'financial';

@Component({
  selector: 'app-reports-page',
  standalone: false,
  templateUrl: './reports-page.html',
  styleUrl: './reports-page.scss'
})
export class ReportsPage implements OnInit {
  terms: Term[] = [];
  selectedTermId: number | null = null;

  activeTab: Tab = 'student';

  studentReport: any[] = [];
  attendanceReport: any[] = [];
  academicReport: any[] = [];
  financialReport: any[] = [];

  loading = false;

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.backend.get<Term[]>('/terms').subscribe({
      next: (terms) => {
        this.terms = terms;
        this.selectedTermId = terms[0]?.id ?? null;
        this.loadAll();
      }
    });
  }

  setTab(tab: Tab): void {
    this.activeTab = tab;
  }

  loadAll(): void {
    this.loading = true;

    this.backend.get<any[]>('/reports/student').subscribe(res => this.studentReport = res);

    if (this.selectedTermId) {
      this.backend.get<any[]>(`/reports/attendance?termId=${this.selectedTermId}`).subscribe(res => this.attendanceReport = res);
      this.backend.get<any[]>(`/reports/academic?termId=${this.selectedTermId}`).subscribe(res => this.academicReport = res);
      this.backend.get<any[]>(`/reports/financial?termId=${this.selectedTermId}`).subscribe(res => {
        this.financialReport = res;
        this.loading = false;
      });
    } else {
      this.loading = false;
    }
  }

  onTermChange(): void {
    this.loadAll();
  }

  downloadCsv(): void {
    let data: any[] = [];
    let headers: string[] = [];
    const type = this.activeTab;

    if (type === 'student') {
      data = this.studentReport;
      headers = ['Class Name', 'Total Students'];
    } else if (type === 'attendance') {
      data = this.attendanceReport;
      headers = ['Class Name', 'Total Records', 'Present %', 'Absent %', 'Late %'];
    } else if (type === 'academic') {
      data = this.academicReport;
      headers = ['Subject Name', 'Total Students', 'Average Mark', 'Pass Rate %', 'Highest Mark'];
    } else if (type === 'financial') {
      data = this.financialReport;
      headers = ['Class Name', 'Expected Fees', 'Collected Fees', 'Outstanding Fees'];
    }

    if (data.length === 0) return;

    const csv = [headers.join(',')];
    for (const row of data) {
      let line: string[] = [];
      if (type === 'student') line = [row.className, row.totalStudents];
      if (type === 'attendance') line = [row.className, row.totalRecords, row.presentPercent, row.absentPercent, row.latePercent];
      if (type === 'academic') line = [row.subjectName, row.totalStudents, row.averageMark, row.passRate, row.highestMark];
      if (type === 'financial') line = [row.className, row.expectedFees, row.collectedFees, row.outstandingFees];

      csv.push(line.join(','));
    }

    const blob = new Blob([csv.join('\n')], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${type}-report.csv`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }

  downloadPdf(): void {
    const type = this.activeTab;
    let url = `/reports/${type}/pdf`;
    if (type !== 'student' && this.selectedTermId) {
      url += `?termId=${this.selectedTermId}`;
    }

    this.backend.downloadBlob(url).subscribe({
      next: (blob) => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = `${type}-report.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(a.href);
      }
    });
  }
}
