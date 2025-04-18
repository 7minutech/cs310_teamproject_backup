# Time and Attendance System – CS 310 Team Project (Spring 2025)

This project is part of the **Software Engineering I** course at Jacksonville State University. It focuses on building a **Time and Attendance System** for a local manufacturing company with ~150 hourly employees.

## 📌 Project Scope

We are developing a **Java-based library** that handles:
- Core business logic
- Data access via DAOs

This library will later support:
- Physical clock terminal controls
- A web-based time/attendance interface

For now, our focus is purely on the **backend logic and data layer** using best practices outlined in *Java Database Programming, Part 2*.

## ⚠️ Problems with the Current System

- Only one time clock → long lines at start/end of shifts
- Policies on tardiness and absences are enforced manually
- Managers can’t easily view attendance history or trends

## 💡 Our Solution

The new system will:
- Support **multiple terminals** linked to a central server
- **Auto-adjust** punches based on policy (e.g., grace periods, docking)
- Track and compute total hours for payroll
- Enforce configurable rules like:
  - **Interval**: max deviation from scheduled time
  - **Grace Period**: short buffer for lateness
  - **Dock Time**: penalty for excessive lateness
  - **Lunch deductions**

The system handles edge cases when punches fall before or after a scheduled shift.

## 🗃️ Data Layer

- Designed using **Java model objects** and **DAO pattern**
- Abstracts database implementation
- Includes a sample MySQL database with real punch records

## 🛠️ Technical Info

- Use `java.time` (Java 8+): `LocalDate`, `LocalTime`, `LocalDateTime`
- Avoid legacy `Date`, `Calendar`, or `Timestamp` classes
- No time zone support needed (local server time is sufficient)

## 🗂️ Setup

- Sample database and schema available on Canvas:
  - Go to **Team Project** → “Preliminary Setup Steps”

---

🚧 *Note: This is the foundational backend module only. Frontend and hardware integration will follow in future phases.*
