import { Component, signal } from '@angular/core';
//import { RouterOutlet } from '@angular/router';
import { from } from 'rxjs';
import { Note } from "./models/note.model";
import { CommonModule } from '@angular/common';
import { Header } from './header/header';
import { Footer } from './footer/footer';
import { FormsModule } from "@angular/forms";

@Component({
  selector: 'app-root',
  imports: [CommonModule, Header, Footer, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {

  handleSubscribe(){
    console.log("work");
    
  }

  showAlert(){
   
  }

  title = "hello my web"


  protected readonly notes = signal<Note[]>([
    {
      id: 1,
      title: "pervaya",
      content: "sosi",
      createdAt: new Date()
    },
    {
      id: 2,
      title: "pervaya",
      content: "sosi",
      createdAt: new Date()
    }
  ])
}
