# 🏥 Hospital Management System v1.0  
### Paradise Hospital — Java Swing + OOP Architecture  


![Java](https://img.shields.io/badge/Java-11%2B-orange)  
![GUI](https://img.shields.io/badge/GUI-Swing-blue)  
![OOP](https://img.shields.io/badge/Concepts-OOP-success)  
![Status](https://img.shields.io/badge/Project-Completed-brightgreen)  
![License](https://img.shields.io/badge/License-MIT-lightgrey)  

---

## 📌 Overview  

A **desktop-based Hospital Management System** built with **Java (JDK 11+)** and **Swing GUI**, designed to demonstrate **clean architecture and Object-Oriented Programming (OOP)** principles.  

This system helps manage:  
- 👨‍⚕️ Doctors  
- 👩‍⚕️ Nurses  
- 🧑‍🤝‍🧑 Patients  
- 🏥 Rooms & Wards  
- 📅 Appointments  
Real-time dashboard updates
---

## ✨ Features  

- 🖥️ Interactive **Swing GUI**
- 🧱 Clean **modular architecture**
- 🔄 Real-time dashboard updates  
- ✅ Input validation system  
- 📊 Live hospital statistics  
- 🧩 Strong OOP implementation  

---

## 📸 Screenshots  

> 📌 Add your screenshots inside a `/screenshots` folder in your repo  

### 🏠 Dashboard  
<img width="1917" height="1007" alt="Screenshot 2026-07-28 121304" src="https://github.com/user-attachments/assets/9efa74d5-ff95-4ea3-ad86-641eef4c3755" />
### 👨‍⚕️ Doctor Management  
<img width="1917" height="1006" alt="Screenshot 2026-07-28 121321" src="https://github.com/user-attachments/assets/d8a4f6af-5714-408f-b4ae-100d143b4279" />
### Nurse Management
<img width="1917" height="1031" alt="Screenshot 2026-07-28 121339" src="https://github.com/user-attachments/assets/d46e55a1-ad61-4111-9fcc-9d8db402fb01" />
### Patient Management
<img width="1917" height="1007" alt="Screenshot 2026-07-28 121401" src="https://github.com/user-attachments/assets/aa4977a7-31f8-4a02-8f98-a6981bd7978a" />
### 🏥 Ward View
<img width="1916" height="1006" alt="Screenshot 2026-07-28 121500" src="https://github.com/user-attachments/assets/462e3902-82b1-427c-82c4-582c230aee6e" />
### 📅 Appointment System  
<img width="1916" height="1022" alt="Screenshot 2026-07-28 121414" src="https://github.com/user-attachments/assets/cf87dd4a-aa99-4bd0-a800-5229b00a15fc" />


---

## 🗂️ Project Structure  

```bash
src/
├── Main.java                         # Application entry point
│
├── model/                            # Core domain models
│   ├── PersonDetails.java            # Abstract base class
│   ├── Address.java                  # Immutable value object
│   ├── Doctor.java                   # Doctor entity
│   ├── Nurse.java                    # Nurse entity
│   └── Patient.java                  # Patient entity
│
├── building/                         # Hospital infrastructure
│   ├── Building.java                 # Main container
│   ├── Room.java                     # Room representation
│   └── Ward.java                     # Ward management
│
├── service/                          # Business logic layer
│   ├── HospitalService.java          # Interface (abstraction)
│   └── ManagementSystem.java         # Implementation
│
├── ui/                               # Graphical User Interface
│   └── HospitalGUI.java              # Swing-based UI (MVC View)
│
└── util/                             # Utility classes
    └── Validator.java                # Input validation
```

## 🧠 OOP Concepts Used  

- 🔹 **Abstraction** → Interfaces & abstract classes  
- 🔹 **Encapsulation** → Private fields + validation  
- 🔹 **Inheritance** → Shared base class (`PersonDetails`)  
- 🔹 **Polymorphism** → Method overriding  

---


## 🧪 Example Workflow  

1. Setup building (rooms & wards)  
2. Add doctors and nurses  
3. Register patients  
4. Assign wards  
5. Book appointments  
6. Monitor dashboard  
7. Assign staff

---
🎯 Purpose of the Project

This project was developed as a learning-focused software system to:

Practice OOP concepts in Java
Build a GUI-based real-world application
Understand software architecture and design patterns
Improve problem-solving and coding skills

---
## 🚀 Future Improvements  

- 🔐 User authentication system  
- 🗄️ Database integration (MySQL)  
- 🌐 Web version (Spring Boot + React)  
- 📈 Advanced analytics dashboard  

---

## 👨‍💻 Author-> MD Sakib Hasan CSE KU  

**Paradise Hospital Project**  
Built for learning **Java, GUI, and OOP concepts**  

