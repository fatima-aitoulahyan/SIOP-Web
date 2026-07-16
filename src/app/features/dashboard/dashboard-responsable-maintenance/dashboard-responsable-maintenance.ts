import { Component, inject, OnInit, computed } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AscenseurService } from '../../ascenseurs/services/ascenseur.service';
import { AuthService } from '../../../core/auth/auth';
import { Router } from '@angular/router';


@Component({
  selector: 'app-dashboard-responsable-maintenance',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dashboard-responsable-maintenance.html',
  styleUrl: './dashboard-responsable-maintenance.scss',
})
export class DashboardResponsableMaintenance implements OnInit {
  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }
}
