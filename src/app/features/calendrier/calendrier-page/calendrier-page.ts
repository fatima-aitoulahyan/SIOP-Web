import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FullCalendarModule } from '@fullcalendar/angular';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin, { DateClickArg } from '@fullcalendar/interaction';
import { CalendarOptions, EventClickArg, EventInput } from '@fullcalendar/core';

import { CalendrierService } from '../services/calendrier.service';
import { EvenementForm } from '../evenement-form/evenement-form';
import { CalendrierEventDTO } from '../models/calendrier-event.model';
import { UtilisateurResponseDTO } from '../../../core/models/utilisateur.model';
import { UtilisateurService } from '../../utilisateurs/services/utilisateur';

@Component({
  selector: 'app-calendrier-page',
  standalone: true,
  imports: [CommonModule, FullCalendarModule, EvenementForm],
  templateUrl: './calendrier-page.html',
  styleUrl: './calendrier-page.scss',
})
export class CalendrierPage implements OnInit {
  private calendrierService = inject(CalendrierService);
  private utilisateurService = inject(UtilisateurService);

  evenements = signal<CalendrierEventDTO[]>([]);
  technicienFiltre = signal<number | null>(null);
  techniciensListe = signal<UtilisateurResponseDTO[]>([]);

  modalOuverte = signal(false);
  dateSelectionnee = signal<string | null>(null);
  evenementEnEdition = signal<CalendrierEventDTO | null>(null);

  calendarEvents = computed<EventInput[]>(() =>
    this.evenements()
      .filter(
        (e) =>
          this.technicienFiltre() === null || e.technicienIds.includes(this.technicienFiltre()!),
      )
      .map((e) => ({
        id: e.id,
        title: e.titre,
        start: e.debut,
        end: e.fin,
        backgroundColor: e.couleur,
        borderColor: e.couleur,
        extendedProps: { source: e.source, type: e.type, lieu: e.lieu },
      })),
  );

  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
    initialView: 'timeGridWeek',
    locale: 'fr',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay',
    },
    firstDay: 1,
    height: 'auto',
    selectable: true,
    editable: false,
    events: [],
    dateClick: (arg: DateClickArg) => this.onDateClick(arg),
    eventClick: (arg: EventClickArg) => this.onEventClick(arg),
  };

  ngOnInit(): void {
    this.chargerEvenements();
    this.chargerTechniciens();
  }

  private chargerEvenements(): void {
    const debut = this.borneDebut();
    const fin = this.borneFin();

    this.calendrierService.getEvenementsCalendrier(debut, fin, this.technicienFiltre()).subscribe({
      next: (events) => {
        this.evenements.set(events);
        this.calendarOptions = { ...this.calendarOptions, events: this.calendarEvents() };
      },
      error: (err) => console.error('Erreur lors du chargement du calendrier', err),
    });
  }

  private chargerTechniciens(): void {
    this.utilisateurService.getTechniciens().subscribe({
      next: (data) => this.techniciensListe.set(data),
      error: (err) => console.error('Erreur lors du chargement des techniciens', err),
    });
  }

  private borneDebut(): string {
    const d = new Date();
    d.setDate(d.getDate() - 30);
    return d.toISOString();
  }

  private borneFin(): string {
    const d = new Date();
    d.setDate(d.getDate() + 30);
    return d.toISOString();
  }

  onDateClick(arg: DateClickArg): void {
    this.dateSelectionnee.set(arg.dateStr);
    this.evenementEnEdition.set(null);
    this.modalOuverte.set(true);
  }

  ouvrirNouvelEvenement(): void {
    this.dateSelectionnee.set(null);
    this.evenementEnEdition.set(null);
    this.modalOuverte.set(true);
  }

  onEventClick(arg: EventClickArg): void {
    if (arg.event.extendedProps['source'] !== 'EVENEMENT') {
      return;
    }
    const evt = this.evenements().find((e) => e.id === arg.event.id) ?? null;
    this.evenementEnEdition.set(evt);
    this.dateSelectionnee.set(null);
    this.modalOuverte.set(true);
  }

  fermerModal(): void {
    this.modalOuverte.set(false);
  }

  surEvenementEnregistre(): void {
    this.fermerModal();
    this.chargerEvenements();
  }

  supprimerEvenement(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet événement ?')) {
      this.calendrierService.supprimerEvenement(id).subscribe({
        next: () => {
          this.fermerModal();
          this.chargerEvenements();
        },
        error: (err) => console.error('Erreur lors de la suppression de l’événement', err),
      });
    }
  }

  changerFiltreTechnicien(technicienId: number | null): void {
    this.technicienFiltre.set(technicienId);
    this.chargerEvenements();
  }

  onChangerFiltre(event: any): void {
    const valeur = event.target.value;
    const id = valeur === 'null' ? null : Number(valeur);
    this.changerFiltreTechnicien(id);
  }
}
