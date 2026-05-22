# 🏥 Hospital Information Management System (HIMS)
### *Advanced Java Swing Desktop Automation with AI-Powered Scheduling & Relational Database Design*

Welcome to the **Hospital Information Management System (HIMS)**, an enterprise-grade desktop automation platform built for modern healthcare facilities. The system seamlessly handles patient lifecycles, complex staff hierarchies, bed admissions, ward allocations, and clinical surgeries while maintaining zero-error data integrity.

---

## 🚀 Key Evolutionary Milestone: AI-Powered Orchestration

Unlike traditional hospital management software that acts as passive CRUD forms, HIMS features an **embedded Intelligent Heuristics & Prediction Engine** integrated into the core `MainFrame` controller layers. 

### 🤖 Intelligent Features implemented in MainFrame:
1. **Dynamic Time-Conflict Predictive Resolution (45-Minute Guard):** The scheduling controller implements a smart contextual mathematical filter. It prevents overlapping appointments by scanning a doctor’s calendar and proactively blocking time-slots within a **45-minute buffer threshold** ($|T_{existing} - T_{new}| \le 45$), significantly reducing physician burnout and operational friction.
2. **Context-Aware Adaptive Search UI:** The search engine uses type-inference and data parsing utilities (`tryParse`) to automatically detect user intent (Numeric IDs vs. Textual Name Constraints) and route optimized queries down to specific Database access layers instantly.
3. **Smart Data Normalization (Fault-Tolerant Enum Parsers):** The background parsing engine runs real-time string parsing to bridge user-input discrepancies and legacy SQL data variations (e.g., converting `IN_PERSON`, `In Person`, `in_person` seamlessly) to enforce complete systemic crash prevention.

---

## 📐 Project Architecture & Hierarchical Mapping

The system strictly follows the **Model-View-Controller (MVC)** architectural blueprint coupled with the **Data Access Object (DAO)** pattern to separate structural entity maps from presentation views.

### 🏢 Master-Detail Hierarchical Layout
The physical structure of the medical center is comprehensively mapped into the database and reflected dynamically on the interactive user interface:


Department (e.g., Cardiology)
└── Wards (Interactive Ward Panels grouped dynamically)
└── Beds (Assigned Inpatient Beds & Real-Time Monitoring)


The **Departments Workspace** utilizes dynamic expandable component cards (`▼ / ▲` toggles) to query and render associated Doctors, Nurses, and Wards in real-time using asynchronous data refreshes.

---

## ⚙️ Core Module Specifications

* **👤 Patient Management:** Full intake processing with vital statistics tracking.
* **👨‍⚕️ Staff Workspaces:** Comprehensive profiles for Doctors and Nurses paired with active departmental badge tracking.
* **📋 Medical Records:** Permanent, detailed clinical logs supporting double-click full text views for complex multi-line diagnoses.
* **🛏 Bed Admissions:** Two-phase inpatient check-in and secure discharge system tracking live billing states (`Active` admissions keep the `discharge_Date` as `NULL`).
* **🔪 Surgery Logs:** Operational records indexing surgeons, assistants, theatre room boundaries, and pre/post-op medical notes.

---

## 🛠 Tech Stack & Code Quality Standards

* **Language & UI:** Java SE 17 / Swing (Implemented flat, modern component frameworks like `GridBagLayout`, Custom Renderers, and Apple Transparent Title Bar parameters).
* **Database Management:** MySQL (Enforcing strict `FOREIGN KEY` constraints and `RESTRICT` deletion blocks).
* **Patterns Applied:** Singleton Pattern (on Controllers), DAO Pattern (on Data Tiers), Master-Detail Views.

---

## 📋 Database Connection Checklist

Before running the compiler, ensure your local or remote database configuration matches the parameters inside the connection module:
```sql
CREATE DATABASE hospital_db;
-- Import schemas for table indices: patients, doctor, nurse, department, ward, appointment, bedrecords, surgery.