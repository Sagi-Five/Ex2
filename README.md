# README: Spreadsheet Project

## Overview
This project is a simple spreadsheet system
that allows users to manage cells, perform calculations using formulas,
and save or load data. 
It provides both a programmatic interface and a graphical user interface (GUI) for interacting with the spreadsheet.

---

## Features
- **Spreadsheet Management**:
  - Create and manage a spreadsheet with customizable dimensions.
  - Access cells by coordinates or references (e.g., `A1`, `B2`).
- **Cell Functionality**:
  - Store and retrieve text, numeric values, or formulas.
  - Automatically detect and compute formula values.
- **Dependency Resolution**:
  - Handle dependencies between cells.
  - Detect and flag circular dependencies.
- **Persistence**:
  - Save spreadsheets to a file.
  - Load data back into the spreadsheet from a file.
- **Graphical Interface**:
  - Includes a GUI for interacting with the spreadsheet.
  - Visual representation of cell data and dependencies.

<img width="1189" alt="image" src="https://github.com/user-attachments/assets/f4fba3a1-4b18-48b1-b978-7c08419e0aff" />


---

## Project Structure
### Core Classes
1. **`SCell`**
   - Represents an individual cell.
   - Handles raw data storage, type detection, and formula computation.

2. **`Ex2Sheet`**
   - Manages the spreadsheet as a 2D grid of cells.
   - Supports formula evaluation, dependency resolution, and data persistence.

3. **`CellEntry`**
   - Provides utility methods for parsing and managing cell entries.

4. **`Index2D`**
   - Facilitates mapping between cell references (e.g., `A1`) and grid coordinates.

5. **`Ex2Utils`**
   - Contains constants and utility functions for the project.
