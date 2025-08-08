# Camera Recommendation System

## Introduction 🔎
This was the final project for CSDS 341: Introduction to Database Systems, taught by Dianne Foreback at Case Western Reserve University Spring semester of 2024. The scope of this project lasted from after the midterm exam to the end of the semester. Its premise was to create a database using SQL commands utilizing CWRU's access to Microsoft SQL Server with the knowledge of both SQL programming and relational algebra that was taught in the course. This was a group project that was ideated by Darin Hall, who is also a photographer (hence the theme :smile:).

## Contents 📲
This repository hosted on GitHub is solely Java in order to ensure consistency throughout our code for our team members. The actual program was run on a virtual desktop through Citrix Workspace that ran on a Microsoft OS. We then used Microsoft SQL Server to host the SQL database and ran the program through our IDEs on the virtual desktop.

This project was created to solve the question that photographers get all too often: "What camera should I buy?" We first populated the SQL databse with various information: camera brands, bodies, lenses, and other various attributes. The database itself included around 5-10 combinations of various attributes per brand, indicating an accurate sample size of its choices and offerings. 

The backend databsae was then queried through JDBC (Java Database Connector), adding in logic and computing power behind the SQL queries themselves. The recommendation system ran through the terminal, asking questions or clarification statements to narrow down the search such as "You are a customer and would like to find a camera based on your preferences" or "You are looking for a camera and would like to see the cheapest and most expensive camera from every brand." This served the purpose of demonstrating the ability to query throughout the databse.

Other various questions demonstrated the ability to manipulate the contents of the database or add in new information and data, allowing the database itself to be scalable, for example: "You are a company that would like to reduce the prices of your catalog by a specified amount for a sale" or "You are a company that would like to add a new product to the market."

The questionaire would accomodate to the level of expertise of the user, allowing for a more "step-by-step" process to help decide if the user was less familiar with photography nomenclature. If the user selects that they are advanced or understand photography terminology, the questions became more specific to help the specific need of the photographer. 

The result of the questionaire in the terminal would provide either two things: 
1. Various recommendations based on the answers to the question
2. Confirmation message regarding changes made to the database

## Contributors 👥
Darin Hall, Jeremy Bullis, and Dan Tayman