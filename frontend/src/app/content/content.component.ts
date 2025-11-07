import { Component } from '@angular/core';

@Component({
  selector: 'app-content',
  templateUrl: './content.component.html',
  styleUrls: ['./content.component.css']
})
export class ContentComponent {
  componentToShow: string = "welcome";

  showComponent(componentToShow: string): void {
    this.componentToShow = componentToShow;
  }
}

