import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { interval, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-track-order',
  templateUrl: './track-order.component.html',
  styleUrls: ['./track-order.component.css'],
})
export class TrackOrderComponent implements OnInit, OnDestroy {
  order: any = null;
  loading: boolean = true;
  errorMessage: string = '';
  private pollingSubscription!: Subscription;

  constructor(private route: ActivatedRoute, private orderService: OrderService) { }

  ngOnInit(): void {
    const orderId = this.route.snapshot.paramMap.get('id');
    if (orderId) {
      this.loadOrder(+orderId);
      this.startPolling(+orderId);
    } else {
      this.errorMessage = 'ID porudžbine nije validan.';
      this.loading = false;
    }
  }

  private loadOrder(orderId: number): void {
    this.orderService.trackOrder(orderId).subscribe({
      next: (data) => {
        this.order = {
          ...data,
          formattedDishes: data.dishes?.map((dish: any) => dish.name).join(', ') || 'N/A',
        };
        this.loading = false;
        if (this.order.status === 'DELIVERED') {
          this.stopPolling();
        }
      },
      error: (error) => {
        this.errorMessage = 'Greška pri učitavanju porudžbine.';
        this.loading = false;
        console.error('Error loading order:', error);
      },
    });
  }

  private startPolling(orderId: number): void {
    this.pollingSubscription = interval(1000)
      .pipe(switchMap(() => this.orderService.trackOrder(orderId)))
      .subscribe({
        next: (data) => {
          this.order = {
            ...data,
            formattedDishes: data.dishes?.map((dish: any) => dish.name).join(', ') || 'N/A',
          };
          if (this.order.status === 'DELIVERED') {
            this.stopPolling();
          }
        },
        error: (error) => {
          this.errorMessage = 'Greška pri osvežavanju porudžbine.';
          console.error('Error polling order:', error);
        },
      });
  }

  private stopPolling(): void {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
      this.pollingSubscription = null!;
    }
  }

  ngOnDestroy(): void {
    this.stopPolling();
  }
}
