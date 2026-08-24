import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../../../core/models/api-response.model';
import { DemandeMaintenanceDTO } from '../../../core/models/maintenance.model';
import { BonTravailResumeDTO } from '../../../core/models/bon-travail.model';
import { environment } from '../../../../environments/environment';
import {
  ActiviteParParc,
  AnomalieCritique,
  AscenseurAvecEtat,
  DashboardAdminStats,
  DashboardClientStats,
  DashboardResponsableStats,
  DashboardTechnicienStats,
  DemandeSuivi,
  PlanningJour,
  ProchaineIntervention,
  RepartitionUtilisateurs,
} from '../../../core/models/dashboard.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/dashboard/responsable`;
  private readonly adminUrl = `${environment.apiUrl}/dashboard/admin`;
  private readonly technicienUrl = `${environment.apiUrl}/dashboard/technicien`;
  private readonly clientUrl = `${environment.apiUrl}/dashboard/client`;
  private readonly demandesUrl = `${environment.apiUrl}/demandes-maintenance`;
  private readonly bonsUrl = `${environment.apiUrl}/bons-travail`;

  // ── Responsable ──

  // GET /api/dashboard/responsable/stats → les 10 KPIs
  getStats(): Observable<DashboardResponsableStats> {
    return this.http
      .get<ApiResponse<DashboardResponsableStats>>(`${this.baseUrl}/stats`)
      .pipe(map((res) => res.data));
  }

  // GET /api/dashboard/responsable/anomalies-critiques → liste des anomalies critiques (7 derniers jours)
  getAnomaliesCritiques(): Observable<AnomalieCritique[]> {
    return this.http
      .get<ApiResponse<AnomalieCritique[]>>(`${this.baseUrl}/anomalies-critiques`)
      .pipe(map((res) => res.data));
  }

   // GET /api/demandes-maintenance/a-traiter → top 5 demandes EN_ATTENTE par priorité
     getDemandesAtraiter(): Observable<DemandeMaintenanceDTO[]> {
       return this.http
         .get<ApiResponse<DemandeMaintenanceDTO[]>>(`${this.demandesUrl}/a-traiter`)
         .pipe(map((res) => res.data));
    }

  // GET /api/bons-travail/aujourd-hui → interventions planifiées aujourd'hui
  getInterventionsAujourdhui(): Observable<BonTravailResumeDTO[]> {
    return this.http
      .get<ApiResponse<BonTravailResumeDTO[]>>(`${this.bonsUrl}/aujourd-hui`)
      .pipe(map((res) => res.data));
  }

  // ── Admin ──

  // GET /api/dashboard/admin/stats → structure du système + santé opérationnelle
  getStatsAdmin(): Observable<DashboardAdminStats> {
    return this.http
      .get<ApiResponse<DashboardAdminStats>>(`${this.adminUrl}/stats`)
      .pipe(map((res) => res.data));
  }

  // GET /api/dashboard/admin/repartition-utilisateurs → compteurs par rôle
  getRepartitionUtilisateurs(): Observable<RepartitionUtilisateurs> {
    return this.http
      .get<ApiResponse<RepartitionUtilisateurs>>(`${this.adminUrl}/repartition-utilisateurs`)
      .pipe(map((res) => res.data));
  }

  // GET /api/dashboard/admin/activite-par-parc → supervision par parc
  getActiviteParParc(): Observable<ActiviteParParc[]> {
    return this.http
      .get<ApiResponse<ActiviteParParc[]>>(`${this.adminUrl}/activite-par-parc`)
      .pipe(map((res) => res.data));
  }
  
  // ── Technicien ──

  // GET /api/dashboard/technicien/stats → 4 compteurs perso
  getStatsTechnicien(): Observable<DashboardTechnicienStats> {
    return this.http
      .get<ApiResponse<DashboardTechnicienStats>>(`${this.technicienUrl}/stats`)
      .pipe(map((res) => res.data));
  }

  // GET /api/dashboard/technicien/aujourd-hui → mes interventions du jour triées par heure
  getInterventionsTechnicienAujourdhui(): Observable<BonTravailResumeDTO[]> {
    return this.http
      .get<ApiResponse<BonTravailResumeDTO[]>>(`${this.technicienUrl}/aujourd-hui`)
      .pipe(map((res) => res.data));
  }

  // GET /api/dashboard/technicien/prochaine → prochaine intervention + contexte
  getProchaineIntervention(): Observable<ProchaineIntervention> {
    return this.http
      .get<ApiResponse<ProchaineIntervention>>(`${this.technicienUrl}/prochaine`)
      .pipe(map((res) => res.data));
  }

  // GET /api/dashboard/technicien/planning-semaine → nb d'interventions par jour (lun→dim)
  getPlanningSemaineTechnicien(): Observable<PlanningJour[]> {
    return this.http
      .get<ApiResponse<PlanningJour[]>>(`${this.technicienUrl}/planning-semaine`)
      .pipe(map((res) => res.data));
  }
  
  // ── Client ──

  // GET /api/dashboard/client/stats → compteurs du client
  getStatsClient(): Observable<DashboardClientStats> {
    return this.http
      .get<ApiResponse<DashboardClientStats>>(`${this.clientUrl}/stats`)
      .pipe(map((res) => res.data));
  }

  // GET /api/dashboard/client/mes-ascenseurs-etat → mes ascenseurs + état de leur demande
  getAscenseursAvecEtat(): Observable<AscenseurAvecEtat[]> {
    return this.http
      .get<ApiResponse<AscenseurAvecEtat[]>>(`${this.clientUrl}/mes-ascenseurs-etat`)
      .pipe(map((res) => res.data));
  }

  // GET /api/dashboard/client/suivi → mes demandes actives avec technicien assigné
  getSuiviClient(): Observable<DemandeSuivi[]> {
    return this.http
      .get<ApiResponse<DemandeSuivi[]>>(`${this.clientUrl}/suivi`)
      .pipe(map((res) => res.data));
  }
}
